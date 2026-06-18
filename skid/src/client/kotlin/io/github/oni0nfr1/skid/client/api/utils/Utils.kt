@file:JvmName("SkidUtils")

package io.github.oni0nfr1.skid.client.api.utils

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.TachometerRef
import net.minecraft.client.Minecraft

/**
 * 카트와 타코미터가 모두 유효한 경우에만 [block]을 실행합니다.
 *
 * 두 참조는 서로 다른 수명 주기를 가지므로 블록 실행 직전에 각각의 유효성을 확인합니다.
 *
 * @param kartRef 접근할 카트 참조
 * @param tachometerRef 접근할 타코미터 참조
 * @param block 유효한 카트와 타코미터로 실행할 작업
 * @return 블록의 실행 결과, 두 참조 중 하나라도 무효하면 `null`
 * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 접근한 경우
 */
inline fun <T, R> access(kartRef: KartRef, tachometerRef: TachometerRef<T>, block: (Kart, T) -> R): R?
    where T : KartTachometer
{
    if (!Minecraft.getInstance().isSameThread) error("Kart and tachometer can only be accessed in Render Thread")
    val kart = kartRef.handle ?: return null
    val tachometer = tachometerRef.handle ?: return null
    return block.invoke(kart, tachometer)
}
