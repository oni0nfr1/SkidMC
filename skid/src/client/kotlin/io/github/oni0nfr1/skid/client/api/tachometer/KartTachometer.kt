package io.github.oni0nfr1.skid.client.api.tachometer

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import net.minecraft.network.chat.Component

/**
 * 서버에서 수신한 액션바를 파싱해 구성한 카트 타코미터입니다.
 *
 * 타코미터는 새 액션바가 파싱되거나 탑승 대상이 바뀌면 무효화될 수 있습니다.
 * 장기간 참조해야 할 때는 [TachometerRef]를 사용하세요.
 */
sealed interface KartTachometer {
    /** 이 타코미터에 대응하는 엔진 타입입니다. */
    val type: KartEngine.Type
    /** 마지막으로 정상 파싱된 원본 액션바 컴포넌트입니다. */
    val text: Component
    /** [text]에서 스타일을 제거한 일반 문자열입니다. */
    val rawString: String
        get() = text.string
}
