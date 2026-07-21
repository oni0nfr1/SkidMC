package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.utils.createEvent

/** 클라이언트 월드에서 카트가 생성되거나 제거되는 이벤트를 제공합니다. */
object KartSummonEvents {

    /**
     * 카트 saddle의 타입과 엔진이 처음 준비되어 추적 가능한 카트가 되었을 때 호출됩니다.
     *
     * 콜백에 전달되는 [Kart]는 유효하며 엔진에 접근할 수 있습니다.
     * 렌더 스레드에서 호출됩니다.
     */
    @JvmField val SUMMON = createEvent<KartSummonCallback> { listeners ->
        KartSummonCallback { kart ->
            for (listener in listeners) {
                listener.onSummon(kart)
            }
        }
    }

    /**
     * 추적 중인 카트가 client level에서 제거되거나 level 자체가 종료될 때 호출됩니다.
     *
     * 콜백이 끝날 때까지 [Kart]는 유효하며, 콜백이 끝난 직후 무효화됩니다.
     * 아직 준비되지 않아 SUMMON이 발행되지 않은 pending saddle에는 REMOVE를 발행하지 않습니다.
     * 렌더 스레드에서 호출됩니다.
     */
    @JvmField val REMOVE = createEvent<KartRemoveCallback> { listeners ->
        KartRemoveCallback { kart ->
            for (listener in listeners) {
                listener.onRemove(kart)
            }
        }
    }

    /** 카트 생성 이벤트를 처리합니다. */
    fun interface KartSummonCallback {
        /** @param kart 생성되어 추적이 시작된 카트 */
        fun onSummon(kart: Kart<*>)
    }

    /** 카트 제거 이벤트를 처리합니다. */
    fun interface KartRemoveCallback {
        /** @param kart 제거 직전의 유효한 카트 */
        fun onRemove(kart: Kart<*>)
    }
}
