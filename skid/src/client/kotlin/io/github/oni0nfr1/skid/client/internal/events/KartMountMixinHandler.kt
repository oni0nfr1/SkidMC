package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.SkidClient
import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skid.client.api.events.KartSummonEvents
import io.github.oni0nfr1.skid.client.api.kart.KartSaddleEntity
import io.github.oni0nfr1.skid.client.api.kart.MountType
import io.github.oni0nfr1.skid.client.api.kart.mountStatus
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.player.RemotePlayer
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

internal object KartMountMixinHandler {
    private val client: Minecraft by MCClient

    /**
     * 서버가 선언한 passenger 중 SkidMC가 실제 엔티티를 확인하고 처리한 관계입니다.
     *
     * INVARIANT:
     * - 저장된 saddle과 rider ID는 현재 client level에서 각각 KartSaddleEntity와 Player로 확인된 적이 있다.
     * - 저장되기 전에 해당 관계의 MOUNT_EARLY 호출이 정상적으로 완료됐다.
     * - 하나의 rider ID는 최대 하나의 saddle ID에만 속한다.
     * - 제거된 엔티티와 이전 client level의 ID는 남아 있지 않는다.
     *
     * THREADING:
     * - 렌더 스레드에서만 접근하고 변경한다.
     *
     * RECOVERY:
     * - 처리된 관계의 엔티티를 찾지 못하면 WARN을 기록하고 이벤트 없이 내부 관계를 제거한다.
     */
    private val handledPassengerIdsByKartId = mutableMapOf<Int, MutableSet<Int>>()
    private var wasSpectating = false

    /** 탑승 관계 lifecycle 콜백을 등록하고 이전 로컬 상태를 초기화합니다. */
    fun init() {
        clearLocalState()
        ClientPlayConnectionEvents.INIT.register { _, _ -> clearLocalState() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> teardownTrackedKarts() }
    }

    /**
     * [ClientboundSetPassengersPacket]을 수신한 뒤
     * [ClientPacketListener.handleSetEntityPassengersPacket]의 TAIL에 붙은 믹스인에서 호출됩니다.
     *
     * REQUIRES:
     * - Vanilla의 passenger 처리가 끝난 TAIL 시점에 호출된다.
     *
     * ENSURES:
     * - 정상 종료 시 실제 Player가 확인되고 MOUNT_EARLY가 완료된 관계만 handled 상태에 포함된다.
     * - 패킷에서 제거된 관계는 handled 상태와 KartManager에서 제거된다.
     *
     * THREADING:
     * - 렌더 스레드에서 호출된다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleSetEntityPassengersPacket
     */
    @JvmStatic
    fun onEntityMountPacket(
        packet: ClientboundSetPassengersPacket,
        @Suppress("UNUSED") ci: CallbackInfo,
    ) {
        val level = client.level ?: return
        val kart = level.getEntity(packet.vehicle) as? KartSaddleEntity
        if (kart == null) {
            val handledRiderIds = handledPassengerIdsByKartId.remove(packet.vehicle).orEmpty()
            val wasTracked = packet.vehicle in KartManager.getTrackedSaddleIds()
            if (handledRiderIds.isNotEmpty() || wasTracked) {
                SkidClient.LOGGER.warn(
                    "Handled kart saddle is missing during passenger update: saddleId={}, riderIds={}; " +
                        "cleaning the kart and relations without entity-based events",
                    packet.vehicle,
                    handledRiderIds,
                )
                handledRiderIds.forEach { KartManager.dismountRider(it) }
                KartManager.removeKart(packet.vehicle)
            }
            return
        }
        val kartId = kart.id

        val newRiderIds = packet.passengers.toSet()
        val oldRiderIds = handledPassengerIdsByKartId[kartId]?.toSet().orEmpty()

        (oldRiderIds - newRiderIds).forEach { riderId ->
            dismountAndForget(kart, riderId)
        }

        newRiderIds.forEach { riderId ->
            val rider = level.getEntity(riderId) as? Player
            if (rider == null) {
                if (riderId in oldRiderIds) {
                    SkidClient.LOGGER.warn(
                        "Handled kart passenger is missing during passenger update: " +
                            "saddleId={}, riderId={}; cleaning the stale relation",
                        kartId,
                        riderId,
                    )
                    removeHandledPassenger(kartId, riderId)
                    KartManager.dismountRider(riderId)
                }
                return@forEach
            }
            if (rider.vehicle !== kart) {
                SkidClient.LOGGER.warn(
                    "Kart passenger update was not applied to the client entity relation: " +
                        "saddleId={}, riderId={}; leaving the relation pending",
                    kartId,
                    riderId,
                )
                removeHandledPassenger(kartId, riderId)
                KartManager.dismountRider(riderId)
                return@forEach
            }
            handleMountEarly(kart, rider)
            completeMountIfReady(kart, rider)
        }
    }

