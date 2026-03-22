package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.engine.BoatEngine
import io.github.oni0nfr1.skid.client.api.engine.GearlikeEngine
import io.github.oni0nfr1.skid.client.api.engine.MKEngine
import io.github.oni0nfr1.skid.client.api.engine.NitroEngine
import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat

@SkidTest
object EngineTypeTest: TestUnit() {
    override val id: String = "engine-type-test"

    override val description: String = """
        이 유닛에서는 SkidMC의 KartEngine 종류 감지가 잘 되는지를 테스트합니다.
        다음의 절차를 따라 테스트를 진행하시면 됩니다.
        1. 카트에 탑승한다.
        2. 출력되는 채팅 메시지의 엔진 정보가 정확한지를 확인한다.
        3. 모든 카트 엔진에 대해 엔진 정보가 정확한지 확인한다.
        
        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    init { register() }

    override fun test(): TestResult {
        KartMountEvents.MOUNT.register { kartEntity, _ ->
            if (!status.testing) return@register
            val engine = kartEntity.kart?.engine ?: return@register

            val engineCategoryName = when (engine) {
                is NitroEngine -> "kartrider-like"
                is GearlikeEngine -> "gear-like"
                is MKEngine -> "mariokart-like"
                is BoatEngine -> "boat"
            }

            client.sendChat("detected engine: ${engine.type.engineName}")
            client.sendChat("detected engine category: $engineCategoryName")
        }

        return TestResult.TESTING
    }
}