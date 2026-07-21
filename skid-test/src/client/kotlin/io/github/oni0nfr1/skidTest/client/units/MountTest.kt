package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat
import net.minecraft.world.entity.player.Player

@SkidTest
object MountTest : TestUnit() {
    override val id: String = "mount-test"

    private data class MountRelationKey(
        val saddleId: Int,
        val riderId: Int,
    )

    private data class SpectateRelationKey(
        val saddleId: Int,
        val riderId: Int,
        val targetId: Int,
    )

    private val earlyMountRelations = mutableSetOf<MountRelationKey>()
    private val readyMountRelations = mutableSetOf<MountRelationKey>()
    private var earlySpectateRelation: SpectateRelationKey? = null
    private var readySpectateRelation: SpectateRelationKey? = null

    override val description: String = """
        이 유닛에서는 SkidMC의 KartMountEvents가 잘 작동하는지를 테스트합니다.
        다음의 절차에 따라 테스트를 진행하시면 됩니다.
        1. 아무 카트나 탑승하고, MOUNT_EARLY와 MOUNT 메시지가 순서대로 출력되는지 확인한다.
        2. MOUNT에서 Kart.rider, Player.ridingKart, KartEngine.kart가 같은 수명 주기를 가리키는지 확인한다.
        3. 해당 카트에서 내리고, 같은 관계의 DISMOUNT가 한 번 출력되는지 확인한다.
        4. 준비 전 관계와 준비 완료 관계가 각각 EARLY 또는 READY 단계로 정리되는지 확인한다.
        5. 멀티방에서 다른 플레이어를 관전하고, SPECTATE_EARLY와 SPECTATE 메시지가 순서대로 출력되는지 확인한다.
        6. SPECTATE에서 대상의 ridingKart와 준비된 Kart/Engine의 소유 관계가 일치하는지 확인한다.
        7. 대상 카트가 준비되기 전후에 관전 대상을 변경하거나 대상이 하차하게 하고, 두 경우 모두 SPECTATE_END 메시지가 출력되는지 확인한다.
        
        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    init { register() }

    private fun verifyKartReady(event: String, ref: KartRef, rider: Player): Kart<*>? {
        val kart = ref.get().orElse(null) ?: run {
            client.sendChat("[$event] kart object or engine is missing for kart#${ref.saddleId}!")
            autoFail()
            return null
        }
        if (!kart.alive) {
            client.sendChat("[$event] kart#${ref.saddleId} is not alive!")
            autoFail()
            return null
        }
        if (kart.rider !== rider) {
            client.sendChat("[$event] kart#${ref.saddleId} has a different rider!")
            autoFail()
            return null
        }
        if (kart.engine.kart !== kart) {
            client.sendChat("[$event] kart#${ref.saddleId} has a mismatched engine owner!")
            autoFail()
            return null
        }
        val riderKart = rider.ridingKart?.get()?.orElse(null)
        if (riderKart !== kart) {
            client.sendChat("[$event] ${rider.name.string} does not resolve the same riding kart!")
            autoFail()
            return null
        }

        return kart
    }

    override fun test(): TestResult {
        earlyMountRelations.clear()
        readyMountRelations.clear()
        earlySpectateRelation = null
        readySpectateRelation = null

        KartMountEvents.MOUNT_EARLY.register { kartEntity, rider ->
            if (!status.testing) return@register
            val relation = MountRelationKey(kartEntity.id, rider.id)
            if (!earlyMountRelations.add(relation)) {
                client.sendChat("[MOUNT_EARLY] duplicate relation for kart#${kartEntity.id} and player#${rider.id}!")
                autoFail()
                return@register
            }
            client.sendChat("[MOUNT_EARLY] ${rider.name.string} (player#${rider.id}) is riding kart#${kartEntity.id}.")
        }

        KartMountEvents.MOUNT.register { ref, rider ->
            if (!status.testing) return@register
            val relation = MountRelationKey(ref.saddleId, rider.id)
            if (relation !in earlyMountRelations) {
                client.sendChat("[MOUNT] matching MOUNT_EARLY relation is missing!")
                autoFail()
                return@register
            }
            if (!readyMountRelations.add(relation)) {
                client.sendChat("[MOUNT] duplicate relation for kart#${ref.saddleId} and player#${rider.id}!")
                autoFail()
                return@register
            }
            val kart = verifyKartReady("MOUNT", ref, rider) ?: return@register
            client.sendChat("[MOUNT] ${rider.name.string} (player#${rider.id}) is riding kart#${ref.saddleId} (kart=present, engine=${kart.type.engineName}).")
        }

        KartMountEvents.DISMOUNT.register { kartEntity, rider ->
            if (!status.testing) return@register
            val relation = MountRelationKey(kartEntity.id, rider.id)
            if (!earlyMountRelations.remove(relation)) {
                client.sendChat("[DISMOUNT] matching MOUNT_EARLY relation is missing!")
                autoFail()
                return@register
            }
            val wasReady = readyMountRelations.remove(relation)
            if (wasReady) {
                val ref = kartEntity.kart ?: run {
                    client.sendChat("[DISMOUNT] ready kart#${kartEntity.id} does not resolve during callback!")
                    autoFail()
                    return@register
                }
                if (verifyKartReady("DISMOUNT", ref, rider) == null) return@register
            }
            val stage = if (wasReady) "READY" else "EARLY"
            client.sendChat("[DISMOUNT:$stage] ${rider.name.string} (player#${rider.id}) is not riding kart#${kartEntity.id}.")
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

        KartMountEvents.SPECTATE.register { ref, rider, target ->
            if (!status.testing) return@register
            val relation = SpectateRelationKey(ref.saddleId, rider.id, target.id)
            if (earlySpectateRelation != relation) {
                client.sendChat("[SPECTATE] matching SPECTATE_EARLY relation is missing!")
                autoFail()
                return@register
            }
            if (readySpectateRelation != null) {
                client.sendChat("[SPECTATE] duplicate ready relation!")
                autoFail()
                return@register
            }
            readySpectateRelation = relation
            val kart = verifyKartReady("SPECTATE", ref, target) ?: return@register
            client.sendChat("[SPECTATE] ${rider.name.string} (player#${rider.id}) is spectating ${target.name.string} (player#${target.id}) riding kart#${ref.saddleId} (kart=present, engine=${kart.type.engineName}).")
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