    /**
     * 실제 엔티티가 확인된 탑승 관계에 MOUNT_EARLY를 한 번만 발행합니다.
     *
     * REQUIRES:
     * - [kart]와 [rider]는 현재 client level에 존재한다.
     *
     * ENSURES:
     * - 정상 종료 시 관계가 handled 상태에 존재한다.
     * - 다른 saddle에 남아 있던 동일 rider 관계는 제거된다.
     */
    private fun handleMountEarly(kart: KartSaddleEntity, rider: Player) {
        val kartId = kart.id
        val riderId = rider.id
        if (riderId in handledPassengerIdsByKartId[kartId].orEmpty()) return

        val previousKartIds = handledPassengerIdsByKartId
            .filterValues { riderId in it }
            .keys
            .filter { it != kartId }

        previousKartIds.forEach { previousKartId ->
            SkidClient.LOGGER.warn(
                "Kart passenger was handled by multiple saddles: previousSaddleId={}, " +
                    "newSaddleId={}, riderId={}; removing the previous relation",
                previousKartId,
                kartId,
                riderId,
            )
            val previousKart = client.level?.getEntity(previousKartId) as? KartSaddleEntity
            if (previousKart != null) {
                dismountAndForget(previousKart, riderId)
            } else {
                removeHandledPassenger(previousKartId, riderId)
                KartManager.dismountRider(riderId)
            }
        }

        KartMountEvents.MOUNT_EARLY.invoker().onKartMount(kart, rider)
        handledPassengerIdsByKartId.getOrPut(kartId) { mutableSetOf() }.add(riderId)
    }

    /**
     * 준비된 카트에 rider 관계를 반영하고 MOUNT를 한 번만 발행합니다.
     *
     * REQUIRES:
     * - [kart]와 [rider]는 현재 client level에 존재한다.
     * - 해당 관계의 MOUNT_EARLY 처리가 완료됐다.
     *
     * ENSURES:
     * - 카트가 ready이면 관계가 KartManager에 등록된다.
     * - 카트가 pending이거나 이미 등록된 관계이면 MOUNT를 발행하지 않는다.
     */
    private fun completeMountIfReady(kart: KartSaddleEntity, rider: Player) {
        if (!KartManager.isReady(kart.id)) return

        val mounted = KartManager.mountRider(rider.id, kart.id)
        if (mounted) KartMountEvents.MOUNT.invoker().onKartMount(kart, rider)
    }

    /**
     * [ClientboundRemoveEntitiesPacket]에 따라 엔티티가 제거되기 직전에 호출됩니다.
     *
     * ENSURES:
     * - 정상 종료 시 [entityId]는 handled 또는 active 탑승 관계에 남지 않는다.
     *
     * THREADING:
     * - 렌더 스레드에서 호출된다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleRemoveEntitiesPacket
     */
    @JvmStatic
    fun beforeEntityRemoveByPacket(
        entityId: Int,
        @Suppress("UNUSED") ci: CallbackInfo,
    ) {
        val level = client.level ?: return
        when (val entity = level.getEntity(entityId)) {
            is KartSaddleEntity -> removeAllPassengers(entity)
            is Player -> removePlayerRelations(level, entity)
        }
    }

