package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.attr.AttrModifierSnapshot
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import net.fabricmc.fabric.api.event.Event
import net.minecraft.core.Holder
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.player.Player

@Deprecated(message = "카트 메타데이터의 주체가 플레이어에서 카트 엔티티로 변경되었습니다. getKartMeta를 사용하세요.", replaceWith = ReplaceWith("KartAttrEvents"))
object RiderAttrEvents {
    /** 라이더 메타데이터 전송에 사용하는 Minecraft 어트리뷰트입니다. */
    val RIDER_META_ATTR_KEY: Holder<Attribute> = KartAttrEvents.KART_META_ATTR_KEY

    /**
     * 어트리뷰트가 갱신될 때 호출됩니다. 어트리뷰트 전체 값을 한 번에 얻고자 할 때 사용하세요.
     *
     * 다른 일반 어트리뷰트 이벤트들보다 먼저 호출됩니다.
     */
    @JvmField
    val RIDER_META_ATTR = createEvent { listeners ->
        AttrPacketCallback { player, base, modifiers ->
            for (listener in listeners) {
                listener.onPacket(player, base, modifiers)
            }
        }
    }

    // 엔진 구분
    /** 선택한 엔진 코드가 변경될 때 호출됩니다. */
    @JvmField val KART_ENGINE = riderAttrEvent(KartAttrEvents.ID_ENGINE)
    /** 현재 탑승한 카트의 실제 엔진 코드가 변경될 때 호출됩니다. */
    @JvmField val KART_ENGINE_REAL = riderAttrEvent(KartAttrEvents.ID_ENGINE_REAL)

    // 랩 수 관련
    /** 트랙의 최대 랩 수가 변경될 때 호출됩니다. */
    @JvmField val MAX_LAP = riderAttrEvent(KartAttrEvents.CTX_MAX_LAP)
    /** 플레이어의 현재 랩이 변경될 때 호출됩니다. */
    @JvmField val CURRENT_LAP = riderAttrEvent(KartAttrEvents.CTX_CURRENT_LAP)

    // 순간부스터 상태
    /** 순간 부스터 기능의 활성화 상태가 변경될 때 호출됩니다. */
    @JvmField val FORCE_INSTANT_BOOST = riderAttrEvent(KartAttrEvents.CAN_IBOOST)
    /** 순간 부스터 사용 가능 상태가 변경될 때 호출됩니다. */
    @JvmField val ACTIVE_INSTANT_BOOST = riderAttrEvent(KartAttrEvents.STATE_IBOOST)

    // 정규 카트 상태
    /** 드리프트 상태가 변경될 때 호출됩니다. */
    @JvmField val IS_DRIFTING = riderAttrEvent(KartAttrEvents.STATE_DRIFTING)
    /** 일반·듀얼 부스터 상태가 변경될 때 호출됩니다. */
    @JvmField val BOOST_STATE = riderAttrEvent(KartAttrEvents.STATE_NITRO)
    /** 최대 부스터 수가 변경될 때 호출됩니다. */
    @JvmField val KART_MAX_BOOST_COUNT = riderAttrEvent(KartAttrEvents.CAP_NITRO_COUNT)
    /** 드래프트 상태가 변경될 때 호출됩니다. */
    @JvmField val DRAFT_STATE = riderAttrEvent(KartAttrEvents.STATE_DRAFT_ACCEL)

    // 특수 설정
    /** 카트 성능 제한 단계가 변경될 때 호출됩니다. */
    @JvmField val KART_PERFORMANCE_LIMIT_LEVEL = riderAttrEvent(KartAttrEvents.CTX_PERF_LIMIT)
    /** 카트 타이어 설정이 변경될 때 호출됩니다. */
    @JvmField val KART_TIRE = riderAttrEvent(KartAttrEvents.ID_TIRE)

    init {
        KartAttrEvents.KART_META_ATTR.register { kartEntity, base, modifiers ->
            kartEntity.passengers.forEach { passenger ->
                if (passenger is Player) RIDER_META_ATTR.invoker().onPacket(passenger, base, modifiers)
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun riderAttrEvent(source: Event<KartAttrEvents.KartAttrCallback>): Event<RiderAttrCallback> {
        val event = createEvent { listeners ->
            RiderAttrCallback { player, value ->
                for (listener in listeners) {
                    listener.onAttrChange(player, value)
                }
            }
        }
        source.register { kartEntity, value ->
            kartEntity.passengers.forEach { passenger ->
                if (passenger is Player) event.invoker().onAttrChange(passenger, value)
            }
        }
        return event
    }

    /** 개별 라이더 메타데이터 modifier 변경 이벤트를 처리합니다. */
    fun interface RiderAttrCallback {
        /**
         * @param player 값이 변경된 카트에 탑승 중인 플레이어
         * @param value 패킷으로 새로 수신한 modifier 값
         */
        fun onAttrChange(player: Player, value: Double)
    }

    /** 라이더 메타데이터 어트리뷰트 패킷 전체를 처리합니다. */
    fun interface AttrPacketCallback {
        /**
         * @param player 패킷 대상 카트에 탑승 중인 플레이어
         * @param base 패킷에 포함된 어트리뷰트 기본값
         * @param modifiers 패킷에 포함된 modifier 스냅샷
         */
        fun onPacket(player: Player, base: Double, modifiers: AttrModifierSnapshot)
    }
}
