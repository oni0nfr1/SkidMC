package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
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

internal interface TachometerInternal {
    val revision: Long
    val kartId: Int
    val type: KartEngine.Type

    fun update(actionBar: Component): TachometerUpdateResult
}
