package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skid.client.api.kart.KartSaddleEntity
import io.github.oni0nfr1.skid.client.api.kart.MountType
import io.github.oni0nfr1.skid.client.api.kart.mountStatus
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

internal object KartMountMixinHandler {
    private val client: Minecraft by MCClient

    private val passengerIdsByKartId = mutableMapOf<Int, IntArray>()
    private var wasSpectating = false

    /**
     * [ClientboundSetPassengersPacket]을 수신한 뒤
     * [ClientPacketListener.handleSetEntityPassengersPacket]의 TAIL에 붙은 믹스인에서 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleSetEntityPassengersPacket
     */
    @JvmStatic
    fun onEntityMountPacket(
        packet: ClientboundSetPassengersPacket, @Suppress("UNUSED") ci: CallbackInfo
    ) {
        val level = client.level ?: return

        val kart = level.getEntity(packet.vehicle) as? KartSaddleEntity ?: return
        val kartId = kart.id

        val newRiderIds = packet.passengers
        val oldRiderIds = if (newRiderIds.isEmpty()) {
            passengerIdsByKartId.remove(kartId)
        } else {
            passengerIdsByKartId.put(kartId, newRiderIds)
        } ?: IntArray(0)

        val removed = oldRiderIds.filter { it !in newRiderIds }
        val added = newRiderIds.filter { it !in oldRiderIds }

        removed.forEach { riderId ->
            val rider = level.getEntity(riderId) as? Player ?: return@forEach
            KartMountEvents.DISMOUNT.invoker().onKartDismount(kart, rider)
            KartManager.dismountRider(rider.id)
        }

        added.forEach { riderId ->
            val rider = level.getEntity(riderId) as? Player ?: return@forEach
            KartMountEvents.MOUNT_EARLY.invoker().onKartMount(kart, rider)
        }
    }

    /**
     * [ClientboundRemoveEntitiesPacket]을 수신한 뒤
     * [ClientPacketListener.handleRemoveEntities]의 forEach 람다에서 removeEntity가 호출되기 직전에 붙은 믹스인에서 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleRemoveEntitiesPacket
     */
    @JvmStatic
    fun beforeEntityRemoveByPacket(
        entityId: Int, @Suppress("UNUSED") ci: CallbackInfo
    ) {
        val level = client.level ?: return
        when (val entity = level.getEntity(entityId)) {
            is KartSaddleEntity -> {
                val kartId = entity.id

                val removed = passengerIdsByKartId.remove(kartId) ?: IntArray(0)

                removed.forEach { riderId ->
                    val rider = level.getEntity(riderId) as? Player ?: return@forEach
                    KartMountEvents.DISMOUNT.invoker().onKartDismount(entity, rider)
                    KartManager.dismountRider(rider.id)
                }
            }
            is Player -> {
                val kart = KartManager.getByRiderId(entity.id)?.internalSaddleOrNull
                if (kart != null) KartMountEvents.DISMOUNT.invoker().onKartDismount(kart, entity)
                KartManager.dismountRider(entity.id)
            }
        }
    }

    /**
     * 어트리뷰트 갱신 패킷 수신 메서드 [ClientPacketListener.handleUpdateAttributes]가 호출된 직후에 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.afterHandleUpdateAttributes
     */
    @JvmStatic
    fun onFirstAttrUpdateAfterMount(entity: Entity) {
        if (entity !is Player) return

        val prevKart = entity.ridingKart
        val vehicle = entity.vehicle
        val wasRidingKart = prevKart?.access { alive } ?: false
        val isRidingKart = vehicle is KartSaddleEntity

        if (!wasRidingKart && isRidingKart) {
            val mounted = KartManager.mountRider(entity.id, vehicle.id)
            if (mounted) KartMountEvents.MOUNT.invoker().onKartMount(vehicle, entity)
        }
    }

    /**
     * [Minecraft.setCameraEntity]가 호출되기 직전에 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     * @see io.github.oni0nfr1.skid.client.internal.mixin.MinecraftMixin.onSetCameraEntity
     */
    @JvmStatic
    fun onSpectateTargetChange(player: Player, prevCamera: Entity, newCamera: Entity) {
        if (prevCamera == newCamera) return
        val kartSaddleEntity = newCamera.vehicle as? KartSaddleEntity

        if (!player.isSpectator || newCamera !is Player || kartSaddleEntity == null) { // 카트 관전으로 넘어가는 게 아님
            if (prevCamera !is Player) return
            val kartSaddleEntity = prevCamera.vehicle as? KartSaddleEntity ?: return

            KartMountEvents.SPECTATE_END.invoker().onKartSpectateEnd(kartSaddleEntity, player, prevCamera)
            wasSpectating = false
        } else { // 카트 관전으로 넘어감
            TachometerManager.clear()
            KartMountEvents.SPECTATE_EARLY.invoker().onKartSpectate(kartSaddleEntity, player, newCamera)
        }
    }

    /**
     * 어트리뷰트 갱신 패킷 수신 메서드 [ClientPacketListener.handleUpdateAttributes]가 호출된 직후에 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.afterHandleUpdateAttributes
     */
    @JvmStatic
    fun onFirstAttrUpdateAfterSpectate(entity: Entity) {
        if (entity !is LocalPlayer || entity != client.player) return

        val mountStatus = entity.mountStatus
        val isSpectating = mountStatus is MountType.Spectating

        if (!wasSpectating && isSpectating) {
            val target = mountStatus.camera as? Player ?: return
            val kartSaddleEntity = target.vehicle as? KartSaddleEntity ?: return

            KartMountEvents.SPECTATE.invoker().onKartSpectate(kartSaddleEntity, entity, target)
            wasSpectating = true
        }
    }
}