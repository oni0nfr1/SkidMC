package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.*
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.engine.NitroEngineImpl
import io.github.oni0nfr1.skid.client.internal.utils.visit
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors.YELLOW
import net.minecraft.world.entity.player.Player
import java.util.Optional

internal class XEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), XEngine {
    override val type = KartEngine.Type.X
}

internal class EXEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), EXEngine {
    override val type = KartEngine.Type.EX
}

internal class JiuEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), JiuEngine {
    override val type = KartEngine.Type.JIU
}

internal class NewEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), NewEngine {
    override val type = KartEngine.Type.NEW
}

internal class Z7EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), Z7Engine {
    override val type = KartEngine.Type.Z7
}

internal class V1EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), V1Engine {
    override val type = KartEngine.Type.V1
}

internal class A2EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), A2Engine {
    override val type = KartEngine.Type.A2
}

internal class LegacyEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), LegacyEngine {
    override val type = KartEngine.Type.LEGACY
    override val gaugeText: Char = '-'

    override fun parseNitro(actionBar: Component): Int? {
        var sawLeftNitro = false
        var leftNitroYellow = false
        var sawRightNitro = false
        var rightNitroYellow = false
        var waitMultiplierNumber = false
        var multiplierValue: Int? = null

        actionBar.visit(Style.EMPTY){ style: Style, text: String ->
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
                text == " x" -> { waitMultiplierNumber = true }
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

internal class ProEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), ProEngine {
    override val type = KartEngine.Type.PRO
}

internal class RushPlusEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), RushPlusEngine {
    override val type = KartEngine.Type.RUSHPLUS
    override val speedPattern: Regex = Regex("""// (\d+(?:\.\d)?)km/h \\\\""")

    override fun parseSpeed(actionBar: Component): Double? {
        val raw = actionBar.string
        val match = speedPattern.find(raw) ?: return null
        val speed = match.groupValues[1].toDouble()

        return speed
    }
}

internal class ChargeEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), ChargeEngine {
    override val type = KartEngine.Type.CHARGE
}

internal class N1EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), N1Engine {
    override val type = KartEngine.Type.N1
}

internal class KeyEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), KeyEngine {
    override val type = KartEngine.Type.KEY
}
