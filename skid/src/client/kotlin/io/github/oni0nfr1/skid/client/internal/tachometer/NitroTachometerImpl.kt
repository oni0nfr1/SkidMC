package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.internal.utils.visit
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.utils.KartType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

internal abstract class NitroTachometerImpl(
    revision: Long,
    kartId: Int,
    engineType: KartType<*>,
) : KartTachometerImpl(revision, kartId, engineType) {

    open var speed: Double = 0.0
        protected set
    open var gauge: Double = 0.0
        protected set
    open var nitro: Int = 0
        protected set

    protected open val gaugeText: Char = '|'
    protected open val emptyGaugeColor: TextColor = TextColor.fromRgb(0x959595)
    protected open val speedPattern = Regex("""// (\d+)km/h \\\\""")
    protected open val nitroPattern = Regex("""NITRO x(0|[1-9]\d*)""")

    protected open fun parseGauge(actionBar: Component): Double? {
        var totalGaugeCount = 0
        var filledGaugeCount = 0
        actionBar.visit(Style.EMPTY) { style, string ->
            val gaugeCount = string.count { it == gaugeText }
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

    protected open fun parseSpeed(actionBar: Component): Double? {
        val match = speedPattern.find(actionBar.string) ?: return null
        return match.groupValues[1].toDouble()
    }

    protected open fun parseNitro(actionBar: Component): Int? {
        val match = nitroPattern.find(actionBar.string) ?: return null
        return match.groupValues[1].toInt()
    }

    override fun update(additionalMatched: Boolean, actionBar: Component): TachometerUpdateResult {
        val parsedSpeed = parseSpeed(actionBar)
        val parsedGauge = parseGauge(actionBar)
        val parsedNitro = parseNitro(actionBar)
        if (!additionalMatched || parsedSpeed == null || parsedGauge == null || parsedNitro == null) {
            return TachometerUpdateResult.notMatched()
        }

        commit(actionBar)

        speed = parsedSpeed
        val speedResult = KartTachometerEvents.SPEED.invoker().onSpeedUpdate(parsedSpeed)
        nitro = parsedNitro
        val nitroResult = KartTachometerEvents.NITRO.invoker().onNitroUpdate(parsedNitro)
        gauge = parsedGauge
        val gaugeResult = KartTachometerEvents.GAUGE.invoker().onGaugeUpdate(parsedGauge)

        return TachometerUpdateResult.matched(
            KartTachometerEvents.Result.finalize(speedResult, nitroResult, gaugeResult)
        )
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        return update(true, actionBar)
    }
}
