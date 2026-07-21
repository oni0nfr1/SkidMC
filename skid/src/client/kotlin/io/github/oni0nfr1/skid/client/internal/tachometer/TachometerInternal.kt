package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.api.utils.KartType
import net.minecraft.network.chat.Component

internal data class TachometerUpdateResult(
    val matched: Boolean,
    val result: KartTachometerEvents.Result,
) {
    companion object {
        fun notMatched() = TachometerUpdateResult(false, KartTachometerEvents.Result.SHOW)
        fun matched(result: KartTachometerEvents.Result) = TachometerUpdateResult(true, result)
    }
}

internal interface TachometerInternal : KartTachometer {
    val revision: Long
    val kartId: Int
    val type: KartType<*>

    fun update(actionBar: Component): TachometerUpdateResult
    fun update(additionalMatched: Boolean, actionBar: Component): TachometerUpdateResult

    fun tick() { }
}
