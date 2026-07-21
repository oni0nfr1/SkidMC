package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.SkidClient
import io.github.oni0nfr1.skid.client.api.events.KartSummonEvents
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.world.entity.Entity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/** 카트 saddle 패킷을 pending, ready, removed 수명 주기 전이에 연결합니다. */
internal object KartSummonMixinHandler {
    private val client: Minecraft by MCClient

    /**
     * client level에 추가된 saddle을 pending 카트로 등록합니다.
     *
     * REQUIRES:
     * - Vanilla가 [entity]를 client level에 추가한 뒤 호출된다.
     *
     * ENSURES:
     * - [entity]가 KartSaddle이면 해당 ID가 pending 상태가 된다.
     * - 같은 ID의 이전 추적 상태가 있으면 무효화되고 새 수명 주기로 대체된다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleAddEntityPacket
     */
    @JvmStatic
    fun onAddEntityPacket(entity: Entity, @Suppress("UNUSED") ci: CallbackInfo) {
        if (entity !is KartSaddle) return

        if (entity.id in KartManager.getTrackedSaddleIds()) {
            SkidClient.LOGGER.warn(
                "Kart saddle entity ID was added while still tracked: saddleId={}; " +
                    "replacing the previous lifecycle without REMOVE",
                entity.id,
            )
        }
        KartManager.trackKart(entity)
    }

    /**
     * saddle의 어트리뷰트에서 KartType을 확인하고 pending 카트를 ready로 전환합니다.
     *
     * ENSURES:
     * - pending saddle의 타입을 해석할 수 있으면 ready Kart로 전환하고 SUMMON을 한 번 발행한다.
     *
     * FAILURE:
     * - SUMMON 콜백이 예외를 던져도 ready 상태를 유지한다.
     */
    @JvmStatic
    fun afterUpdateAttributes(entity: Entity) {
        if (entity !is KartSaddle) return
        val kart = KartManager.prepareKart(entity) ?: return
        KartSummonEvents.SUMMON.invoker().onSummon(kart)
    }

    /**
     * [ClientboundRemoveEntitiesPacket]에 따라 saddle이 제거되기 직전에 수명 주기를 종료합니다.
     *
     * REQUIRES:
     * - Vanilla의 [ClientLevel.removeEntity] 호출 전에 실행된다.
     *
     * ENSURES:
     * - ready Kart이면 유효한 상태에서 REMOVE를 발행한 뒤 무효화한다.
     * - pending saddle이면 이벤트 없이 추적 상태를 제거한다.
     *
     * FAILURE:
     * - REMOVE 콜백이 예외를 던져도 추적 상태를 제거한다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleRemoveEntitiesPacket
     */
    @JvmStatic
    fun beforeRemoveEntityByPacket(entityId: Int, @Suppress("UNUSED") ci: CallbackInfo) {
        val entity = client.level?.getEntity(entityId)
        if (entity is KartSaddle) {
            removeTrackedKart(entity)
        } else if (entityId in KartManager.getTrackedSaddleIds()) {
            discardTrackedKart(entityId, "entity removal")
        }
    }

    /**
     * 존재하는 saddle의 추적 수명 주기를 정상적으로 종료합니다.
     *
     * ENSURES:
     * - ready Kart이면 REMOVE 콜백이 끝날 때까지 유효하다.
     * - saddle의 추적 상태를 제거한다.
     *
     * FAILURE:
     * - REMOVE 콜백이 예외를 던져도 추적 상태를 제거한다.
     */
    fun removeTrackedKart(saddle: KartSaddle) {
        val kart = KartManager.getBySaddleId(saddle.id)
        try {
            if (kart != null) KartSummonEvents.REMOVE.invoker().onRemove(kart)
        } finally {
            KartManager.removeKart(saddle.id)
        }
    }

    /**
     * saddle 엔티티를 확인할 수 없는 추적 상태를 이벤트 없이 폐기합니다.
     *
     * ENSURES:
     * - 추적 중인 saddle을 REMOVE 없이 제거한다.
     */
    fun discardTrackedKart(saddleId: Int, phase: String) {
        if (saddleId !in KartManager.getTrackedSaddleIds()) return

        SkidClient.LOGGER.warn(
            "Tracked kart saddle is missing during {}: saddleId={}; " +
                "discarding the lifecycle without REMOVE",
            phase,
            saddleId,
        )
        KartManager.removeKart(saddleId)
    }
}
