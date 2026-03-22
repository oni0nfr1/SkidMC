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

internal abstract class GearlikeEngineImpl(
    val kart: Kart,
    val rider: Player,
) {

    open var rpm: Double = 0.0
    open var speed: Double = 0.0
    open var gear: Int = 0

    val rpmText: Char = '|'
    val rpmColors: Array<TextColor> = arrayOf(
        TextColor.fromLegacyFormat(ChatFormatting.GREEN) ?: TextColor.fromRgb(0x55FF55), // 왼쪽 게이지 (비어있으면 0x959595)
        TextColor.fromLegacyFormat(ChatFormatting.YELLOW) ?: TextColor.fromRgb(0xFFFF55), // 오른쪽 게이지 왼쪽 절반 (비어있으면 0x959595)
        TextColor.fromLegacyFormat(ChatFormatting.RED) ?: TextColor.fromRgb(0xFF5555), // 오른쪽 게이지 오른쪽 절반 (비어있으면 0x000000)
    )
    open val speedPattern = Regex("""// (\d+)km/h \\\\""")
    open val gearPattern = Regex("""GEAR \[(\d+)]""")

    open fun parseRpm(actionBar: Component): Double? {
        var totalGaugeCount = 0
        var filledGaugeCount = 0
        actionBar.visit(Style.EMPTY) { style, string ->
            val gaugeCount = string.count { it == rpmText }

            totalGaugeCount += gaugeCount
            if (style.color in rpmColors) {
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

    open fun parseGear(actionBar: Component): Int? {
        val raw = actionBar.string
        val match = gearPattern.find(raw) ?: return null
        val gear = match.groupValues[1].toInt()

        return gear
    }

    open fun dispatchTachometerEvents(actionBar: Component): KartTachometerEvents.Result {
        val speedResult = parseSpeed(actionBar)?.let { value ->
            speed = value
            KartTachometerEvents.SPEED.invoker().onSpeedUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val rpmResult = parseRpm(actionBar)?.let { value ->
            rpm = value
            KartTachometerEvents.RPM.invoker().onRpmUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val gearResult = parseGear(actionBar)?.let { value ->
            gear = value
            KartTachometerEvents.GEAR.invoker().onGearUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        return KartTachometerEvents.Result.finalize(speedResult, rpmResult, gearResult)
    }
}
