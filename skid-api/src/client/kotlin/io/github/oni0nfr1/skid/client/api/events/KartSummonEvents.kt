package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle

/** 클라이언트 월드에서 카트 엔티티의 발견, 준비 완료 및 제거 이벤트를 제공합니다. */
object KartSummonEvents {

    /**
     * 카트 saddle 엔티티가 클라이언트 월드에 추가된 직후 수명 주기당 한 번 호출됩니다.
     *
     * 첫 카트 정보 어트리뷰트가 아직 적용되지 않았을 수 있으므로 준비된 Kart 대신
     * [KartSaddle]을 전달합니다. 렌더 스레드에서 호출됩니다.
     */
    @JvmField
    val SUMMON_EARLY = createEvent<KartSummonEarlyCallback> { listeners ->
        KartSummonEarlyCallback { kartEntity ->
            for (listener in listeners) {
                listener.onSummon(kartEntity)
            }
        }
    }

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
     * [SUMMON_EARLY]가 호출된 saddle이 클라이언트 월드에서 제거되거나 월드 자체가 종료될
     * 때 공통 종료 이벤트로 호출됩니다.
     *
     * 준비 전에 saddle이 사라지면 [SUMMON] 없이 호출될 수 있습니다. [SUMMON]까지 호출된
     * 수명 주기에서는 콜백이 끝날 때까지 saddle을 통해 준비된 카트에 접근할 수 있습니다.
     * 준비 전 수명 주기도 정리할 수 있도록 [KartSaddle]을 전달하며 렌더 스레드에서
     * 호출됩니다.
     */
    @JvmField
    val REMOVE = createEvent<KartRemoveCallback> { listeners ->
        KartRemoveCallback { kartEntity ->
            for (listener in listeners) {
                listener.onRemove(kartEntity)
            }
        }
    }

    /** 준비 전 카트 엔티티 생성 이벤트를 처리합니다. */
    fun interface KartSummonEarlyCallback {
        /** @param kartEntity 클라이언트 월드에 추가된 카트의 saddle 엔티티 */
        fun onSummon(kartEntity: KartSaddle)
    }

    /** 준비된 카트 생성 이벤트를 처리합니다. */
    fun interface KartSummonCallback {
        /** @param kart 생성되어 추적이 시작된 카트 */
        fun onSummon(kart: KartRef)
    }

    /** 카트 제거 이벤트를 처리합니다. */
    fun interface KartRemoveCallback {
        /** @param kartEntity 제거 직전의 카트 saddle 엔티티 */
        fun onRemove(kartEntity: KartSaddle)
    }
}
