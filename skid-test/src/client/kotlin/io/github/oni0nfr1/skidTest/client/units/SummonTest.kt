package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartSummonEvents
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skid.client.api.utils.access
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

@SkidTest
object SummonTest : TestUnit() {
    override val id = "summon-test"
    override val description = """
        이 유닛에서는 KartSummonEvents의 수명 주기를 테스트합니다.
        1. 카트를 생성하고 SUMMON_EARLY 이후 SUMMON이 출력되는지 확인한다.
        2. SUMMON에서 카트 참조와 엔진 종류가 출력되는지 확인한다.
        3. 준비 전 또는 준비 후 카트를 제거하고 REMOVE가 한 번 출력되는지 확인한다.
        4. 준비 후 제거한 카트 참조가 REMOVE 콜백 이후 무효화되는지 확인한다.

        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    private val earlySaddleIds = mutableSetOf<Int>()
    private val readySaddleIds = mutableSetOf<Int>()
    private val removedRefsToCheck = mutableListOf<KartRef>()

    init { register() }

    override fun test(): TestResult {
        earlySaddleIds.clear()
        readySaddleIds.clear()
        removedRefsToCheck.clear()

        KartSummonEvents.SUMMON_EARLY.register { kartEntity ->
            if (!status.testing) return@register
            if (!earlySaddleIds.add(kartEntity.id)) {
                client.sendChat("[SUMMON_EARLY] duplicate event for kart#${kartEntity.id}!")
                autoFail()
                return@register
            }
            client.sendChat("[SUMMON_EARLY] kart#${kartEntity.id} was added.")
        }

        KartSummonEvents.SUMMON.register { kart ->
            if (!status.testing) return@register
            if (kart.saddleId !in earlySaddleIds) {
                client.sendChat("[SUMMON] matching SUMMON_EARLY is missing for kart#${kart.saddleId}!")
                autoFail()
                return@register
            }
            if (!readySaddleIds.add(kart.saddleId)) {
                client.sendChat("[SUMMON] duplicate event for kart#${kart.saddleId}!")
                autoFail()
                return@register
            }

            val engineName = kart.access { type.engineName } ?: run {
                client.sendChat("[SUMMON] kart#${kart.saddleId} does not resolve!")
                autoFail()
                return@register
            }
            client.sendChat("[SUMMON] kart#${kart.saddleId} is ready (engine=$engineName).")
        }

        KartSummonEvents.REMOVE.register { kartEntity ->
            if (!status.testing) return@register
            if (!earlySaddleIds.remove(kartEntity.id)) {
                client.sendChat("[REMOVE] matching SUMMON_EARLY is missing for kart#${kartEntity.id}!")
                autoFail()
                return@register
            }

            val wasReady = readySaddleIds.remove(kartEntity.id)
            if (wasReady) {
                val kart = kartEntity.kart ?: run {
                    client.sendChat("[REMOVE] ready kart#${kartEntity.id} does not resolve during callback!")
                    autoFail()
                    return@register
                }
                removedRefsToCheck += kart
            }
            val stage = if (wasReady) "READY" else "EARLY"
            client.sendChat("[REMOVE:$stage] kart#${kartEntity.id} was removed.")
        }

        return TestResult.TESTING
    }

    override fun drawHud(guiGraphics: GuiGraphics, tickDelta: DeltaTracker) {
        if (!status.testing || removedRefsToCheck.isEmpty()) return

        val iterator = removedRefsToCheck.iterator()
        while (iterator.hasNext()) {
            val kart = iterator.next()
            if (kart.get().isPresent) {
                client.sendChat("[REMOVE] kart#${kart.saddleId} still resolves after callback!")
                autoFail()
            } else {
                client.sendChat("[REMOVE] kart#${kart.saddleId} was invalidated after callback.")
            }
            iterator.remove()
        }
    }
}
