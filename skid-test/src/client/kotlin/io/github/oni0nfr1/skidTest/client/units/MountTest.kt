package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import net.minecraft.network.chat.Component

@SkidTest
object MountTest: TestUnit() {
    override val id: String = "mount-test"

    override val description: String = """
        이 유닛에서는 SkidMC의 KartMountEvents가 잘 작동하는지를 테스트합니다.
        다음의 절차에 따라 테스트를 진행하시면 됩니다.
        1. 아무 카트나 탑승하고, 카트 탑승 확인 메시지가 정상적으로 출력되는지 확인한다.
        2. 해당 카트에서 내리고, 카트 하차 확인 메시지가 정상적으로 출력되는지 확인한다.
        3. 카트에서 타고 내릴 때 출력되는 플레이어/카트의 엔티티 ID가 같은지를 확인한다.
        
        가능하다면 다음의 추가 테스트를 하는 것이 좋습니다.
        - 테스트를 반복하면서 카트 엔티티의 ID가 바뀌는지 확인한다 (어느 결과가 나와도 테스트는 통과하나, 이전과 차이가 발생할 경우 해당 사항을 기록)
        - 다른 플레이어가 주변에서 카트에 타고 내릴 때 메시지를 확인하고, 플레이어의 엔티티 ID 및 이름이 달라지는지 확인한다.
        
        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    init { register() }

    override fun test(): TestResult {
        KartMountEvents.MOUNT.register { kart, rider ->
            if (!status.testing) return@register

            val msg = Component.literal("now ${rider.name.string} (player#${rider.id}) is riding a kart#${kart.id}!")
            client.player?.displayClientMessage(msg, false)
        }

        KartMountEvents.DISMOUNT.register { kart, rider ->
            if (!status.testing) return@register

            val msg = Component.literal("now ${rider.name.string} (player#${rider.id}) is not riding a kart#${kart.id}!)")
            client.player?.displayClientMessage(msg, false)
        }
        return TestResult.TESTING
    }
}
