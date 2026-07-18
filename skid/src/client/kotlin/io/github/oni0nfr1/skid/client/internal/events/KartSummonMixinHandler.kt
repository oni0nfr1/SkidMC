package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.api.events.KartSummonEvents
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.api.kart.KartSaddleEntity
import io.github.oni0nfr1.skid.client.internal.kart.KartImpl
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.world.entity.Entity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

internal object KartSummonMixinHandler {
    private val client: Minecraft by MCClient

    /**
     * [ClientboundAddEntityPacket]을 수신한 뒤
     * [ClientPacketListener.handleAddEntity]에서 생성한 엔티티가 null이 아닐 경우 월드에 등록 후 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleAddEntityPacket
     */
    @JvmStatic
    fun onAddEntityPacket(entity: Entity, @Suppress("UNUSED") ci: CallbackInfo) {
        if (entity !is KartSaddleEntity) return
        val kart = KartImpl(entity)

        KartManager.addKart(kart)
        KartSummonEvents.SUMMON.invoker().onSummon(kart)
    }

    /**
     * [ClientboundRemoveEntitiesPacket]을 수신한 뒤
     * 해당 패킷에 따라 각각의 엔티티에 대해 [ClientLevel.removeEntity]를 호출하기 직전마다 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleRemoveEntitiesPacket
     */
    @JvmStatic
    fun beforeRemoveEntityByPacket(entityId: Int, @Suppress("UNUSED") ci: CallbackInfo) {
        val entity = client.level?.getEntity(entityId) ?: return
        if (entity !is KartSaddleEntity) return
        val kart = KartManager.getBySaddleId(entity.id) ?: return

        KartSummonEvents.REMOVE.invoker().onRemove(kart)
        KartManager.removeKart(kart.saddleId)
    }
}
