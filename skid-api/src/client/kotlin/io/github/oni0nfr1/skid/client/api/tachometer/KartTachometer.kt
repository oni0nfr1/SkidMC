package io.github.oni0nfr1.skid.client.api.tachometer

import net.minecraft.network.chat.Component

/**
 * 클라이언트 화면에 표시된 주행 정보를 파싱해 구성한 카트 타코미터입니다.
 */
interface KartTachometer {

    /** 마지막으로 정상 파싱된 원본 컴포넌트입니다. */
    val text: Component

    /** [text]에서 스타일을 제거한 일반 문자열입니다. */
    val rawString: String
        get() = text.string

}
