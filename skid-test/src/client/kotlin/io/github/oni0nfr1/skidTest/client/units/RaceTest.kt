package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartRaceEvents
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat

@SkidTest
object RaceTest: TestUnit() {
    override val id = "race-test"
    override val description = """
        이 유닛에서는 SkidMC의 Race API가 정상적으로 작동하는지를 테스트합니다.
        테스트 절차는 다음과 같습니다:
        (준비 안 됨)
    """.trimIndent()

    init { register() }

    override fun test(): TestResult {
        KartRaceEvents.START.register { _, _ ->
            if (!status.testing) return@register
            client.sendChat("race started!")
        }

        KartRaceEvents.END.register { _, _, reason ->
            if (!status.testing) return@register
            client.sendChat("race ended! (reason: $reason)")
        }

        KartRaceEvents.LAP.register { _, lap, maxLap, lapTime, _ ->
            if (!status.testing) return@register
            client.sendChat("lap $lap/$maxLap finished at ${lapTime.format()}")
        }

        KartRaceEvents.RANKING.register { race, ranking ->
            if (!status.testing) return@register
            client.sendChat("ranking updated:")
            // TODO
        }

        return TestResult.TESTING
    }
}