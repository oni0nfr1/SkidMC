package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.SkidClient
import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
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
     * - 저장된 saddle과 rider ID는 현재 client level에서 각각 KartSaddle와 Player로 확인된 적이 있다.
     * - 저장되기 전에 해당 관계의 MOUNT_EARLY 호출이 정상적으로 완료됐다.
     * - 하나의 rider ID는 최대 하나의 saddle ID에만 속한다.
     * - 제거된 엔티티와 이전 client level의 ID는 남아 있지 않는다.
     *
     * THREADING:
     * - 렌더 스레드에서만 접근하고 변경한다.
     */
    private val handledPassengerIdsByKartId = mutableMapOf<Int, MutableSet<Int>>()

    private enum class SpectateStage {
        EARLY,
        READY,
    }

    private class SpectateRelation(
        val saddle: KartSaddle,
        val rider: Player,
        val target: RemotePlayer,
        var stage: SpectateStage,
    )

    /**
     * SPECTATE_EARLY가 완료됐고 아직 SPECTATE_END가 시작되지 않은 관전 관계입니다.
     *
     * INVARIANT:
     * - 값이 존재하면 저장된 관계에 SPECTATE_EARLY가 정확히 한 번 정상 완료됐다.
     * - [SpectateStage.READY]이면 같은 관계에 SPECTATE도 정확히 한 번 정상 완료됐다.
     * - 저장된 엔티티는 SPECTATE_END에 동일한 관계를 전달하기 위해 종료 시점까지 유지한다.
     *
     * THREADING:
     * - 렌더 스레드에서만 접근하고 변경한다.
     */
    private var spectateRelation: SpectateRelation? = null

    /** 탑승 관계 lifecycle 콜백을 등록하고 이전 로컬 상태를 초기화합니다. */
    fun init() {
        clearLocalState()
        ClientPlayConnectionEvents.INIT.register { _, _ -> clearLocalState() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> teardownTrackedKarts() }
    }

    /**
     * 새 saddle 수명 주기가 추적되기 전에 같은 ID에 남은 탑승 관계를 폐기합니다.
     *
     * ENSURES:
     * - [entity]가 KartSaddle이면 해당 ID의 handled 관계가 남아 있지 않는다.
     * - 남은 관계를 제거할 때는 DISMOUNT를 발행하지 않는다.
     */
    @JvmStatic
    fun beforeEntityTracked(entity: Entity) {
        if (entity !is KartSaddle) return

        val riderIds = handledPassengerIdsByKartId.remove(entity.id).orEmpty()
        if (riderIds.isEmpty()) return

        SkidClient.LOGGER.warn(
            "Handled kart passengers remain when a new saddle lifecycle starts: " +
                "saddleId={}, riderIds={}; discarding the relations without DISMOUNT",
            entity.id,
            riderIds,
        )
        riderIds.forEach { KartManager.dismountRider(it) }
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
     * FAILURE:
     * - 예외 전에 완료된 관계는 유지하며, 실패한 DISMOUNT 관계는 제거한다.
     * - 실패한 MOUNT의 active 관계는 유지하고 실패한 MOUNT_EARLY 관계는 기록하지 않는다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleSetEntityPassengersPacket
     */
    @JvmStatic
    fun onEntityMountPacket(
        packet: ClientboundSetPassengersPacket,
        @Suppress("UNUSED") ci: CallbackInfo,
    ) {
        val level = client.level ?: return
        val kart = level.getEntity(packet.vehicle) as? KartSaddle
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
                KartSummonMixinHandler.discardTrackedKart(packet.vehicle, "passenger update")
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
     *
     * FAILURE:
     * - MOUNT_EARLY 콜백이 예외를 던지면 새 관계를 handled 상태에 추가하지 않는다.
     * - 이후 SPECTATE_EARLY 콜백이 예외를 던져도 완료된 handled 관계는 유지한다.
     */
    private fun handleMountEarly(kart: KartSaddle, rider: Player) {
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
            val previousKart = client.level?.getEntity(previousKartId) as? KartSaddle
            if (previousKart != null) {
                dismountAndForget(previousKart, riderId)
            } else {
                removeHandledPassenger(previousKartId, riderId)
                KartManager.dismountRider(riderId)
            }
        }

        KartMountEvents.MOUNT_EARLY.invoker().onKartMount(kart, rider)
        handledPassengerIdsByKartId.getOrPut(kartId) { mutableSetOf() }.add(riderId)
        startSpectatingIfCurrent(kart, rider)
    }

    /**
     * 준비된 카트에 rider 관계를 반영하고 MOUNT를 한 번만 발행합니다.
     *
     * REQUIRES:
     * - 해당 관계의 MOUNT_EARLY 처리가 완료됐다.
     *
     * ENSURES:
     * - 카트가 ready이면 관계가 KartManager에 등록된다.
     * - 카트가 pending이거나 이미 등록된 관계이면 MOUNT를 발행하지 않는다.
     *
     * FAILURE:
     * - MOUNT 콜백이 예외를 던져도 active 관계를 유지한다.
     * - 이후 SPECTATE 콜백이 예외를 던지면 active 관계와 EARLY 관전 관계를 유지한다.
     */
    private fun completeMountIfReady(kart: KartSaddle, rider: Player) {
        if (!KartManager.isReady(kart.id)) return

        val mounted = KartManager.mountRider(rider.id, kart.id)
        if (mounted) KartMountEvents.MOUNT.invoker().onKartMount(KartRef(kart.id), rider)
        completeSpectatingIfReady(kart)
    }

    /**
     * [ClientboundRemoveEntitiesPacket]에 따라 엔티티가 제거되기 직전에 호출됩니다.
     *
     * REQUIRES:
     * - Vanilla가 해당 엔티티를 client level에서 제거하기 전에 호출된다.
     *
     * ENSURES:
     * - 정상 종료 시 [entityId]는 handled 또는 active 탑승 관계에 남지 않는다.
     *
     * FAILURE:
     * - DISMOUNT 콜백이 예외를 던져도 해당 엔티티의 탑승 관계를 모두 제거한다.
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
            is KartSaddle -> removeAllPassengers(entity)
            is Player -> {
                val riderId = entity.id
                val kartIds = buildSet {
                    (entity.vehicle as? KartSaddle)?.let { add(it.id) }
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

                try {
                    kartIds.forEach { kartId ->
                        val kart = level.getEntity(kartId) as? KartSaddle
                        if (kart != null) {
                            dismountAndForget(kart, riderId)
                        } else {
                            SkidClient.LOGGER.warn(
                                "Handled kart saddle is missing during rider removal: " +
                                    "saddleId={}, riderId={}; " +
                                    "cleaning the relation without firing DISMOUNT",
                                kartId,
                                riderId,
                            )
                            removeHandledPassenger(kartId, riderId)
                            KartManager.dismountRider(riderId)
                        }
                    }
                } finally {
                    // DISMOUNT 리스너 예외가 kartIds 순회를 중단하면, 남은 관계는 이벤트 없이 정리한다.
                    // 한 rider의 복수 saddle 관계는 불변식 위반이므로 정리를 우선하고 원래 예외를 다시 전파한다.
                    kartIds.forEach { removeHandledPassenger(it, riderId) }
                    KartManager.dismountRider(riderId)
                }
            }
        }
    }

    private fun removeHandledPassenger(kartId: Int, riderId: Int) {
        val handled = handledPassengerIdsByKartId[kartId] ?: return
        handled.remove(riderId)
        if (handled.isEmpty()) handledPassengerIdsByKartId.remove(kartId)
    }

    /**
     * rider 객체가 있으면 DISMOUNT를 발행하고 관계를 항상 정리합니다.
     *
     * ENSURES:
     * - 현재 관전 대상이면 SPECTATE_END를 DISMOUNT보다 먼저 발행한다.
     * - 해당 rider의 handled 및 active 관계를 제거한다.
     * - rider 엔티티가 없으면 WARN을 기록하고 DISMOUNT 없이 관계를 정리한다.
     *
     * FAILURE:
     * - SPECTATE_END 콜백이 예외를 던지면 DISMOUNT는 발행하지 않지만 관계는 제거한다.
     * - DISMOUNT 콜백이 예외를 던져도 관계를 제거한다.
     */
    private fun dismountAndForget(kart: KartSaddle, riderId: Int) {
        val rider = client.level?.getEntity(riderId) as? Player
        try {
            val relation = spectateRelation
            if (relation != null && relation.saddle === kart && relation.target.id == riderId) {
                endSpectating()
            }

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

    /**
     * 카트에 연결된 handled 및 active rider 관계를 모두 종료합니다.
     *
     * FAILURE:
     * - SPECTATE_END 또는 DISMOUNT 콜백이 예외를 던져도 카트의 모든 rider 관계를 제거한다.
     */
    private fun removeAllPassengers(kart: KartSaddle) {
        val riderIds = buildSet {
            addAll(handledPassengerIdsByKartId[kart.id].orEmpty())
            addAll(KartManager.getRiderIdsBySaddleId(kart.id))
        }
        try {
            if (spectateRelation?.saddle === kart) endSpectating()
            riderIds.forEach { dismountAndForget(kart, it) }
        } finally {
            handledPassengerIdsByKartId.remove(kart.id)
            riderIds.forEach { KartManager.dismountRider(it) }
        }
    }

    /**
     * 카트 어트리뷰트가 준비된 뒤 현재 passenger 관계의 처리를 완료합니다.
     *
     * ENSURES:
     * - ready 카트에 존재하는 Player passenger는 MOUNT_EARLY 이후 MOUNT 순서로 처리된다.
     *
     * FAILURE:
     * - 실패한 MOUNT의 active 관계는 유지하고 실패한 MOUNT_EARLY 관계는 기록하지 않는다.
     */
    @JvmStatic
    fun afterUpdateAttributes(entity: Entity) {
        if (entity !is KartSaddle) return
        if (!KartManager.isReady(entity.id)) return

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
     * - ready 수명 주기에서는 REMOVE 콜백이 끝날 때까지 Kart가 유효하다.
     * - 가능한 이벤트를 발행한 뒤 모든 추적 상태를 제거한다.
     *
     * FAILURE:
     * - 이벤트 콜백이 예외를 던져도 모든 추적 상태를 제거한다.
     */
    @JvmStatic
    fun teardownTrackedKarts() {
        val level = client.level
        try {
            endSpectating()

            val kartIds = buildSet {
                addAll(KartManager.getTrackedSaddleIds())
                addAll(handledPassengerIdsByKartId.keys)
            }

            kartIds.forEach { kartId ->
                val saddle = level?.getEntity(kartId) as? KartSaddle
                if (saddle != null) {
                    removeAllPassengers(saddle)
                    KartSummonMixinHandler.removeTrackedKart(saddle)
                } else {
                    val riderIds = handledPassengerIdsByKartId.remove(kartId).orEmpty()
                    if (riderIds.isNotEmpty()) {
                        SkidClient.LOGGER.warn(
                            "Handled kart passengers lost their saddle during client level teardown: " +
                                "saddleId={}, riderIds={}; cleaning relations without DISMOUNT",
                            kartId,
                            riderIds,
                        )
                    }
                    riderIds.forEach { KartManager.dismountRider(it) }
                    KartSummonMixinHandler.discardTrackedKart(kartId, "client level teardown")
                }
            }
        } finally {
            clearLocalState()
            KartManager.clear()
            TachometerManager.clear()
        }
    }

    /** 이전 client level에 속한 handler 로컬 상태를 이벤트 없이 초기화합니다. */
    private fun clearLocalState() {
        handledPassengerIdsByKartId.clear()
        spectateRelation = null
    }

    /**
     * 현재 EARLY 또는 READY 관전 관계를 공통 종료 이벤트에 연결합니다.
     *
     * ENSURES:
     * - 관계가 존재하면 같은 saddle, rider, target으로 SPECTATE_END를 한 번 발행한다.
     * - SPECTATE_END 콜백의 성공 여부와 관계없이 저장된 관전 관계를 먼저 제거한다.
     */
    private fun endSpectating() {
        val relation = spectateRelation ?: return
        spectateRelation = null
        KartMountEvents.SPECTATE_END.invoker()
            .onKartSpectateEnd(relation.saddle, relation.rider, relation.target)
    }

    /**
     * 현재 카메라가 새 카트 탑승자를 보고 있으면 EARLY 관전 관계를 시작합니다.
     *
     * ENSURES:
     * - 조건이 맞고 기존 관계가 없으면 SPECTATE_EARLY를 발행한 뒤 관계를 EARLY로 저장한다.
     *
     * FAILURE:
     * - SPECTATE_EARLY 콜백이 예외를 던지면 새 관계를 저장하지 않는다.
     */
    private fun startSpectatingIfCurrent(saddle: KartSaddle, target: Player) {
        val rider = client.player ?: return
        if (!rider.isSpectator) return
        if (target !is RemotePlayer || client.cameraEntity !== target) return
        if (target.vehicle !== saddle || target !in saddle.passengers) return
        if (spectateRelation != null) return

        TachometerManager.clear()
        KartMountEvents.SPECTATE_EARLY.invoker()
            .onKartSpectate(saddle, rider, target)
        spectateRelation = SpectateRelation(
            saddle = saddle,
            rider = rider,
            target = target,
            stage = SpectateStage.EARLY,
        )
    }

    /**
     * 현재 EARLY 관전 관계의 카트가 준비됐으면 READY 관계로 완료합니다.
     *
     * ENSURES:
     * - 저장된 관계와 현재 카메라·passenger 관계가 일치하고 카트가 ready인 경우에만
     *   SPECTATE를 발행하고 단계를 READY로 전환한다.
     *
     * FAILURE:
     * - SPECTATE 콜백이 예외를 던지면 EARLY 관계를 유지해 이후 SPECTATE_END로 정리한다.
     */
    private fun completeSpectatingIfReady(saddle: KartSaddle) {
        val relation = spectateRelation ?: return
        if (relation.stage != SpectateStage.EARLY || relation.saddle !== saddle) return
        if (client.player !== relation.rider || client.cameraEntity !== relation.target) return
        if (relation.target.vehicle !== saddle || relation.target !in saddle.passengers) return
        if (!KartManager.isReady(saddle.id)) return

        KartMountEvents.SPECTATE.invoker()
            .onKartSpectate(KartRef(saddle.id), relation.rider, relation.target)
        if (spectateRelation === relation) relation.stage = SpectateStage.READY
    }

    /**
     * [Minecraft.setCameraEntity]가 호출된 직후, 실제 카메라 대상이 변경된 경우 호출됩니다.
     *
     * ENSURES:
     * - 이전 관전 수명 주기를 종료한 뒤 확인 가능한 새 관전 수명 주기를 시작한다.
     *
     * FAILURE:
     * - SPECTATE_END 콜백이 예외를 던지면 이전 관계를 제거하고 새 관계는 시작하지 않는다.
     * - SPECTATE_EARLY 콜백이 예외를 던지면 새 관계를 기록하지 않는다.
     * - SPECTATE 콜백이 예외를 던지면 EARLY 관계를 유지해 이후 SPECTATE_END로 정리한다.
     */
    @JvmStatic
    fun afterSpectateTargetChange(player: Player, prevCamera: Entity, newCamera: Entity) {
        if (prevCamera === newCamera) return
        val newSaddleEntity = newCamera.vehicle as? KartSaddle

        endSpectating()

        if (player.isSpectator && newCamera is RemotePlayer && newSaddleEntity != null) {
            startSpectatingIfCurrent(newSaddleEntity, newCamera)
            completeSpectatingIfReady(newSaddleEntity)
        }
    }

    /**
     * 카트 어트리뷰트가 준비된 뒤 pending 관전 관계를 완료합니다.
     *
     * ENSURES:
     * - 카트가 ready이고 현재 관전 관계가 확인된 경우에만 SPECTATE를 발행한다.
     *
     * FAILURE:
     * - SPECTATE 콜백이 예외를 던지면 EARLY 관계를 유지해 이후 SPECTATE_END로 정리한다.
     */
    @JvmStatic
    fun onFirstAttrUpdateAfterSpectate(entity: Entity) {
        if (entity !is KartSaddle) return
        completeSpectatingIfReady(entity)
    }
}
