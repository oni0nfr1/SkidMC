package io.github.oni0nfr1.skid.client.api.kart

import net.minecraft.client.Minecraft

/**
 * [Kart] 객체에 안전하게 접근하기 위한 객체입니다.
 *
 * 원본 카트 객체는 대구 엔티티가 디스폰되는 등의 상황에서 [KartManager]로부터 버려지게 되지만,
 * 해당 객체를 라이브러리 사용자 측에서 들고 있을 경우
 */
class KartRef(val kartId: Int) {
    constructor(entity: KartEntity) : this(entity.id)
    constructor(kart: Kart) : this(kart.entityId)

    /**
     * 이 람다 블록은 카트 원본의 확장 함수로, 이 범위에서는 this가 [Kart] 원본 객체를 가리키게 됩니다.
     *
     * 카트 원본 객체가 엔티티 디스폰 등의 이유로 버려진 상태가 아니라면 블록을 실행하고 결과를 반환합니다.
     * 원본이 버려졌을 경우는 블록을 실행하지 않으면 null을 반환합니다.
     *
     * @throws IllegalStateException 메인 스레드가 아닌 곳에서 원본에 접근하려고 했을 경우
     */
    inline fun <R> access(block: Kart.() -> R): R? {
        if (Minecraft.getInstance().isSameThread) {
            return handle?.block()
        } else {
            error("Kart can only be accessed in Render Thread")
        }
    }

    /**
     * [access]와 같은 방식으로 동작하나, 원본 핸들이 버려졌을 경우 예외를 던집니다.
     *
     * @throws StaleKartException 원본이 이미 버려졌을 경우
     * @throws IllegalStateException 메인 스레드가 아닌 곳에서 원본에 접근하려고 했을 경우
     */
    inline fun <R> forceAccess(block: Kart.() -> R): R {
        if (Minecraft.getInstance().isSameThread) {
            return handle?.block() ?: throw StaleKartException()
        } else {
            error("Kart can only be accessed in Render Thread")
        }
    }

    val handle: Kart?
        get() = KartManager.getKartHandleByEntityId(kartId)
}