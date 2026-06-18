package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.attr.AttrModifierSnapshot
import io.github.oni0nfr1.skid.client.api.attr.KnownAttrModId
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import net.fabricmc.fabric.api.event.Event
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.Attributes
import io.github.oni0nfr1.skid.client.api.kart.KartSaddleEntity

/**
 * 서버에서 카트 대구 엔티티의 어트리뷰트로 전송되는 카트 메타데이터 이벤트를 제공합니다.
 *
 * 모든 어트리뷰트 이벤트는 렌더 스레드에서 호출됩니다.
 * 개별 modifier 이벤트는 새 스냅샷에 포함된 값이 이전 값과 다를 때만 호출되며,
 * 이전 스냅샷에 있던 modifier가 새 스냅샷에서 사라진 경우에는 호출되지 않습니다.
 */
object KartAttrEvents {

    /** 카트 메타데이터 전송에 사용하는 Minecraft 어트리뷰트입니다. */
    val KART_META_ATTR_KEY: Holder<Attribute> = Attributes.ARMOR
    internal val attrEventRegistry = mutableMapOf<ResourceLocation, Event<KartAttrCallback>>()

    /**
     * 어트리뷰트가 갱신될 때 호출됩니다. 어트리뷰트 전체 값을 한 번에 얻고자 할 때 사용하세요.
     *
     * 다른 일반 어트리뷰트 이벤트들보다 먼저 호출됩니다.
     */
    @JvmField
    val KART_META_ATTR = createEvent { listeners ->
        AttrPacketCallback { kartEntity, base, modifiers ->
            for (listener in listeners) {
                listener.onPacket(kartEntity, base, modifiers)
            }
        }
    }

    // 엔진 구분
    /** 선택한 엔진 코드가 변경될 때 호출됩니다. */
    @JvmField val ID_ENGINE = attrEvent(KnownAttrModId.ID_ENGINE)
    /** 현재 탑승한 카트의 실제 엔진 코드가 변경될 때 호출됩니다. */
    @JvmField val ID_ENGINE_REAL = attrEvent(KnownAttrModId.ID_ENGINE_REAL)

    // 랩 수 관련
    /** 트랙의 최대 랩 수가 변경될 때 호출됩니다. */
    @JvmField val CTX_MAX_LAP = attrEvent(KnownAttrModId.CTX_MAX_LAP)
    /** 플레이어의 현재 랩이 변경될 때 호출됩니다. */
    @JvmField val CTX_CURRENT_LAP = attrEvent(KnownAttrModId.CTX_CURRENT_LAP)

    // 순간부스터 상태
    /** 순간 부스터 기능의 활성화 상태가 변경될 때 호출됩니다. */
    @JvmField val CAN_IBOOST = attrEvent(KnownAttrModId.CAN_IBOOST)
    /** 순간 부스터 사용 가능 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_IBOOST = attrEvent(KnownAttrModId.STATE_IBOOST)

    // 정규 카트 상태
    /** 드리프트 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_DRIFTING = attrEvent(KnownAttrModId.STATE_DRIFTING)
    /** 일반·듀얼 부스터 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_NITRO = attrEvent(KnownAttrModId.STATE_NITRO)
    /** 최대 부스터 수가 변경될 때 호출됩니다. */
    @JvmField val CAP_NITRO_COUNT = attrEvent(KnownAttrModId.CAP_NITRO_COUNT)
    /** 드래프트 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_DRAFT_ACCEL = attrEvent(KnownAttrModId.STATE_DRAFT_ACCEL)
    /** 보유한 팀 부스터 개수가 변경될 때 호출됩니다. */
    @JvmField val STATE_TEAM_NITRO_COUNT = attrEvent(KnownAttrModId.STATE_TEAM_NITRO_COUNT)

    // 특수 설정
    /** 카트 성능 제한 단계가 변경될 때 호출됩니다. */
    @JvmField val CTX_PERF_LIMIT = attrEvent(KnownAttrModId.CTX_PERF_LIMIT)
    /** 카트 타이어 설정이 변경될 때 호출됩니다. */
    @JvmField val ID_TIRE = attrEvent(KnownAttrModId.ID_TIRE)
    /** 카트 바디 종류가 변경될 때 호출됩니다. */
    @JvmField val ID_BODY_TYPE = attrEvent(KnownAttrModId.ID_BODY_TYPE)
    /**
     * 어트리뷰트 modifier [KnownAttrModId.STATE_MODEL_ROTATION_ALLOWED] 값이 변경될 때 호출됩니다.
     *
     * @see KnownAttrModId.STATE_MODEL_ROTATION_ALLOWED
     */
    @JvmField val STATE_MODEL_ROTATION_ALLOWED = attrEvent(KnownAttrModId.STATE_MODEL_ROTATION_ALLOWED)



    /** 개별 카트 메타데이터 modifier 변경 이벤트를 처리합니다. */
    fun interface KartAttrCallback {
        /**
         * @param kartEntity 값이 변경된 카트의 대구 엔티티
         * @param value 패킷으로 새로 수신한 modifier 값
         */
        fun onAttrChange(kartEntity: KartSaddleEntity, value: Double)
    }

    /** 카트 메타데이터 어트리뷰트 패킷 전체를 처리합니다. */
    fun interface AttrPacketCallback {
        /**
         * @param kartEntity 패킷의 대상 카트 대구 엔티티
         * @param base 패킷에 포함된 어트리뷰트 기본값
         * @param modifiers 패킷에 포함된 modifier 스냅샷
         */
        fun onPacket(kartEntity: KartSaddleEntity, base: Double, modifiers: AttrModifierSnapshot)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun attrEvent(key: ResourceLocation): Event<KartAttrCallback> {
        val event = createEvent { listeners ->
            KartAttrCallback { kartEntity, value ->
                for (listener in listeners) {
                    listener.onAttrChange(kartEntity, value)
                }
            }
        }
        attrEventRegistry[key] = event
        return event
    }
}