    /**
     * 제거될 player가 가진 모든 handled 및 active 카트 관계를 종료합니다.
     *
     * ENSURES:
     * - 정상 종료 시 rider ID는 어느 handled set과 KartManager에도 남지 않는다.
     */
    private fun removePlayerRelations(level: ClientLevel, rider: Player) {
        val riderId = rider.id
        val kartIds = buildSet {
            (rider.vehicle as? KartSaddleEntity)?.let { add(it.id) }
            KartManager.getSaddleIdByRiderId(riderId)?.let(::add)
            handledPassengerIdsByKartId.forEach { (kartId, riderIds) ->
                if (riderId in riderIds) add(kartId)
            }
        }

        if (kartIds.size > 1) {
            SkidClient.LOGGER.warn(
                "Kart passenger is associated with multiple saddles during removal: " +
                    "saddleIds={}, riderId={}",
                kartIds,
                riderId,
            )
        }

        kartIds.forEach { kartId ->
            val kart = level.getEntity(kartId) as? KartSaddleEntity
            if (kart != null) {
                dismountAndForget(kart, riderId)
            } else {
                SkidClient.LOGGER.warn(
                    "Handled kart saddle is missing during rider removal: saddleId={}, riderId={}; " +
                        "cleaning the relation without firing DISMOUNT",
                    kartId,
                    riderId,
                )
                removeHandledPassenger(kartId, riderId)
                KartManager.dismountRider(riderId)
            }
        }

        if (kartIds.isEmpty()) KartManager.dismountRider(riderId)
    }

    /**
     * [handledPassengerIdsByKartId]의 특정 관계를 제거합니다.
     *
     * ENSURES:
     * - 정상 종료 시 [riderId]는 [kartId]의 handled set에 존재하지 않는다.
     * - set이 비면 [kartId] 키도 제거된다.
     *
     * @return 실제 handled 관계가 제거되었으면 `true`
     */
    private fun removeHandledPassenger(kartId: Int, riderId: Int): Boolean {
        val handled = handledPassengerIdsByKartId[kartId] ?: return false
        val removed = handled.remove(riderId)
        if (handled.isEmpty()) handledPassengerIdsByKartId.remove(kartId)
        return removed
    }

    /**
     * rider 객체가 있으면 DISMOUNT를 발행하고 관계를 항상 정리합니다.
     *
     * ENSURES:
     * - 이벤트 콜백의 성공 여부와 관계없이 해당 rider의 handled 및 active 관계를 제거한다.
     *
     * RECOVERY:
     * - rider 엔티티가 없으면 WARN을 기록하고 DISMOUNT 없이 관계를 정리한다.
     */
    private fun dismountAndForget(kart: KartSaddleEntity, riderId: Int) {
        val rider = client.level?.getEntity(riderId) as? Player
        try {
            if (rider == null) {
                SkidClient.LOGGER.warn(
                    "Handled kart passenger is missing during dismount: saddleId={}, riderId={}; " +
                        "cleaning the relation without firing DISMOUNT",
                    kart.id,
                    riderId,
                )
            } else {
                KartMountEvents.DISMOUNT.invoker().onKartDismount(kart, rider)
            }
        } finally {
            removeHandledPassenger(kart.id, riderId)
            KartManager.dismountRider(riderId)
        }
    }

    /** 카트에 연결된 handled 및 active rider 관계를 모두 종료합니다. */
    private fun removeAllPassengers(kart: KartSaddleEntity) {
        val riderIds = buildSet {
            addAll(handledPassengerIdsByKartId[kart.id].orEmpty())
            addAll(KartManager.getRiderIdsBySaddleId(kart.id))
        }
        riderIds.forEach { dismountAndForget(kart, it) }
        handledPassengerIdsByKartId.remove(kart.id)
    }

    /**
     * 첫 카트 어트리뷰트가 준비된 뒤 pending 카트와 탑승 관계를 완료합니다.
     *
     * ENSURES:
     * - 새로 준비된 카트에 존재하는 Player passenger는 MOUNT_EARLY 이후 MOUNT 순서로 처리된다.
     */
    @JvmStatic
    fun onFirstAttrUpdateAfterMount(entity: Entity) {
        if (entity !is KartSaddleEntity) return
        val kart = KartManager.prepareKart(entity) ?: return
        KartSummonEvents.SUMMON.invoker().onSummon(kart)

        entity.passengers.forEach { passenger ->
            if (passenger !is Player) return@forEach
            handleMountEarly(entity, passenger)
            completeMountIfReady(entity, passenger)
        }
    }

