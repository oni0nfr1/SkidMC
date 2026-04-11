package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.KartEntity
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.kart.KartImpl
import io.github.oni0nfr1.skid.client.api.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.world.entity.Entity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object KartSummonEvents {

    /**
     * 마크라이더 카트로 추정되는 엔티티가 소환될 때 호출됩니다.
     * - 클라이언트 측 바닐라 엔티티 소환 로직이 성공한 직후에 렌더 스레드에서 호출됩니다.
     */
    @JvmField val SUMMON = createEvent<KartSummonCallback> { listeners ->
        KartSummonCallback { kart ->
            for (listener in listeners) {
                listener.onSummon(kart)
            }
        }
    }

    /**
     * 마크라이더 카트로 추정되는 엔티티가 제거될 때 호출됩니다.
     * - 클라이언트 측에서 서버에서 받은 패킷에 따라 엔티티를 월드에서 제거하기 직전에 렌더 스레드에서 호출됩니다.
     * - 이 이벤트 호출이 끝나는 즉시 [KartManager]에서 해당 카트가 제거됩니다.
     */
    @JvmField val REMOVE = createEvent<KartRemoveCallback> { listeners ->
        KartRemoveCallback { kart ->
            for (listener in listeners) {
                listener.onRemove(kart)
            }
        }
    }

    fun interface KartSummonCallback {
        fun onSummon(kart: Kart)
    }

    fun interface KartRemoveCallback {
        fun onRemove(kart: Kart)
    }

    internal object MixinHandler {
        private val client: Minecraft by MCClient

        /**
         * [ClientboundAddEntityPacket]을 수신한 뒤
         * [ClientPacketListener.handleAddEntity]에서 생성한 엔티티가 null이 아닐 경우 월드에 등록 후 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         *
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.onHandleAddEntityPacket
         */
        @JvmStatic
        fun onAddEntityPacket(entity: Entity, @Suppress("UNUSED") ci: CallbackInfo) {
            if (entity !is KartEntity) return
            val kart = KartImpl(entity)

            KartManager.addKart(kart)
            SUMMON.invoker().onSummon(kart)
        }

        /**
         * [ClientboundRemoveEntitiesPacket]을 수신한 뒤
         * 해당 패킷에 따라 각각의 엔티티에 대해 [ClientLevel.removeEntity]를 호출하기 직전마다 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         *
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.onHandleRemoveEntitiesPacket
         */
        @JvmStatic
        fun beforeRemoveEntityByPacket(entityId: Int, @Suppress("UNUSED") ci: CallbackInfo) {
            val entity = client.level?.getEntity(entityId) ?: return
            if (entity !is KartEntity) return
            val kart = KartManager.getKartHandle(entity) ?: return

            REMOVE.invoker().onRemove(kart)
            KartManager.removeKart(kart)
        }
    }
}