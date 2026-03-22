package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.BoatEngine
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.engine.MKEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.utils.visit
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.entity.player.Player
import java.util.Optional

internal class MKEngineImpl(
    override val kart: Kart,
    override val rider: Player,
) : MKEngine {
    override val type = KartEngine.Type.MK

    private val turboGaugeText = '■'
    private val emptyGaugeColor: TextColor = TextColor.fromRgb(0x959595)

    override fun dispatchTachometerEvents(actionBar: Component): KartTachometerEvents.Result {
        val gaugeResult = parseTurboGauge(actionBar)?.let { value ->
            turboGauge = value
            KartTachometerEvents.MK_GAUGE.invoker().onGaugeUpdate(value)
        } ?: KartTachometerEvents.Result.SHOW

        return KartTachometerEvents.Result.finalize(gaugeResult)
    }

    override var turboGauge: Double = 0.0

    override fun parseTurboGauge(actionBar: Component): Double? {
        var totalGaugeCount = 0
        var filledGaugeCount = 0
        actionBar.visit(Style.EMPTY) { style, string ->
            val gaugeCount = string.count { it == turboGaugeText }

            totalGaugeCount += gaugeCount
            if (style.color != emptyGaugeColor) {
                filledGaugeCount += gaugeCount
            }
            Optional.empty<Unit>()
        }

        return if (totalGaugeCount > 0) { // avoid zero-division
            filledGaugeCount.toDouble() / totalGaugeCount.toDouble()
        } else null
    }
}

internal class BoatEngineImpl(
    override val kart: Kart,
    override val rider: Player,
) : BoatEngine {
    override val type = KartEngine.Type.BOAT

    override fun dispatchTachometerEvents(actionBar: Component): KartTachometerEvents.Result {
        return KartTachometerEvents.Result.SHOW // 보트엔진은 타코미터가 없음
    }
}
