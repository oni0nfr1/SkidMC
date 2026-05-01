package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.utils.createEvent

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
}