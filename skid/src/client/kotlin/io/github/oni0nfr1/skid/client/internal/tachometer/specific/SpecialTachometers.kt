package io.github.oni0nfr1.skid.client.internal.tachometer.specific

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.tachometer.BoatTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.DSTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.MKTachometer
import io.github.oni0nfr1.skid.client.internal.utils.visit
import io.github.oni0nfr1.skid.client.internal.tachometer.KartTachometerImpl
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerUpdateResult
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

internal class MKTachometerImpl(
    engine: KartEngine,
) : KartTachometerImpl(engine), MKTachometer {

    private val turboGaugeText = '■'
    private val emptyGaugeColor: TextColor = TextColor.fromRgb(0x959595)

    override var turboGauge: Double = 0.0
        private set

    private fun parseTurboGauge(actionBar: Component): Double? {
        var totalGaugeCount = 0
        var filledGaugeCount = 0
        actionBar.visit(Style.EMPTY) { style, string ->
            val gaugeCount = string.count { it == turboGaugeText }
            totalGaugeCount += gaugeCount
            if (style.color != emptyGaugeColor) {
                filledGaugeCount += gaugeCount
            }
            Optional.empty<Unit>()
        }

        return if (totalGaugeCount > 0) {
            filledGaugeCount.toDouble() / totalGaugeCount.toDouble()
        } else null
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        val parsedGauge = parseTurboGauge(actionBar) ?: return TachometerUpdateResult.notMatched()

        commit(actionBar)

        turboGauge = parsedGauge
        val result = KartTachometerEvents.TURBO_GAUGE.invoker().onGaugeUpdate(kartRef, parsedGauge)
        return TachometerUpdateResult.matched(result)
    }

    override fun update(
        additionalMatched: Boolean,
        actionBar: Component
    ): TachometerUpdateResult {
        return update(actionBar)
    }
}

internal class DSTachometerImpl(
    engine: KartEngine,
) : KartTachometerImpl(engine), DSTachometer {

    private val turboGaugeText = '■'
    private val emptyGaugeColor: TextColor = TextColor.fromRgb(0x959595)

    override var turboGauge: Double = 0.0
        private set

    private fun parseTurboGauge(actionBar: Component): Double? {
        var totalGaugeCount = 0
        var filledGaugeCount = 0
        actionBar.visit(Style.EMPTY) { style, string ->
            val gaugeCount = string.count { it == turboGaugeText }
            totalGaugeCount += gaugeCount
            if (style.color != emptyGaugeColor) {
                filledGaugeCount += gaugeCount
            }
            Optional.empty<Unit>()
        }

        return if (totalGaugeCount > 0) {
            filledGaugeCount.toDouble() / totalGaugeCount.toDouble()
        } else null
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        val parsedGauge = parseTurboGauge(actionBar) ?: return TachometerUpdateResult.notMatched()

        commit(actionBar)

        turboGauge = parsedGauge
        val result = KartTachometerEvents.TURBO_GAUGE.invoker().onGaugeUpdate(kartRef, parsedGauge)
        return TachometerUpdateResult.matched(result)
    }

    override fun update(
        additionalMatched: Boolean,
        actionBar: Component
    ): TachometerUpdateResult {
        return update(actionBar)
    }
}

internal class BoatTachometerImpl(
    engine: KartEngine,
) : KartTachometerImpl(engine), BoatTachometer {
    override fun update(actionBar: Component): TachometerUpdateResult {
        commit(actionBar)
        return TachometerUpdateResult.matched(KartTachometerEvents.Result.SHOW)
    }

    override fun update(
        additionalMatched: Boolean,
        actionBar: Component
    ): TachometerUpdateResult {
        return update(actionBar)
    }
}
