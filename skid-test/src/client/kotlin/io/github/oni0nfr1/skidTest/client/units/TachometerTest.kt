package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.tachometer.BoatTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.F1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearlikeTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.MKTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.NitroTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RushPlusTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.tachometer
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.renderDebugPanel
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

@SkidTest
object TachometerTest: TestUnit() {
    override val id = "tachometer-test"
    override val description = """
        이 유닛에서는 SkidMC의 KartTachometer 구현이 정상적으로 작동하는지를 테스트합니다.
        다음의 절차를 따라 테스트를 진행하시면 됩니다.
        1. 카트에 탑승한다.
        2. 화면 좌상단 디버그 패널에 데이터가 잘 출력되는지 확인한다.
        3. 모든 엔진에 대해 정상적으로 값이 출력되는지 확인한다.
    """.trimIndent()

    init { register() }

    override fun test(): TestResult {
        return TestResult.TESTING
    }

    override fun drawHud(guiGraphics: GuiGraphics, tickDelta: DeltaTracker) {
        if (!status.testing) return
        var engineData = "[SKIDMC DEBUG PANEL]\n"

        val tachometer = client.tachometer
        engineData += tachometer?.access {
            engineData += "engine-code: ${type.engineCode}\n"
            engineData += "engine-name: ${type.engineName}\n"
            engineData += "isDummy: ${type.isDummy}\n"

            when (this) {
                is NitroTachometer -> nitroEngineInfo(this)
                is GearlikeTachometer -> gearlikeEngineInfo(this)
                is MKTachometer -> marioEngineInfo(this)
                is BoatTachometer -> boatEngineInfo(this)
            }
        } ?: "tachometer: null\n"

        guiGraphics.renderDebugPanel(engineData, 10, 10, shadow = false)
    }

    private fun nitroEngineInfo(tachometer: NitroTachometer): String {
        var info = ""
        info += "speed: ${tachometer.speed}\n"
        info += "nitro: ${tachometer.nitro}\n"
        info += "gauge: ${tachometer.gauge}\n"
        if (tachometer is RushPlusTachometer) info += "fusion-active: ${tachometer.fusionActive}\n"
        return info
    }

    private fun gearlikeEngineInfo(tachometer: GearlikeTachometer): String {
        var info = ""
        info += "speed: ${tachometer.speed}\n"
        info += "gear: ${tachometer.gear}\n"
        info += "rpm: ${tachometer.rpm}\n"
        if (tachometer is F1Tachometer) info += "ers: ${tachometer.ers}\n"
        return info
    }

    private fun marioEngineInfo(tachometer: MKTachometer): String {
        var info = ""
        info += "turbo-gauge: ${tachometer.turboGauge}\n"
        return info
    }

    private fun boatEngineInfo (engine: BoatTachometer): String {
        val info = ""
        // NOTHING
        return info
    }
}
