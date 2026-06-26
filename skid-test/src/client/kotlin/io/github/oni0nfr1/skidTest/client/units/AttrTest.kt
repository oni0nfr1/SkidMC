package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartAttrEvents
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat

@SkidTest
object AttrTest: TestUnit() {
    override val id = "attr-test"
    override val description = """
        이 유닛에서는 SkidMC 마크라이더 S2C 어트리뷰트 감지 기능이 정상적으로 작동하는지 감지하며,
        또한 S2C 어트리뷰트의 값이 상황에 따라 어떻게 변하는지를 테스트합니다.
        테스트할 어트리뷰트 값들:
        - data-engine
        - data-engine-real
        - data-max-boost-count
        - data-team-boost-count
        - state-drift
        - state-boost
        - skill-instant-boost
        - state-instant-boost
        - data-max-lap
        - data-current-lap
        - data-performance-limit-level
        - data-tire
        - data-is-bike
        - state-allow-model-rotation
        
        어트리뷰트 변화가 감지되지 않을 경우 테스트는 실패합니다.
        어트리뷰트 변화가 감지되나 기존의 문서에 기록된 것과 다르게 작동한다면, 문서를 갱신합니다.
        기존과의 불일치 확인시 다음 메이저 업데이트까지 API 측에서는 기존 방식과 같은 방식으로 표시되도록 런타임에 재매핑하도록 만드는 것을 권장합니다.
    """.trimIndent()

    init { register() }

    private fun chatIfTesting(message: String) {
        if (!status.testing) return
        client.sendChat(message)
    }

    override fun test(): TestResult {
        KartAttrEvents.ID_ENGINE.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-engine = $value")
        }
        KartAttrEvents.ID_ENGINE_REAL.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-engine-real = $value")
        }
        KartAttrEvents.CAP_NITRO_COUNT.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-max-boost-count = $value")
        }
        KartAttrEvents.STATE_TEAM_NITRO_COUNT.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-team-boost-count = $value")
        }
        KartAttrEvents.STATE_DRAFT_ACCEL.register { kart, value ->
            chatIfTesting("kart#${kart.id} state-draft = $value")
        }
        KartAttrEvents.STATE_DRIFTING.register { kart, value ->
            chatIfTesting("kart#${kart.id} state-drift = $value")
        }
        KartAttrEvents.STATE_NITRO.register { kart, value ->
            chatIfTesting("kart#${kart.id} state-boost = $value")
        }
        KartAttrEvents.CAN_IBOOST.register { kart, value ->
            chatIfTesting("kart#${kart.id} skill-instant-boost = $value")
        }
        KartAttrEvents.STATE_IBOOST.register { kart, value ->
            chatIfTesting("kart#${kart.id} state-instant-boost = $value")
        }
        KartAttrEvents.CTX_MAX_LAP.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-max-lap = $value")
        }
        KartAttrEvents.CTX_CURRENT_LAP.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-current-lap = $value")
        }
        KartAttrEvents.CTX_PERF_LIMIT.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-performance-limit-level = $value")
        }
        KartAttrEvents.ID_TIRE.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-tire = $value")
        }
        KartAttrEvents.ID_BODY_TYPE.register { kart, value ->
            chatIfTesting("kart#${kart.id} data-is-bike = $value")
        }
        KartAttrEvents.STATE_MODEL_ROTATION_ALLOWED.register { kart, value ->
            chatIfTesting("kart#${kart.id} state-allow-model-rotation = $value")
        }
        return TestResult.TESTING
    }
}
