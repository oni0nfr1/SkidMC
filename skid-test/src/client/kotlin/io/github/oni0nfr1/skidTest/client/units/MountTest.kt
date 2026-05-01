package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat

@SkidTest
object MountTest: TestUnit() {
    override val id: String = "mount-test"

    override val description: String = """
        이 유닛에서는 SkidMC의 KartMountEvents가 잘 작동하는지를 테스트합니다.
        다음의 절차에 따라 테스트를 진행하시면 됩니다.
        1. 아무 카트나 탑승하고, 카트 탑승 확인 메시지가 정상적으로 출력되는지 확인한다.
        2. 해당 카트에서 내리고, 카트 하차 확인 메시지가 정상적으로 출력되는지 확인한다.
        3. 카트에서 타고 내릴 때 출력되는 플레이어/카트의 엔티티 ID가 같은지를 확인한다.
        4. 멀티방에서 다른 플레이어를 관전/관전 이동을 한다.
        5. 
        
        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    init { register() }

    override fun test(): TestResult {
        KartMountEvents.MOUNT.register { kartEntity, rider ->
            if (!status.testing) return@register
            client.sendChat("now ${rider.name.string} (player#${rider.id}) is riding a kart#${kartEntity.id}!")
        }

        KartMountEvents.DISMOUNT.register { kartEntity, rider ->
            if (!status.testing) return@register
            client.sendChat("now ${rider.name.string} (player#${rider.id}) is not riding a kart#${kartEntity.id}!)")
        }

        KartMountEvents.SPECTATE.register { kartEntity, rider, target ->
            if (!status.testing) return@register
            client.sendChat("now ${rider.name.string} (player#${rider.id}) is spectating ${target.name.string} (player#${target.id}) riding a kart#${kartEntity.id}!")
        }

        KartMountEvents.SPECTATE_END.register { kartEntity, rider, target ->
            if (!status.testing) return@register
            client.sendChat("now ${rider.name.string} (player#${rider.id}) is not spectating ${target.name.string} (player#${target.id}) riding a kart#${kartEntity.id}!")
        }
        return TestResult.TESTING
    }
}
