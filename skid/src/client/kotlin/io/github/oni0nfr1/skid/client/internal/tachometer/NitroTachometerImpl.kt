package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.internal.utils.visit
import net.minecraft.ChatFormatting
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

internal abstract class NitroTachometerImpl(
    revision: Long,
    kartId: Int,
    engineType: KartEngine.Type,
) : KartTachometerImpl(revision, kartId, engineType) {

    open var speed: Double = 0.0
        protected set
    open var gauge: Double = 0.0
        protected set
    open var nitro: Int = 0
        protected set

    protected open val gaugeText: Char = '|'
    protected open val gaugeColor: TextColor =
        TextColor.fromLegacyFormat(ChatFormatting.GOLD) ?: TextColor.fromRgb(0xFFAA00)
    protected open val speedPattern = Regex("""// (\d+)km/h \\\\""")
    protected open val nitroPattern = Regex("""NITRO x(0|[1-9]\d*)""")

    protected open fun parseGauge(actionBar: Component): Double? {
        var totalGaugeCount = 0
        var filledGaugeCount = 0
        actionBar.visit(Style.EMPTY) { style, string ->
            val gaugeCount = string.count { it == gaugeText }
            totalGaugeCount += gaugeCount
            if (style.color == gaugeColor) {
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

    override fun update(actionBar: Component): TachometerUpdateResult {
        val parsedSpeed = parseSpeed(actionBar)
        val parsedGauge = parseGauge(actionBar)
        val parsedNitro = parseNitro(actionBar)
        if (parsedSpeed == null && parsedGauge == null && parsedNitro == null) {
            return TachometerUpdateResult.notMatched()
        }

        commit(actionBar)

        val speedResult = parsedSpeed?.let { value ->
            speed = value
            KartTachometerEvents.SPEED.invoker().onSpeedUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val nitroResult = parsedNitro?.let { value ->
            nitro = value
            KartTachometerEvents.NITRO.invoker().onNitroUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val gaugeResult = parsedGauge?.let { value ->
            gauge = value
            KartTachometerEvents.GAUGE.invoker().onGaugeUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        return TachometerUpdateResult.matched(
            KartTachometerEvents.Result.finalize(speedResult, nitroResult, gaugeResult)
        )
    }
}
