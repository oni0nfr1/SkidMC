package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.engine.BoatEngine
import io.github.oni0nfr1.skid.client.api.engine.F1Engine
import io.github.oni0nfr1.skid.client.api.engine.GearlikeEngine
import io.github.oni0nfr1.skid.client.api.engine.MKEngine
import io.github.oni0nfr1.skid.client.api.engine.NitroEngine
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.renderDebugPanel
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

@SkidTest
object EngineImplTest: TestUnit() {
    override val id = "engine-impl-test"
    override val description = """
        이 유닛에서는 SkidMC의 KartEngine 구현이 정상적으로 작동하는지를 테스트합니다.
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

        val engine = client.player?.ridingKart?.engine ?: return
        var engineData = "[SKIDMC DEBUG PANEL]\n"
        engineData += "engine-code: ${engine.type.engineCode}\n"
        engineData += "engine-name: ${engine.type.engineName}\n"
        engineData += "isDummy: ${engine.type.isDummy}\n"

        engineData += when (engine) {
            is NitroEngine -> nitroEngineInfo(engine)
            is GearlikeEngine -> gearlikeEngineInfo(engine)
            is MKEngine -> marioEngineInfo(engine)
            is BoatEngine -> boatEngineInfo(engine)
        }

        guiGraphics.renderDebugPanel(
            engineData,
            10, 10,
            0xFFFFFFFF.toInt(),
            shadow = false,
        )
    }

    private fun nitroEngineInfo(engine: NitroEngine): String {
        var info = ""
        info += "speed: ${engine.speed}\n"
        info += "nitro: ${engine.nitro}\n"
        info += "gauge: ${engine.gauge}\n"
        return info
    }

    private fun gearlikeEngineInfo(engine: GearlikeEngine): String {
        var info = ""
        info += "speed: ${engine.speed}\n"
        info += "gear: ${engine.gear}\n"
        info += "rpm: ${engine.rpm}\n"
        if (engine is F1Engine) info += "ers: ${engine.ers}\n"
        return info
    }

    private fun marioEngineInfo(engine: MKEngine): String {
        var info = ""
        info += "turbo-gauge: ${engine.turboGauge}\n"
        return info
    }

    private fun boatEngineInfo (engine: BoatEngine): String {
        val info = ""
        // NOTHING
        return info
    }
}