package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.KartRef

/** 클라이언트 월드에서 준비된 카트의 추적 시작과 종료 이벤트를 제공합니다. */
object KartSummonEvents {

    /**
     * 카트 saddle의 타입과 엔진이 처음 준비되어 추적 가능한 카트가 되었을 때 호출됩니다.
     *
     * 콜백에 전달되는 [KartRef]는 유효한 카트로 해석되며 엔진에 접근할 수 있습니다.
     * 렌더 스레드에서 호출됩니다.
     */
    @JvmField
    val SUMMON = createEvent<KartSummonCallback> { listeners ->
        KartSummonCallback { kart ->
            for (listener in listeners) {
                listener.onSummon(kart)
            }
        }
    }

    /**
     * 추적 중인 카트가 클라이언트 월드에서 제거되거나 월드 자체가 종료될 때 호출됩니다.
     *
     * 콜백이 끝날 때까지 [KartRef]는 유효한 카트로 해석되며, 콜백이 끝난 직후
     * 빈 참조가 됩니다.
     * 아직 준비되지 않아 [SUMMON]이 발행되지 않은 pending saddle에는 이 이벤트를 발행하지
     * 않습니다. 렌더 스레드에서 호출됩니다.
     */
    @JvmField
    val REMOVE = createEvent<KartRemoveCallback> { listeners ->
        KartRemoveCallback { kart ->
            for (listener in listeners) {
                listener.onRemove(kart)
            }
        }
    }

    /** 카트 생성 이벤트를 처리합니다. */
    fun interface KartSummonCallback {
        /** @param kart 생성되어 추적이 시작된 카트 */
        fun onSummon(kart: KartRef)
    }

    /** 카트 제거 이벤트를 처리합니다. */
    fun interface KartRemoveCallback {
        /** @param kart 제거 직전의 유효한 카트 */
        fun onRemove(kart: KartRef)
    }
}
