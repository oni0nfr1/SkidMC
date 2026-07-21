package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.spi.SkidApiProviderLoader
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.api.utils.Ref
import net.minecraft.client.Minecraft
import java.util.Optional
import java.util.UUID

/**
 * 카트의 수명 주기와 독립적으로 보관할 수 있는 참조입니다.
 *
 * 실제 카트는 [get]을 호출하는 시점에 엔티티 ID로 다시 조회하고 생성 당시의 UUID와
 * 비교합니다. 같은 서버 엔티티가 다시 동기화되면 resolve될 수 있지만, 다른 엔티티가
 * 같은 ID를 재사용하면 빈 [Optional]을 반환합니다.
 */
class KartRef private constructor(
    /** 참조하는 카트 saddle의 엔티티 ID입니다. */
    val saddleId: Int,
    private val saddleUuid: UUID,
) : Ref<Kart<*>> {

    /** [saddle]의 엔티티 ID와 UUID를 함께 캡처한 카트 참조를 생성합니다. */
    constructor(saddle: KartSaddle) : this(saddle.id, saddle.uuid)

    /**
     * 현재 시점에 유효한 카트를 반환합니다.
     *
     * 카트가 아직 준비되지 않았거나 현재 추적되지 않거나, provider가 반환한 saddle의
     * UUID가 생성 당시와 다르면 빈 [Optional]을 반환합니다.
     *
     * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 호출한 경우
     */
    override fun get(): Optional<Kart<*>> {
        check(Minecraft.getInstance().isSameThread) {
            "Kart can only be accessed on the render thread"
        }

        return SkidApiProviderLoader.provider.getKart(saddleId, saddleUuid)
    }

    /**
     * [type]으로 엔진 타입이 지정된 카트 참조를 반환합니다.
     *
     * 반환된 참조는 접근할 때마다 현재 카트의 타입을 다시 검사합니다. 카트가 유효하지
     * 않거나 현재 타입이 [type]과 다르면 빈 [Optional]을 반환합니다.
     */
    fun <ENGINE : KartEngine> specify(
        type: KartType<ENGINE>,
    ): Ref<Kart<ENGINE>> = SpecifiedKartRef(this, type)
}

private class SpecifiedKartRef<ENGINE : KartEngine>(
    private val origin: KartRef,
    private val type: KartType<ENGINE>,
) : Ref<Kart<ENGINE>> {
    override fun get(): Optional<Kart<ENGINE>> {
        val kart = origin.get().orElse(null) ?: return Optional.empty()
        if (kart.type !== type) return Optional.empty()

        @Suppress("UNCHECKED_CAST")
        return Optional.of(kart as Kart<ENGINE>)
    }
}
