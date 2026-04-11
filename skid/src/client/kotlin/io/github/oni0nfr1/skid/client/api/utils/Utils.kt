@file:JvmName("SkidUtils")

package io.github.oni0nfr1.skid.client.api.utils

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.StaleKartException
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.NoTachometerException
import io.github.oni0nfr1.skid.client.api.tachometer.TachometerRef
import net.minecraft.client.Minecraft

inline fun <T, R> access(kartRef: KartRef, tachometerRef: TachometerRef<T>, block: (Kart, T) -> R): R?
    where T : KartTachometer
{
    if (Minecraft.getInstance().isSameThread) {
        val kart = kartRef.handle ?: return null
        val tachometer = tachometerRef.handle ?: return null
        return block.invoke(kart, tachometer)
    } else {
        error("Kart and tachometer can only be accessed in Render Thread")
    }
}

inline fun <T, R> forceAccess(kartRef: KartRef, tachometerRef: TachometerRef<T>, block: (Kart, T) -> R): R?
    where T : KartTachometer
{
    if (Minecraft.getInstance().isSameThread) {
        val kart = kartRef.handle ?: throw StaleKartException()
        val tachometer = tachometerRef.handle ?: throw NoTachometerException()
        return block.invoke(kart, tachometer)
    } else {
        error("Kart and tachometer can only be accessed in Render Thread")
    }
}

