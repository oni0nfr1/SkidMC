package io.github.oni0nfr1.skid.client.internal.tachometer.specific

import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.tachometer.F1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RallyTachometer
import io.github.oni0nfr1.skid.client.internal.tachometer.GearlikeTachometerImpl
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerUpdateResult
import net.minecraft.network.chat.Component

internal class GearTachometerImpl(revision: Long, kartId: Int) :
    GearlikeTachometerImpl(revision, kartId, KartEngine.Type.GEAR), GearTachometer {
    override val gearPattern = Regex("""GEAR (\d+)단""")
}

internal class RallyTachometerImpl(revision: Long, kartId: Int) :
    GearlikeTachometerImpl(revision, kartId, KartEngine.Type.RALLY), RallyTachometer

internal class F1TachometerImpl(
    revision: Long,
    kartId: Int,
) : GearlikeTachometerImpl(revision, kartId, KartEngine.Type.F1), F1Tachometer {

    private val ersPattern = Regex("""ERS \[(\d{3})]""")

    override var ers: Int = 0
        private set

    private fun parseErs(actionBar: Component): Int? {
        val match = ersPattern.find(actionBar.string) ?: return null
        return match.groupValues[1].toInt()
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        val baseResult = super.update(actionBar)
        val parsedErs = parseErs(actionBar)
        if (!baseResult.matched && parsedErs == null) {
            return baseResult
        }

        commit(actionBar)

        val ersResult = parsedErs?.let { value ->
            ers = value
            KartTachometerEvents.ERS.invoker().onErsUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        return if (baseResult.matched) {
            TachometerUpdateResult.matched(KartTachometerEvents.Result.finalize(baseResult.result, ersResult))
        } else {
            TachometerUpdateResult.matched(ersResult)
        }
    }
}