    /**
     * 이전 client level에서 추적하던 모든 탑승·관전·카트 상태를 종료합니다.
     *
     * ENSURES:
     * - SPECTATE_END, DISMOUNT, REMOVE 순서로 가능한 이벤트를 발행한다.
     * - REMOVE 콜백이 끝날 때까지 Kart는 유효하다.
     * - 종료 후 handled, pending, active 관계가 모두 비어 있다.
     *
     * RECOVERY:
     * - 필요한 엔티티가 없으면 WARN을 기록하고 가능한 이벤트만 생략한 뒤 상태를 정리한다.
     */
    @JvmStatic
    fun teardownTrackedKarts() {
        val level = client.level
        try {
            endSpectatingBeforeTeardown(level)

            val kartIds = buildSet {
                addAll(KartManager.getTrackedSaddleIds())
                addAll(handledPassengerIdsByKartId.keys)
            }

            kartIds.forEach { kartId ->
                val saddle = level?.getEntity(kartId) as? KartSaddleEntity
                if (saddle != null) {
                    removeAllPassengers(saddle)
                    KartManager.getBySaddleId(kartId)?.let { kart ->
                        KartSummonEvents.REMOVE.invoker().onRemove(kart)
                    }
                } else {
                    val riderIds = handledPassengerIdsByKartId.remove(kartId).orEmpty()
                    if (riderIds.isNotEmpty() || KartManager.getBySaddleId(kartId) != null) {
                        SkidClient.LOGGER.warn(
                            "Tracked kart saddle is missing during client level teardown: saddleId={}; " +
                                "cleaning state without DISMOUNT or REMOVE",
                            kartId,
                        )
                    }
                    riderIds.forEach { KartManager.dismountRider(it) }
                }
                KartManager.removeKart(kartId)
            }
        } finally {
            clearLocalState()
            KartManager.clear()
            TachometerManager.clear()
        }
    }

    private fun endSpectatingBeforeTeardown(level: ClientLevel?) {
        if (!wasSpectating) return

        val player = client.player
        val target = client.cameraEntity as? RemotePlayer
        val saddle = (target?.vehicle as? KartSaddleEntity)
            ?: target?.let { KartManager.getByRiderId(it.id)?.internalSaddleOrNull }

        if (player != null && target != null && saddle != null) {
            KartMountEvents.SPECTATE_END.invoker().onKartSpectateEnd(saddle, player, target)
        } else {
            SkidClient.LOGGER.warn(
                "Spectated kart relation is incomplete during client level teardown; " +
                    "skipping SPECTATE_END (hasLevel={}, hasPlayer={}, hasTarget={}, hasSaddle={})",
                level != null,
                player != null,
                target != null,
                saddle != null,
            )
        }
        wasSpectating = false
    }

    /** 이전 client level에 속한 handler 로컬 상태를 이벤트 없이 초기화합니다. */
    private fun clearLocalState() {
        handledPassengerIdsByKartId.clear()
        wasSpectating = false
    }

    /**
     * [Minecraft.setCameraEntity]가 호출된 직후, 실제 카메라 대상이 변경된 경우 호출됩니다.
     *
     * THREADING:
     * - 렌더 스레드에서 호출된다.
     */
    @JvmStatic
    fun afterSpectateTargetChange(player: Player, prevCamera: Entity, newCamera: Entity) {
        if (prevCamera === newCamera) return
        val prevSaddleEntity = prevCamera.vehicle as? KartSaddleEntity
        val newSaddleEntity = newCamera.vehicle as? KartSaddleEntity

        if (wasSpectating && prevCamera is RemotePlayer && prevSaddleEntity != null) {
            KartMountEvents.SPECTATE_END.invoker().onKartSpectateEnd(prevSaddleEntity, player, prevCamera)
            wasSpectating = false
        }

        if (player.isSpectator && newCamera is RemotePlayer && newSaddleEntity != null) {
            TachometerManager.clear()
            KartMountEvents.SPECTATE_EARLY.invoker().onKartSpectate(newSaddleEntity, player, newCamera)

            if (KartManager.isReady(newSaddleEntity.id)) {
                KartMountEvents.SPECTATE.invoker().onKartSpectate(newSaddleEntity, player, newCamera)
                wasSpectating = true
            }
        }
    }

    /** 첫 카트 어트리뷰트가 준비된 뒤 pending 관전 관계를 완료합니다. */
    @JvmStatic
    fun onFirstAttrUpdateAfterSpectate(entity: Entity) {
        val player = client.player ?: return
        val camera = client.cameraEntity as? RemotePlayer ?: return
        if (entity !is KartSaddleEntity) return
        if (camera !in entity.passengers) return

        val mountStatus = player.mountStatus
        val isSpectating = mountStatus is MountType.Spectating

        if (!wasSpectating && isSpectating) {
            val target = mountStatus.camera as? RemotePlayer ?: return

            KartMountEvents.SPECTATE.invoker().onKartSpectate(entity, player, target)
            wasSpectating = true
        }
    }
}
