package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.F1Engine
import io.github.oni0nfr1.skid.client.api.engine.GearEngine
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.engine.RallyEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.engine.GearlikeEngineImpl
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

internal class GearEngineImpl(kart: Kart, rider: Player) : GearlikeEngineImpl(kart, rider), GearEngine {
    override val gearPattern = Regex("""GEAR (\d+)단""")

    override val type = KartEngine.Type.GEAR
}

internal class F1EngineImpl(kart: Kart, rider: Player) : GearlikeEngineImpl(kart, rider), F1Engine {
    override val type = KartEngine.Type.F1

    override var ers: Int = 0

    private val ersPattern = Regex("""ERS \[(\d{3})]""")

    override fun parseErs(actionBar: Component): Int? {
        val raw = actionBar.string
        val match = ersPattern.find(raw) ?: return null
        val ers = match.groupValues[1].toInt()

        return ers
    }

    override fun dispatchTachometerEvents(actionBar: Component): KartTachometerEvents.Result {
        val superResult = super.dispatchTachometerEvents(actionBar)

        val ersResult = parseErs(actionBar)?.let { value ->
            ers = value
            KartTachometerEvents.ERS.invoker().onErsUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        return KartTachometerEvents.Result.finalize(superResult, ersResult)
    }
}

internal class RallyEngineImpl(kart: Kart, rider: Player) : GearlikeEngineImpl(kart, rider), RallyEngine {
    override val type = KartEngine.Type.RALLY
}
