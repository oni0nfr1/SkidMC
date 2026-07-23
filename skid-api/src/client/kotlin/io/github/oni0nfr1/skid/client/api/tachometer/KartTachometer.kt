package io.github.oni0nfr1.skid.client.api.tachometer

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import net.minecraft.network.chat.Component

/**
 * 클라이언트 화면에 표시된 주행 정보를 파싱해 구성한 카트 타코미터입니다.
 *
 * 직접 하위 타입 계층은 SkidMC가 정의합니다. API 소비자는 이 인터페이스를 직접
 * 구현하지 않고 [KartEngine.tachometer]로 구현체 모드가 제공하는 typed view를
 * 사용합니다.
 */
sealed interface KartTachometer {

    /** 마지막으로 정상 파싱된 원본 컴포넌트입니다. */
    val text: Component

    /** [text]에서 스타일을 제거한 일반 문자열입니다. */
    val rawString: String
        get() = text.string

}
