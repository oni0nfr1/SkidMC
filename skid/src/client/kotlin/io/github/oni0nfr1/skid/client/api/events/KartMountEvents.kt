package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.KartEntity
import io.github.oni0nfr1.skid.client.api.kart.KartManager
import io.github.oni0nfr1.skid.client.api.kart.MountType
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skid.client.api.kart.mountStatus
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object KartMountEvents {

    /**
     * 엔티티가 카트 엔티티에 탑승할 때 호출됩니다.
     * - 클라이언트 측의 바닐리 처리 직후 시점에 렌더 스레드에서 호출됩니다.
     *
     * ## 주의
     * 이 시점에는 카트 탑승자의 어트리뷰트 정보가 아직 적용되지 않았습니다.
     * 따라서 탑승자의 [Player.ridingKart]가 null이며 [KartEntity.kart]의 엔진 정보 또한 초기화되지 않은 상태(null)입니다.
     */
    @Suppress("UNUSED") @JvmField
    val MOUNT_EARLY = createEvent { listeners ->
        KartMountCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartMount(kartEntity, rider)
            }
        }
    }

    /**
     * 엔티티가 카트 엔티티에 탑승할 때 호출됩니다.
     * - 플레이어가 카트에 탑승한 직후 첫 어트리뷰트 갱신을 받는 시점에 호출됩니다.
     */
    @Suppress("UNUSED") @JvmField
    val MOUNT = createEvent { listeners ->
        KartMountCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartMount(kartEntity, rider)
            }
        }
    }

    /**
     * 엔티티가 카트에서 내릴 때 호출됩니다.
     * - 클라이언트 측의 바닐라 처리 직후 시점에 렌더 스레드에서 호출됩니다.
     * - 단, 카트 엔티티가 직접 사라짐으로 인해 내려지는 경우는 다른 바닐라 처리 직후, 엔티티 제거 직전에 호출됩니다.
     */
    @Suppress("UNUSED") @JvmField
    val DISMOUNT = createEvent { listeners ->
        KartDismountCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartDismount(kartEntity, rider)
            }
        }
    }

    @Suppress("UNUSED") @JvmField
    val SPECTATE_EARLY = createEvent { listeners ->
        KartSpectateCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kartEntity, rider, target)
            }
        }
    }

    @Suppress("UNUSED") @JvmField
    val SPECTATE = createEvent { listeners ->
        KartSpectateCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kartEntity, rider, target)
            }
        }
    }

    @Suppress("UNUSED") @JvmField
    val SPECTATE_END = createEvent { listeners ->
        KartSpectateEndCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectateEnd(kartEntity, rider, target)
            }
        }
    }

    fun interface KartMountCallback {
        fun onKartMount(kartEntity: KartEntity, rider: Player)
    }

    fun interface KartDismountCallback {
        fun onKartDismount(kartEntity: KartEntity, rider: Player)
    }

    fun interface KartSpectateCallback {
        fun onKartSpectate(kartEntity: KartEntity, rider: Player, target: Player)
    }

    fun interface KartSpectateEndCallback {
        fun onKartSpectateEnd(kartEntity: KartEntity, rider: Player, target: Player)
    }

    internal object MixinHandler {
        private val client: Minecraft by MCClient

        private val passengerIdsByKartId = mutableMapOf<Int, IntArray>()
        private var wasSpectating = false

        /**
         * [ClientboundSetPassengersPacket]을 수신한 뒤
         * [ClientPacketListener.handleSetEntityPassengersPacket]의 TAIL에 붙은 믹스인에서 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         *
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.onHandleSetEntityPassengersPacket
         */
        @JvmStatic
        fun onEntityMountPacket(
            packet: ClientboundSetPassengersPacket, @Suppress("UNUSED") ci: CallbackInfo
        ) {
            val level = client.level ?: return

            val kart = level.getEntity(packet.vehicle) as? KartEntity ?: return
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
                DISMOUNT.invoker().onKartDismount(kart, rider)
                KartManager.onKartDismount(kart, rider)
            }

            added.forEach { riderId ->
                val rider = level.getEntity(riderId) as? Player ?: return@forEach
                MOUNT_EARLY.invoker().onKartMount(kart, rider)
            }
        }

        /**
         * [ClientboundRemoveEntitiesPacket]을 수신한 뒤
         * [ClientPacketListener.handleRemoveEntities]의 forEach 람다에서 removeEntity가 호출되기 직전에 붙은 믹스인에서 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         *
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.onHandleRemoveEntitiesPacket
         */
        @JvmStatic
        fun beforeEntityRemoveByPacket(
            entityId: Int, @Suppress("UNUSED") ci: CallbackInfo
        ) {
            val level = client.level ?: return
            val kart = level.getEntity(entityId) as? KartEntity ?: return
            val kartId = kart.id

            val removed = passengerIdsByKartId.remove(kartId) ?: IntArray(0)

            removed.forEach { riderId ->
                val rider = level.getEntity(riderId) as? Player ?: return@forEach
                DISMOUNT.invoker().onKartDismount(kart, rider)
                KartManager.onKartDismount(kart, rider)
            }
        }

        /**
         * 어트리뷰트 갱신 패킷 수신 메서드 [ClientPacketListener.handleUpdateAttributes]가 호출된 직후에 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.afterHandleUpdateAttributes
         */
        @JvmStatic
        fun onFirstAttrUpdateAfterMount(entity: Entity) {
            if (entity !is Player) return

            val prevKart = entity.ridingKart
            val vehicle = entity.vehicle
            val wasRidingKart = prevKart?.access { alive } ?: false
            val isRidingKart = vehicle is KartEntity

            if (!wasRidingKart && isRidingKart) {
                KartManager.onKartMount(vehicle, entity)
                MOUNT.invoker().onKartMount(vehicle, entity)
            }
        }

        /**
         * [Minecraft.setCameraEntity]가 호출되기 직전에 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         * @see io.github.oni0nfr1.skid.client.mixin.MinecraftMixin.onSetCameraEntity
         */
        @JvmStatic
        fun onSpectateTargetChange(player: Player, prevCamera: Entity, newCamera: Entity) {
            if (prevCamera == newCamera) return
            val kartEntity = newCamera.vehicle as? KartEntity

            if (!player.isSpectator || newCamera !is Player || kartEntity == null) { // 카트 관전으로 넘어가는 게 아님
                if (prevCamera !is Player) return
                val kartEntity = prevCamera.vehicle as? KartEntity ?: return

                SPECTATE_END.invoker().onKartSpectateEnd(kartEntity, player, prevCamera)
                wasSpectating = false
            } else { // 카트 관전으로 넘어감
                TachometerManager.clear()
                SPECTATE_EARLY.invoker().onKartSpectate(kartEntity, player, newCamera)
            }
        }

        /**
         * 어트리뷰트 갱신 패킷 수신 메서드 [ClientPacketListener.handleUpdateAttributes]가 호출된 직후에 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.afterHandleUpdateAttributes
         */
        @JvmStatic
        fun onFirstAttrUpdateAfterSpectate(entity: Entity) {
            if (entity !is LocalPlayer || entity != client.player) return

            val mountStatus = entity.mountStatus
            val isSpectating = mountStatus is MountType.Spectating

            if (!wasSpectating && isSpectating) {
                val target = mountStatus.camera as? Player ?: return
                val kartEntity = target.vehicle as? KartEntity ?: return

                SPECTATE.invoker().onKartSpectate(kartEntity, entity, target)
                wasSpectating = true
            }
        }
    }
}