package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skid.client.api.utils.access
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat

@SkidTest
object MountTest: TestUnit() {
    override val id: String = "mount-test"

    private data class SpectateRelationKey(
        val saddleId: Int,
        val riderId: Int,
        val targetId: Int,
    )

    private var earlySpectateRelation: SpectateRelationKey? = null
    private var readySpectateRelation: SpectateRelationKey? = null

    override val description: String = """
        이 유닛에서는 SkidMC의 KartMountEvents가 잘 작동하는지를 테스트합니다.
        다음의 절차에 따라 테스트를 진행하시면 됩니다.
        1. 아무 카트나 탑승하고, MOUNT_EARLY와 MOUNT 메시지가 순서대로 출력되는지 확인한다.
        2. MOUNT 메시지에 카트 객체와 올바른 엔진 종류가 출력되는지 확인한다.
        3. 해당 카트에서 내리고, DISMOUNT 메시지가 정상적으로 출력되는지 확인한다.
        4. 카트에서 타고 내릴 때 출력되는 플레이어/카트의 엔티티 ID가 같은지를 확인한다.
        5. 멀티방에서 다른 플레이어를 관전하고, SPECTATE_EARLY와 SPECTATE 메시지가 순서대로 출력되는지 확인한다.
        6. SPECTATE 메시지에 카트 객체와 올바른 엔진 종류가 출력되는지 확인한다.
        7. 대상 카트가 준비되기 전후에 관전 대상을 변경하거나 대상이 하차하게 하고, 두 경우 모두 SPECTATE_END 메시지가 출력되는지 확인한다.
        
        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    init { register() }

    private fun verifyKartReady(event: String, kartEntity: KartSaddle): String? {
        val kart = kartEntity.kart ?: run {
            client.sendChat("[$event] kart object is missing for kart#${kartEntity.id}!")
            autoFail()
            return null
        }
        val engineType = kart.access { type } ?: run {
            client.sendChat("[$event] engine is missing for kart#${kartEntity.id}!")
            autoFail()
            return null
        }

        return engineType.engineName
    }

    override fun test(): TestResult {
        earlySpectateRelation = null
        readySpectateRelation = null

        KartMountEvents.MOUNT_EARLY.register { kartEntity, rider ->
            if (!status.testing) return@register
            client.sendChat("[MOUNT_EARLY] ${rider.name.string} (player#${rider.id}) is riding kart#${kartEntity.id}.")
        }

        KartMountEvents.MOUNT.register { kartEntity, rider ->
            if (!status.testing) return@register
            val engineName = verifyKartReady("MOUNT", kartEntity) ?: return@register
            client.sendChat("[MOUNT] ${rider.name.string} (player#${rider.id}) is riding kart#${kartEntity.id} (kart=present, engine=$engineName).")
        }

        KartMountEvents.DISMOUNT.register { kartEntity, rider ->
            if (!status.testing) return@register
            client.sendChat("[DISMOUNT] ${rider.name.string} (player#${rider.id}) is not riding kart#${kartEntity.id}.")
        }

        KartMountEvents.SPECTATE_EARLY.register { kartEntity, rider, target ->
            if (!status.testing) return@register
            val relation = SpectateRelationKey(kartEntity.id, rider.id, target.id)
            if (earlySpectateRelation != null) {
                client.sendChat("[SPECTATE_EARLY] previous relation was not ended!")
                autoFail()
            }
            earlySpectateRelation = relation
            client.sendChat("[SPECTATE_EARLY] ${rider.name.string} (player#${rider.id}) is spectating ${target.name.string} (player#${target.id}) riding kart#${kartEntity.id}.")
        }

        KartMountEvents.SPECTATE.register { kartEntity, rider, target ->
            if (!status.testing) return@register
            val relation = SpectateRelationKey(kartEntity.id, rider.id, target.id)
            if (earlySpectateRelation != relation) {
                client.sendChat("[SPECTATE] matching SPECTATE_EARLY relation is missing!")
                autoFail()
            }
            readySpectateRelation = relation
            val engineName = verifyKartReady("SPECTATE", kartEntity) ?: return@register
            client.sendChat("[SPECTATE] ${rider.name.string} (player#${rider.id}) is spectating ${target.name.string} (player#${target.id}) riding kart#${kartEntity.id} (kart=present, engine=$engineName).")
        }

        KartMountEvents.SPECTATE_END.register { kartEntity, rider, target ->
            if (!status.testing) return@register
            val relation = SpectateRelationKey(kartEntity.id, rider.id, target.id)
            if (earlySpectateRelation != relation) {
                client.sendChat("[SPECTATE_END] matching SPECTATE_EARLY relation is missing!")
                autoFail()
            }
            val stage = if (readySpectateRelation == relation) "READY" else "EARLY"
            earlySpectateRelation = null
            readySpectateRelation = null
            client.sendChat("[SPECTATE_END:$stage] ${rider.name.string} (player#${rider.id}) is not spectating ${target.name.string} (player#${target.id}) riding kart#${kartEntity.id}.")
        }
        return TestResult.TESTING
    }
}
