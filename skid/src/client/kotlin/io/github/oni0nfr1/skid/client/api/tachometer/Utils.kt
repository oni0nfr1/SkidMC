@file:JvmName("TachometerUtils")
package io.github.oni0nfr1.skid.client.api.tachometer

import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.minecraft.client.Minecraft

/**
 * 현재 활성 타코미터에 대한 안전한 참조를 반환합니다.
 *
 * @return 현재 타코미터 참조, 아직 타코미터를 인식하지 못했으면 `null`
 */
val Minecraft.tachometer: TachometerRef<KartTachometer>?
    get() = TachometerManager.currentTachometerOrNull?.let(::TachometerRef)
