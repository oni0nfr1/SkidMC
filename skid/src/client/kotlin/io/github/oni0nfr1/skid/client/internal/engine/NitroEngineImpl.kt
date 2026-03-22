package io.github.oni0nfr1.skid.client.internal.engine

import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.utils.visit
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.entity.player.Player
import java.util.Optional

internal abstract class NitroEngineImpl(
    val kart: Kart,
    val rider: Player,
) {

    open var gauge: Double = 0.0
    open var speed: Double = 0.0
    open var nitro: Int = 0

    open val gaugeText: Char = '|'
    open val gaugeColor: TextColor = TextColor.fromLegacyFormat(ChatFormatting.GOLD) ?: TextColor.fromRgb(0xFFAA00)
    open val speedPattern = Regex("""// (\d+)km/h \\\\""")
    open val nitroPattern = Regex("""NITRO x(0|[1-9]\d*)""")

    open fun dispatchTachometerEvents(actionBar: Component): KartTachometerEvents.Result {
        val speedResult = parseSpeed(actionBar)?.let { value ->
            speed = value
            KartTachometerEvents.SPEED.invoker().onSpeedUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val nitroResult = parseNitro(actionBar)?.let { value ->
            nitro = value
            KartTachometerEvents.NITRO.invoker().onNitroUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val gaugeResult = parseGauge(actionBar)?.let { value ->
            gauge = value
            KartTachometerEvents.GAUGE.invoker().onGaugeUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        return KartTachometerEvents.Result.finalize(speedResult, nitroResult, gaugeResult)
    }

    open fun parseGauge(actionBar: Component): Double? {
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

        return if (totalGaugeCount > 0) { // avoid zero-division
            filledGaugeCount.toDouble() / totalGaugeCount.toDouble()
        } else null
    }

    open fun parseSpeed(actionBar: Component): Double? {
        val raw = actionBar.string
        val match = speedPattern.find(raw) ?: return null
        val speed = match.groupValues[1].toInt()

        return speed.toDouble()
    }

    open fun parseNitro(actionBar: Component): Int? {
        val raw = actionBar.string
        val match = nitroPattern.find(raw) ?: return null
        val nitro = match.groupValues[1].toInt()

        return nitro
    }
}
