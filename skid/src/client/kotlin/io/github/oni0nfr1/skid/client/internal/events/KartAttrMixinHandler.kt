package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.api.attr.AttrModifierSnapshot
import io.github.oni0nfr1.skid.client.api.attr.KartAttributes
import io.github.oni0nfr1.skid.client.api.attr.unstable.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.events.KartAttrEvents
import io.github.oni0nfr1.skid.client.api.events.unstable.KartAttrModifierEvents
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import net.fabricmc.fabric.api.event.Event
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

/** Vanilla 어트리뷰트 적용 전후를 카트 정보 이벤트에 연결합니다. */
internal object KartAttrMixinHandler {
    private val modifierEventsById: Map<
        ResourceLocation,
        Event<KartAttrModifierEvents.KartAttrModifierCallback>,
    > = mapOf(
        KnownAttrModId.ID_ENGINE to KartAttrModifierEvents.ID_ENGINE,
        KnownAttrModId.ID_ENGINE_REAL to KartAttrModifierEvents.ID_ENGINE_REAL,
        KnownAttrModId.CTX_MAX_LAP to KartAttrModifierEvents.CTX_MAX_LAP,
        KnownAttrModId.CTX_CURRENT_LAP to KartAttrModifierEvents.CTX_CURRENT_LAP,
        KnownAttrModId.CAN_IBOOST to KartAttrModifierEvents.CAN_IBOOST,
        KnownAttrModId.STATE_IBOOST to KartAttrModifierEvents.STATE_IBOOST,
        KnownAttrModId.STATE_DRIFTING to KartAttrModifierEvents.STATE_DRIFTING,
        KnownAttrModId.STATE_NITRO to KartAttrModifierEvents.STATE_NITRO,
        KnownAttrModId.CAP_NITRO_COUNT to KartAttrModifierEvents.CAP_NITRO_COUNT,
        KnownAttrModId.STATE_DRAFT_ACCEL to KartAttrModifierEvents.STATE_DRAFT_ACCEL,
        KnownAttrModId.STATE_TEAM_NITRO_COUNT to KartAttrModifierEvents.STATE_TEAM_NITRO_COUNT,
        KnownAttrModId.CTX_PERF_LIMIT to KartAttrModifierEvents.CTX_PERF_LIMIT,
        KnownAttrModId.ID_TIRE to KartAttrModifierEvents.ID_TIRE,
        KnownAttrModId.ID_BODY_TYPE to KartAttrModifierEvents.ID_BODY_TYPE,
        KnownAttrModId.STATE_MODEL_ROTATION_ALLOWED to
            KartAttrModifierEvents.STATE_MODEL_ROTATION_ALLOWED,
    )

    /**
     * 카트 정보 스냅샷과 각 modifier의 적용 전 값을 캡처합니다.
     *
     * REQUIRES:
     * - Vanilla가 [snapshot]을 대상 AttributeInstance에 적용하기 직전에 호출된다.
     *
     * ENSURES:
     * - 카트 정보 스냅샷이면 적용 후 발행에 필요한 불변 복사본을 반환한다.
     * - 다른 엔티티 또는 어트리뷰트이면 `null`을 반환한다.
     */
    @JvmStatic
    fun captureBeforeUpdate(
        entity: Entity,
        snapshot: ClientboundUpdateAttributesPacket.AttributeSnapshot,
    ): PendingKartInfoUpdate? {
        if (entity !is KartSaddle) return null
        if (snapshot.attribute != KartAttributes.KART_INFO_ATTR_KEY) return null
        val attribute = entity.attributes.getInstance(KartAttributes.KART_INFO_ATTR_KEY) ?: return null

        val values = LinkedHashMap<ResourceLocation, Double>()
        snapshot.modifiers.forEach { values[it.id] = it.amount }
        val modifiers = AttrModifierSnapshot(values)
        val previousValues = values.keys.associateWith { attribute.getModifier(it)?.amount }

        return PendingKartInfoUpdate(entity, snapshot.base, modifiers, previousValues)
    }

    /**
     * 적용이 끝난 카트 정보의 전체 갱신과 알려진 modifier 변경을 발행합니다.
     *
     * REQUIRES:
     * - [update]가 나타내는 Vanilla 어트리뷰트 스냅샷이 엔티티에 모두 적용된 후 호출된다.
     *
     * ENSURES:
     * - UPDATE를 먼저 발행하고, 새 스냅샷에 존재하며 값이 달라진 modifier 이벤트를 발행한다.
     * - 새 스냅샷에서 사라진 modifier에 대해서는 이벤트를 발행하지 않는다.
     *
     * FAILURE:
     * - 이벤트 리스너가 예외를 던지면 남은 어트리뷰트 이벤트는 발행하지 않고 호출자에게 전파한다.
     */
    @JvmStatic
    fun publishAfterUpdate(update: PendingKartInfoUpdate?) {
        update ?: return
        KartAttrEvents.UPDATE.invoker().onUpdate(update.saddle, update.base, update.modifiers)

        update.modifiers.forEach { (key, value) ->
            val previousValue = update.previousValues[key]
            if (value != previousValue) {
                modifierEventsById[key]?.invoker()?.onChange(update.saddle, previousValue, value)
            }
        }
    }
}

/** 한 카트 정보 AttributeSnapshot의 적용 전 상태와 새 값입니다. */
internal data class PendingKartInfoUpdate(
    val saddle: KartSaddle,
    val base: Double,
    val modifiers: AttrModifierSnapshot,
    val previousValues: Map<ResourceLocation, Double?>,
)
