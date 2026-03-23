package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.RiderAttrEvents
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
        - kart-engine
        - kart-engine-real
        - kart-max-boost-count
        - is-drifting
        - dualboost-state
        - active-instant-boost
        - max-lap
        - kart-performance-limit-level
        - kart-tire
        
        어트리뷰트 변화가 감지되지 않을 경우 테스트는 실패합니다.
        어트리뷰트 변화가 감지되나 기존의 문서에 기록된 것과 다르게 작동한다면, 문서를 갱신합니다.
        기존과의 불일치 확인시 다음 메이저 업데이트까지 API 측에서는 기존 방식과 같은 방식으로 표시되도록 런타임에 재매핑하도록 만드는 것을 권장합니다.
    """.trimIndent()

    init { register() }

    override fun test(): TestResult {
        RiderAttrEvents.KART_ENGINE.register { player, value ->
            client.sendChat("${player.name}'s kart-engine = $value")
        }
        RiderAttrEvents.KART_ENGINE_REAL.register { player, value ->
            client.sendChat("${player.name}'s kart-engine-real = $value")
        }
        RiderAttrEvents.KART_MAX_BOOST_COUNT.register { player, value ->
            client.sendChat("${player.name}'s kart-max-boost-count = $value")
        }
        RiderAttrEvents.IS_DRIFTING.register { player, value ->
            client.sendChat("${player.name}'s is-drifting = $value")
        }
        RiderAttrEvents.ACTIVE_INSTANT_BOOST.register { player, value ->
            client.sendChat("${player.name}'s active-instant-boost = $value")
        }
        RiderAttrEvents.DUALBOOST_STATE.register { player, value ->
            client.sendChat("${player.name}'s dualboost-state = $value")
        }
        RiderAttrEvents.MAX_LAP.register { player, value ->
            client.sendChat("${player.name}'s max-lap = $value")
        }
        RiderAttrEvents.KART_PERFORMANCE_LIMIT_LEVEL.register { player, value ->
            client.sendChat("${player.name}'s kart-performance-limit-level = $value")
        }
        RiderAttrEvents.KART_TIRE.register { player, value ->
            client.sendChat("${player.name}'s kart-tire = $value")
        }
        return TestResult.TESTING
    }
}