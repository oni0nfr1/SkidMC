@file:JvmName("TachometerUtils")
package io.github.oni0nfr1.skid.client.api.tachometer

import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.minecraft.client.Minecraft

val Minecraft.tachometer: TachometerRef<KartTachometer>?
    get() = TachometerManager.currentTachometerOrNull?.let(::TachometerRef)
