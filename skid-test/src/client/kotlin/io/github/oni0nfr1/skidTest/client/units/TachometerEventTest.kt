package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.utils.access
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.renderDebugPanel
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

@SkidTest
object TachometerEventTest : TestUnit() {
    override val id = "tachometer-event-test"
    override val description = """
        이 유닛에서는 SkidMC의 KartTachometerEvents가 정상적으로 호출되는지를 테스트합니다.
        다음의 절차를 따라 테스트를 진행하시면 됩니다.
        1. 카트에 탑승하고 주행한다.
        2. 화면 좌상단 디버그 패널에서 RECEIVE와 해당 엔진이 제공하는 값 이벤트를 확인한다.
        3. 이벤트의 카트 ID와 엔진 이름이 현재 탑승한 카트와 일치하는지 확인한다.
        4. 모든 엔진에 대해 지원되는 이벤트와 값이 정상적으로 출력되는지 확인한다.

        이벤트에서 전달된 KartRef를 해석할 수 없으면 테스트는 자동으로 실패합니다.
        테스트 통과 시 통과 커맨드를 입력하면 테스트가 종료됩니다.
    """.trimIndent()

    private val snapshots = linkedMapOf<String, EventSnapshot>()
    private val currentSequence = mutableListOf<String>()
    private var latestSequence = ""

    init { register() }

    override fun test(): TestResult {
        snapshots.clear()
        currentSequence.clear()
        latestSequence = ""

        KartTachometerEvents.SPEED.register { kart, speed ->
            record("SPEED", kart, speed)
        }
        KartTachometerEvents.NITRO.register { kart, nitro ->
            record("NITRO", kart, nitro)
        }
        KartTachometerEvents.GAUGE.register { kart, gauge ->
            record("GAUGE", kart, gauge)
        }
        KartTachometerEvents.RPM.register { kart, rpm ->
            record("RPM", kart, rpm)
        }
        KartTachometerEvents.GEAR.register { kart, gear ->
            record("GEAR", kart, gear)
        }
        KartTachometerEvents.ERS.register { kart, ers ->
            record("ERS", kart, ers)
        }
        KartTachometerEvents.TURBO_GAUGE.register { kart, gauge ->
            record("TURBO_GAUGE", kart, gauge)
        }
        KartTachometerEvents.RECEIVE.register { kart, text ->
            record("RECEIVE", kart, text.string.replace('\n', ' '))
        }

        return TestResult.TESTING
    }

    override fun drawHud(guiGraphics: GuiGraphics, tickDelta: DeltaTracker) {
        if (!status.testing) return

        val eventData = buildString {
            appendLine("[TACHOMETER EVENT TEST]")
            appendLine("latest sequence: ${latestSequence.ifEmpty { "-" }}")
            if (snapshots.isEmpty()) {
                append("waiting for tachometer events...")
            } else {
                snapshots.forEach { (event, snapshot) ->
                    if (snapshot.value is String && snapshot.value.length > 80) {
                        appendLine(
                            "$event(${snapshot.count}): kart#${snapshot.kartId} " +
                                    "engine=${snapshot.engineName}\n    value=${snapshot.value}"
                        )
                    } else {
                        appendLine(
                            "$event(${snapshot.count}): kart#${snapshot.kartId} " +
                                "engine=${snapshot.engineName} value=${snapshot.value}"
                        )
                    }
                }
            }
        }

        guiGraphics.renderDebugPanel(eventData, 10, 10, shadow = false)
    }

    @Suppress("SameReturnValue")
    private fun record(
        event: String,
        kart: KartRef,
        value: Any,
    ): KartTachometerEvents.Result {
        if (!status.testing) return KartTachometerEvents.Result.SHOW

        val engineName = kart.access { type.engineName }
        if (engineName == null) {
            autoFail()
            return KartTachometerEvents.Result.SHOW
        }

        val previous = snapshots[event]
        snapshots[event] = EventSnapshot(
            count = (previous?.count ?: 0) + 1,
            kartId = kart.saddleId,
            engineName = engineName,
            value = value,
        )
        currentSequence += event

        if (event == "RECEIVE") {
            latestSequence = currentSequence.joinToString(" -> ")
            currentSequence.clear()
        }

        return KartTachometerEvents.Result.SHOW
    }

    private data class EventSnapshot(
        val count: Int,
        val kartId: Int,
        val engineName: String,
        val value: Any,
    )
}
