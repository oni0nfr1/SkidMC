package io.github.oni0nfr1.skid.client.internal.tachometer.specific

import io.github.oni0nfr1.skid.client.internal.utils.visit
import net.minecraft.util.CommonColors.YELLOW
import io.github.oni0nfr1.skid.client.api.tachometer.*
import io.github.oni0nfr1.skid.client.internal.tachometer.NitroTachometerImpl
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerUpdateResult
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.util.Optional

internal class XTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.X), XTachometer

internal class EXTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.EX), EXTachometer

internal class JiuTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.JIU), JiuTachometer

internal class NewTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.NEW), NewTachometer

internal class Z7TachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.Z7), Z7Tachometer

internal class V1TachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.V1), V1Tachometer

internal class A2TachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.A2), A2Tachometer

internal class LegacyTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.LEGACY), LegacyTachometer {
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

internal class ProTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.PRO), ProTachometer

internal class ChargeTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.CHARGE), ChargeTachometer

internal class N1TachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.N1), N1Tachometer

internal class KeyTachometerImpl(revision: Long, kartId: Int) :
    NitroTachometerImpl(revision, kartId, KartEngine.Type.KEY), KeyTachometer

internal class RushPlusTachometerImpl(
    revision: Long,
    kartId: Int,
) : NitroTachometerImpl(revision, kartId, KartEngine.Type.RUSHPLUS), RushPlusTachometer {

    private val fusionNitroPattern = Regex("""FUSION x(0|[1-9]\d*)""")
    private val fusionLabelPattern = Regex("""\bFUSION\b""")
    override val speedPattern: Regex = Regex("""// (\d+(?:\.\d)?)km/h \\\\""")

    override var fusionActive: Boolean = false
        private set

    override fun parseNitro(actionBar: Component): Int? {
        val raw = actionBar.string
        val fusionNitro = fusionNitroPattern.find(raw)?.groupValues?.get(1)?.toInt()
        return fusionNitro ?: super.parseNitro(actionBar)
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        val baseResult = super.update(actionBar)
        val fusionActive = fusionLabelPattern.containsMatchIn(actionBar.string)
        if (!baseResult.matched && !fusionActive) {
            return baseResult
        }

        this.fusionActive = fusionActive
        commit(actionBar)
        return if (baseResult.matched) baseResult else TachometerUpdateResult.matched(baseResult.result)
    }
}
