package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import net.minecraft.client.Minecraft

/**
 * [Kart] 객체에 안전하게 접근하기 위한 참조 래퍼입니다.
 *
 * 이 객체는 [Kart] 원본을 직접 보관하지 않고, 카트의 엔티티 ID만 보관합니다.
 * 실제 [Kart] 객체는 접근 시점마다 [io.github.oni0nfr1.skid.client.internal.kart.KartManager]를 통해 다시 조회됩니다.
 *
 * 따라서 라이브러리 사용자 측에서 [Kart] 또는 [KartEngine] 원본 객체를 장기간 보관하다가
 * 카트의 수명 주기가 끝난 뒤에도 계속 접근하는 일을 줄이고,
 * 항상 현재 시점에 유효한 객체에 대해서만 접근하도록 만들기 위한 용도로 사용됩니다.
 */
open class KartRef(val saddleId: Int) {
    constructor(entity: KartSaddleEntity) : this(entity.id)
    constructor(kart: Kart) : this(kart.saddleId)

    /**
     * 현재 시점에 유효한 [Kart] 객체가 존재할 경우에만 [block]을 실행합니다.
     *
     * [block]은 [Kart]의 확장 함수 형태로 호출되므로, 블록 내부의 `this`는 조회된 원본 [Kart]를 가리킵니다.
     *
     * - 현재 [saddleId]에 대응하는 [Kart]가 존재하면 [block]을 실행한 결과를 반환합니다.
     * - 카트가 이미 폐기되었거나 더 이상 유효하지 않으면 [block]을 실행하지 않고 `null`을 반환합니다.
     *
     * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 접근하려고 했을 경우
     */
    inline fun <R> access(block: Kart.() -> R): R? {
        if (!Minecraft.getInstance().isSameThread) error("Kart can only be accessed in Render Thread")
        return handle?.block()
    }

    /**
     * 현재 [saddleId]에 대응하는 [Kart] 원본 객체를 반환합니다.
     *
     * 대응하는 카트가 존재하지 않거나 이미 유효하지 않은 경우 `null`을 반환합니다.
     *
     * Java에서는 [access]를 이용할 경우 람다 함수 인라이닝이 되지 않아 성능상 비효율이 발생할 수 있으므로 필요할 경우 이 프로퍼티 이용을 추천하나,
     * 코틀린에서는 실수 방지를 위해 [access]를 사용하는 것을 권장합니다.
     */
    val handle: Kart?
        get() = KartManager.getBySaddleId(saddleId)

    companion object {
        /**
         * 주어진 [engine]의 현재 카트에 대한 타입 지정 참조를 생성합니다.
         *
         * 반환된 [Specific]은 카트의 유효성뿐 아니라,
         * 접근 시점의 현재 엔진이 타입 [E]인지도 함께 검사할 수 있습니다.
         */
        inline fun <reified E: KartEngine> specify(engine: E): Specific<E> {
            return Specific(engine, E::class.java)
        }
    }

    /**
     * 엔진 타입 정보가 함께 지정된 [KartRef]입니다.
     *
     * 이 객체는 [KartRef]와 마찬가지로 원본 [Kart] 또는 [KartEngine] 객체를 직접 장기 보관하지 않고,
     * 카트의 엔티티 ID와 기대하는 엔진 타입 정보만 보관합니다.
     *
     * [accessEngine]을 호출하면 현재 시점에 유효한 [Kart]를 다시 조회한 뒤,
     * 그 카트의 현재 엔진이 [E] 타입일 때만 블록을 실행합니다.
     *
     * 즉, 카트의 수명 주기가 끝났거나 엔진 타입이 기대와 달라진 경우에는
     * 내부 객체에 접근하지 않고 `null`을 반환합니다.
     */
    class Specific<out E: KartEngine>(engine: E, val clazz: Class<out E>) : KartRef(engine.kart) {

        /**
         * 현재 시점에 유효한 [Kart]가 존재하고,
         * 그 카트의 현재 엔진이 [E] 타입일 경우에만 [block]을 실행합니다.
         *
         * [block]은 [Kart]의 확장 함수 형태로 호출되며,
         * 첫 번째 인자로 타입이 [E]로 보장된 현재 엔진 객체가 전달됩니다.
         *
         * - 카트가 유효하지 않으면 `null`
         * - 현재 엔진이 [E] 타입이 아니면 `null`
         * - 두 조건을 모두 만족하면 [block]의 실행 결과 반환
         *
         * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 접근하려고 했을 경우
         */
        inline fun <R> accessEngine(block: Kart.(E) -> R): R? {
            if (!Minecraft.getInstance().isSameThread) error("Kart can only be accessed in Render Thread")

            val handle = handle ?: return null
            val engine = handle.engine
            if (!clazz.isInstance(engine)) return null
            return handle.block(clazz.cast(handle.engine))
        }
    }
}