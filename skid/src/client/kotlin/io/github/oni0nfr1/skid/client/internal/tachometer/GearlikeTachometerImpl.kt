package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.internal.utils.visit
import net.minecraft.ChatFormatting
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

internal abstract class GearlikeTachometerImpl(
    revision: Long,
    kartId: Int,
    engineType: KartEngine.Type,
) : KartTachometerImpl(revision, kartId, engineType) {

    open var speed: Double = 0.0
        protected set
    open var rpm: Double = 0.0
        protected set
    open var gear: Int = 0
        protected set

    protected open val rpmText: Char = '|'
    protected open val rpmColors: Array<TextColor> = arrayOf(
        TextColor.fromLegacyFormat(ChatFormatting.GREEN) ?: TextColor.fromRgb(0x55FF55),
        TextColor.fromLegacyFormat(ChatFormatting.YELLOW) ?: TextColor.fromRgb(0xFFFF55),
        TextColor.fromLegacyFormat(ChatFormatting.RED) ?: TextColor.fromRgb(0xFF5555),
    )
    protected open val speedPattern = Regex("""// (\d+)km/h \\\\""")
    protected open val gearPattern = Regex("""GEAR \[(\d+)]""")

    protected open fun parseRpm(actionBar: Component): Double? {
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

        return if (totalGaugeCount > 0) {
            filledGaugeCount.toDouble() / totalGaugeCount.toDouble()
        } else null
    }

    protected open fun parseSpeed(actionBar: Component): Double? {
        val match = speedPattern.find(actionBar.string) ?: return null
        return match.groupValues[1].toInt().toDouble()
    }

    protected open fun parseGear(actionBar: Component): Int? {
        val match = gearPattern.find(actionBar.string) ?: return null
        return match.groupValues[1].toInt()
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        val parsedSpeed = parseSpeed(actionBar)
        val parsedRpm = parseRpm(actionBar)
        val parsedGear = parseGear(actionBar)
        if (parsedSpeed == null && parsedRpm == null && parsedGear == null) {
            return TachometerUpdateResult.notMatched()
        }

        commit(actionBar)

        val speedResult = parsedSpeed?.let { value ->
            speed = value
            KartTachometerEvents.SPEED.invoker().onSpeedUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val rpmResult = parsedRpm?.let { value ->
            rpm = value
            KartTachometerEvents.RPM.invoker().onRpmUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        val gearResult = parsedGear?.let { value ->
            gear = value
            KartTachometerEvents.GEAR.invoker().onGearUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        return TachometerUpdateResult.matched(
            KartTachometerEvents.Result.finalize(speedResult, rpmResult, gearResult)
        )
    }
}
