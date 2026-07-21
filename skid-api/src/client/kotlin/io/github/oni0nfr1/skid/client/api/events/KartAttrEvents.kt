package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.attr.AttrModifierSnapshot
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle

/** 서버에서 수신한 카트 정보 어트리뷰트의 전체 갱신 이벤트를 제공합니다. */
object KartAttrEvents {
    /**
     * 카트 정보 어트리뷰트의 새 값이 엔티티에 모두 적용된 후 호출됩니다.
     *
     * 첫 정보 패킷에서는 아직 준비된 Kart가 생성되지 않았을 수 있으므로 카트의 saddle
     * 엔티티를 전달합니다. 콜백에서 [KartSaddle.getAttributes]로 조회하는 값은 전달된
     * [AttrModifierSnapshot]과 같은 새 상태입니다. 개별 modifier 이벤트보다 먼저 렌더
     * 스레드에서 호출됩니다.
     */
    @JvmField
    val UPDATE = createEvent { listeners ->
        KartAttrUpdateCallback { saddle, base, modifiers ->
            for (listener in listeners) {
                listener.onUpdate(saddle, base, modifiers)
            }
        }
    }

    /** 카트 정보 어트리뷰트의 전체 갱신을 처리합니다. */
    fun interface KartAttrUpdateCallback {
        /**
         * @param saddle 패킷 대상 카트의 대구 엔티티
         * @param base 패킷에 포함된 어트리뷰트 기본값
         * @param modifiers 패킷에 포함된 modifier 스냅샷
         */
        fun onUpdate(saddle: KartSaddle, base: Double, modifiers: AttrModifierSnapshot)
    }
}
