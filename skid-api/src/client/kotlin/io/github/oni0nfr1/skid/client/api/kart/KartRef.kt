package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.api.spi.SkidApiProviderLoader
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.api.utils.Ref
import net.minecraft.client.Minecraft
import java.util.Optional

/**
 * 카트의 수명 주기와 독립적으로 보관할 수 있는 참조입니다.
 *
 * 실제 카트는 [get]을 호출하는 시점에 다시 조회하므로, 카트가 제거되었거나 참조가
 * 더 이상 유효하지 않으면 빈 [Optional]을 반환합니다.
 */
class KartRef(
    /** 참조하는 카트의 대구 엔티티 ID입니다. */
    val saddleId: Int,
) : Ref<Kart<*, *>> {

    /**
     * 현재 시점에 유효한 카트를 반환합니다.
     *
     * provider가 반환한 카트가 이미 제거되었거나 다른 대구 엔티티를 가리키면 빈
     * [Optional]을 반환합니다.
     *
     * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 호출한 경우
     */
    override fun get(): Optional<Kart<*, *>> {
        check(Minecraft.getInstance().isSameThread) {
            "Kart can only be accessed on the render thread"
        }

        return SkidApiProviderLoader.provider.getKart(saddleId).filter { kart ->
            kart.alive && kart.saddle.id == saddleId
        }
    }

    /**
     * [type]으로 엔진과 타코미터 타입이 지정된 카트 참조를 반환합니다.
     *
     * 반환된 참조는 접근할 때마다 현재 카트의 타입을 다시 검사합니다. 카트가 유효하지
     * 않거나 현재 타입이 [type]과 다르면 빈 [Optional]을 반환합니다.
     */
    fun <ENGINE, TACHOMETER> specify(
        type: KartType<ENGINE, TACHOMETER>,
    ): Ref<Kart<ENGINE, TACHOMETER>>
        where
            ENGINE : KartEngine,
            TACHOMETER : KartTachometer = SpecifiedKartRef(this, type)
}

private class SpecifiedKartRef<ENGINE, TACHOMETER>(
    private val origin: KartRef,
    private val type: KartType<ENGINE, TACHOMETER>,
) : Ref<Kart<ENGINE, TACHOMETER>>
    where
        ENGINE : KartEngine,
        TACHOMETER : KartTachometer
{
    override fun get(): Optional<Kart<ENGINE, TACHOMETER>> {
        val kart = origin.get().orElse(null) ?: return Optional.empty()
        if (kart.type !== type) return Optional.empty()

        @Suppress("UNCHECKED_CAST")
        return Optional.of(kart as Kart<ENGINE, TACHOMETER>)
    }
}
