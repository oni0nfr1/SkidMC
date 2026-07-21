package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartSummonEvents
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.sendChat
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import java.util.UUID

@SkidTest
object SummonTest : TestUnit() {
    override val id = "summon-test"
    override val description = """
        이 유닛에서는 KartSummonEvents의 수명 주기를 테스트합니다.
        1. 카트를 생성하고 SUMMON_EARLY 이후 SUMMON이 출력되는지 확인한다.
        2. SUMMON에서 카트 참조와 엔진 종류가 출력되는지 확인한다.
        3. 준비 전 또는 준비 후 카트를 제거하고 REMOVE가 한 번 출력되는지 확인한다.
        4. REMOVE 콜백 이후 이전 Kart와 엔진의 수명 주기가 종료되는지 확인한다.
        5. 같은 saddle이 재동기화되면 KartRef만 새 Kart를 resolve하고 이전 객체들은 재사용되지 않는지 확인한다.

        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    private val earlySaddleIds = mutableSetOf<Int>()
    private data class ReadyKart(
        val ref: KartRef,
        val kart: Kart<*>,
        val engine: KartEngine,
        var tachometer: KartTachometer?,
    )

    private val readyKartsBySaddleId = mutableMapOf<Int, ReadyKart>()

    private data class RemovedKart(
        val ref: KartRef,
        val kart: Kart<*>,
        val engine: KartEngine,
        val tachometer: KartTachometer?,
        val saddleUuid: UUID,
        var invalidationChecked: Boolean = false,
    )

    private val removedKartsToCheck = mutableListOf<RemovedKart>()

    init { register() }

    override fun test(): TestResult {
        earlySaddleIds.clear()
        readyKartsBySaddleId.clear()
        removedKartsToCheck.clear()

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
            if (kart.saddleId in readyKartsBySaddleId) {
                client.sendChat("[SUMMON] duplicate event for kart#${kart.saddleId}!")
                autoFail()
                return@register
            }

            val resolved = kart.get().orElse(null) ?: run {
                client.sendChat("[SUMMON] kart#${kart.saddleId} does not resolve!")
                autoFail()
                return@register
            }
            val engine = resolved.engine
            readyKartsBySaddleId[kart.saddleId] = ReadyKart(
                ref = kart,
                kart = resolved,
                engine = engine,
                tachometer = engine.tachometer,
            )
            client.sendChat("[SUMMON] kart#${kart.saddleId} is ready (engine=${resolved.type.engineName}).")
        }

        KartSummonEvents.REMOVE.register { kartEntity ->
            if (!status.testing) return@register
            if (!earlySaddleIds.remove(kartEntity.id)) {
                client.sendChat("[REMOVE] matching SUMMON_EARLY is missing for kart#${kartEntity.id}!")
                autoFail()
                return@register
            }

            val ready = readyKartsBySaddleId.remove(kartEntity.id)
            if (ready != null) {
                val callbackRef = kartEntity.kart ?: run {
                    client.sendChat("[REMOVE] ready kart#${kartEntity.id} does not resolve during callback!")
                    autoFail()
                    return@register
                }
                val callbackKart = callbackRef.get().orElse(null) ?: run {
                    client.sendChat("[REMOVE] ready kart#${kartEntity.id} is missing during callback!")
                    autoFail()
                    return@register
                }
                if (callbackKart !== ready.kart || callbackKart.engine !== ready.engine) {
                    client.sendChat("[REMOVE] kart#${kartEntity.id} changed objects before callback!")
                    autoFail()
                    return@register
                }
                removedKartsToCheck += RemovedKart(
                    ref = ready.ref,
                    kart = ready.kart,
                    engine = ready.engine,
                    tachometer = ready.tachometer,
                    saddleUuid = kartEntity.uuid,
                )
            }
            val stage = if (ready != null) "READY" else "EARLY"
            client.sendChat("[REMOVE:$stage] kart#${kartEntity.id} was removed.")
        }

        return TestResult.TESTING
    }

    override fun drawHud(guiGraphics: GuiGraphics, tickDelta: DeltaTracker) {
        if (!status.testing) return

        readyKartsBySaddleId.values.forEach { ready ->
            if (ready.tachometer == null) ready.tachometer = ready.engine.tachometer
        }
        if (removedKartsToCheck.isEmpty()) return

        val iterator = removedKartsToCheck.iterator()
        while (iterator.hasNext()) {
            val removed = iterator.next()
            if (!removed.invalidationChecked) {
                if (removed.kart.alive) {
                    client.sendChat("[REMOVE] previous kart#${removed.ref.saddleId} is still alive!")
                    autoFail()
                    iterator.remove()
                    continue
                }
                if (removed.engine.kart !== removed.kart) {
                    client.sendChat("[REMOVE] previous engine changed its owning kart!")
                    autoFail()
                    iterator.remove()
                    continue
                }
                if (removed.engine.tachometer != null) {
                    client.sendChat("[REMOVE] previous engine still exposes a current tachometer!")
                    autoFail()
                    iterator.remove()
                    continue
                }
                val staleEngineRejected = try {
                    removed.kart.engine
                    false
                } catch (_: IllegalStateException) {
                    true
                }
                if (!staleEngineRejected) {
                    client.sendChat("[REMOVE] previous kart still exposes its engine!")
                    autoFail()
                    iterator.remove()
                    continue
                }
                removed.invalidationChecked = true
                client.sendChat("[REMOVE] previous kart#${removed.ref.saddleId} was invalidated.")
            }

            val resolved = removed.ref.get().orElse(null) ?: continue
            if (resolved === removed.kart) {
                client.sendChat("[REMOVE] kart#${removed.ref.saddleId} reused the previous Kart object!")
                autoFail()
            } else if (resolved.engine === removed.engine) {
                client.sendChat("[REMOVE] kart#${removed.ref.saddleId} reused the previous engine!")
                autoFail()
            } else if (resolved.saddle.uuid != removed.saddleUuid) {
                client.sendChat("[REMOVE] kart#${removed.ref.saddleId} resolved to a different entity!")
                autoFail()
            } else {
                val newTachometer = resolved.engine.tachometer
                if (removed.tachometer != null) {
                    if (newTachometer == null) continue
                    if (newTachometer === removed.tachometer) {
                        client.sendChat("[REMOVE] kart#${removed.ref.saddleId} reused the previous tachometer!")
                        autoFail()
                    } else {
                        client.sendChat("[REMOVE] kart#${removed.ref.saddleId} was resynchronized with new objects.")
                    }
                } else {
                    client.sendChat("[REMOVE] kart#${removed.ref.saddleId} was resynchronized with new Kart and engine objects.")
                }
            }
            iterator.remove()
        }
    }
}
