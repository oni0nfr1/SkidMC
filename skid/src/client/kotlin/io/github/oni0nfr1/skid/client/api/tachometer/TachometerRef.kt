package io.github.oni0nfr1.skid.client.api.tachometer

import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerInternal
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.minecraft.client.Minecraft

/**
 * [KartTachometer] 객체에 안전하게 접근하기 위한 참조 래퍼입니다.
 *
 * 이 객체는 생성 시점 타코미터의 revision만 보관합니다.
 * 실제 [KartTachometer] 객체는 접근 시점마다 현재 활성 타코미터를 다시 조회하고 revision이 같은지 확인합니다.
 *
 * 따라서 라이브러리 사용자 측에서 [KartTachometer] 원본 객체를 장기간 보관하다가
 * 카트 타코미터의 수명 주기가 끝난 뒤에도 계속 접근하는 일을 줄이고,
 * 항상 현재 시점에 유효한 객체에 대해서만 접근하도록 만들기 위한 용도로 사용됩니다.
 *
 * @param tachometer 참조를 생성할 현재 타코미터
 */
class TachometerRef<T: KartTachometer>(tachometer: T) {

    private val revision = (tachometer as TachometerInternal).revision

    /**
     * 현재 시점에 유효한 [KartTachometer] 객체가 존재할 경우에만 [block]을 실행합니다.
     *
     * [block]은 [KartTachometer]의 확장 함수 형태로 호출되므로, 블록 내부의 `this`는 조회된 원본 [KartTachometer]를 가리킵니다.
     *
     * - 이 참조 객체에 대응하는 [KartTachometer]가 존재하면 [block]을 실행한 결과를 반환합니다.
     * - 카트 타코미터가 이미 폐기되었거나 더 이상 유효하지 않으면 [block]을 실행하지 않고 `null`을 반환합니다.
     *
     * @param block 유효한 타코미터를 receiver로 실행할 작업
     * @return 블록의 실행 결과, 타코미터가 더 이상 유효하지 않으면 `null`
     * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 접근한 경우
     */
    inline fun <R> access(block: T.() -> R): R? {
        if (!Minecraft.getInstance().isSameThread) error("Tachometer can only be accessed in Render Thread")
        return handle?.block()
    }

    /**
     * 이 참조 객체에 대응하는 [KartTachometer] 원본 객체를 반환합니다.
     *
     * 대응하는 카트 타코미터가 이제 존재하지 않거나 이미 유효하지 않은 경우 `null`을 반환합니다.
     *
     * Java에서는 [access]를 이용할 경우 람다 함수 인라이닝이 되지 않아 성능상 비효율이 발생할 수 있으므로 필요할 경우 이 프로퍼티 이용을 추천하나,
     * 코틀린에서는 실수 방지를 위해 [access]를 사용하는 것을 권장합니다.
     */
    val handle: T?
        get() {
            val current = TachometerManager.currentTachometerOrNull ?: return null
            if ((current as TachometerInternal).revision == revision) {
                @Suppress("UNCHECKED_CAST")
                return current as T
            }
            return null
        }
}
