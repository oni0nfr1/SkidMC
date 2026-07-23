package io.github.oni0nfr1.skid.client.internal.tachometer.specific

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.tachometer.F1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RallyTachometer
import io.github.oni0nfr1.skid.client.internal.tachometer.GearLikeTachometerImpl
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerUpdateResult
import net.minecraft.network.chat.Component

internal class GearTachometerImpl(engine: KartEngine) :
    GearLikeTachometerImpl(engine), GearTachometer {
    override val gearPattern = Regex("""GEAR (\d+)단""")
}

internal class RallyTachometerImpl(engine: KartEngine) :
    GearLikeTachometerImpl(engine), RallyTachometer

internal class F1TachometerImpl(
    engine: KartEngine,
) : GearLikeTachometerImpl(engine), F1Tachometer {

    private val ersPattern = Regex("""ERS \[(\d{3})]""")

    override var ers: Int = 0
        private set

    private fun parseErs(actionBar: Component): Int? {
        val match = ersPattern.find(actionBar.string) ?: return null
        return match.groupValues[1].toInt()
    }

    override fun update(actionBar: Component): TachometerUpdateResult {
        val parsedErs = parseErs(actionBar)

        val baseResult = super.update(parsedErs != null, actionBar)

        if (baseResult.matched && parsedErs != null) {
            ers = parsedErs
            val ersResult = KartTachometerEvents.ERS.invoker().onErsUpdate(kartRef, parsedErs)

            return TachometerUpdateResult.matched(
                KartTachometerEvents.Result.finalize(baseResult.result, ersResult)
            )
        } else {
            return TachometerUpdateResult.notMatched()
        }
    }
}
