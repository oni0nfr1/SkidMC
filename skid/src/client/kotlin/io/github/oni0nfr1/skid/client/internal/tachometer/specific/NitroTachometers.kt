package io.github.oni0nfr1.skid.client.internal.tachometer.specific

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.tachometer.*
import io.github.oni0nfr1.skid.client.internal.tachometer.NitroTachometerImpl
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerUpdateResult
import io.github.oni0nfr1.skid.client.internal.utils.visit
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors.YELLOW
import java.util.Optional

internal class XTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), XTachometer

internal class EXTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), EXTachometer

internal class JiuTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), JiuTachometer

internal class NewTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), NewTachometer

internal class Z7TachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), Z7Tachometer

internal class V1TachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), V1Tachometer {

    override var exceedGauge: Float = 0f

    override fun tick() {
        super.tick()
        val client = Minecraft.getInstance()
        exceedGauge = client.player?.experienceProgress ?: return
    }
}

internal class A2TachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), A2Tachometer

internal class LegacyTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), LegacyTachometer {
    override val gaugeText: Char = '-'

    override fun parseNitro(actionBar: Component): Int? {
        var sawLeftNitro = false
        var leftNitroYellow = false
        var sawRightNitro = false
        var rightNitroYellow = false
        var waitMultiplierNumber = false
        var multiplierValue: Int? = null

        actionBar.visit(Style.EMPTY) { style: Style, text: String ->
            if (text.isEmpty()) return@visit Optional.empty()
            val color = style.color?.value
            when {
                text.contains(" / NITRO [") -> {
                    sawLeftNitro = true
                    leftNitroYellow = color == YELLOW
                }
                text == "NITRO" -> {
                    sawRightNitro = true
                    rightNitroYellow = color == YELLOW
                }
                text == " x" -> waitMultiplierNumber = true
                waitMultiplierNumber -> {
                    multiplierValue = text.toIntOrNull()
                    waitMultiplierNumber = false
                }
            }
            Optional.empty<Unit>()
        }

        multiplierValue?.let { return it + 1 }
        if (!sawLeftNitro || !sawRightNitro) return null
        if (rightNitroYellow) return 2
        if (leftNitroYellow) return 1
        return 0
    }
}

internal class ProTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), ProTachometer

internal class ChargeTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), ChargeTachometer {

    override var chargerGauge: Float = 0f

    override fun tick() {
        super.tick()
        val client = Minecraft.getInstance()
        chargerGauge = client.player?.experienceProgress ?: return
    }
}

internal class N1TachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), N1Tachometer

internal class KeyTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), KeyTachometer

internal class RushPlusTachometerImpl(
    engine: KartEngine,
) : NitroTachometerImpl(engine), RushPlusTachometer {

    private val fusionNitroPattern = Regex("""FUSION x(0|[1-9]\d*)""")
    private val fusionLabelPattern = Regex("""\bFUSION\b""")
    override val speedPattern: Regex = Regex("""// (\d+(?:\.\d)?)km/h \\\\""")

    override var fusionActive: Boolean = false
        private set
    override var exceedGauge: Float = 0f

    override fun parseNitro(actionBar: Component): Int? {
        val raw = actionBar.string
        val fusionNitro = fusionNitroPattern.find(raw)?.groupValues?.get(1)?.toInt()
        return fusionNitro ?: super.parseNitro(actionBar)
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        val fusionActive = fusionLabelPattern.containsMatchIn(actionBar.string)

        val baseResult = super.update(actionBar)

        if (baseResult.matched) {
            this.fusionActive = fusionActive
        }
        return baseResult
    }

    override fun tick() {
        super.tick()
        val client = Minecraft.getInstance()
        exceedGauge = client.player?.experienceProgress ?: return
    }
}

internal class SRTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), SRTachometer

internal class RXTachometerImpl(engine: KartEngine) :
    NitroTachometerImpl(engine), RXTachometer
