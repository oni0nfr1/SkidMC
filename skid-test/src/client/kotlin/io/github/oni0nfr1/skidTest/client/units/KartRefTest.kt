package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.engine.XEngine
import io.github.oni0nfr1.skid.client.api.events.KartMountEvents
import io.github.oni0nfr1.skid.client.api.kart.KartEntity
import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.renderDebugPanel
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.player.Player

@SkidTest
object KartRefTest : TestUnit() {
    override val id = "kart-ref-test"
    override val description: String = """
        이 유닛에서는 KartRef.Specific 타입의 사용법을 설명합니다.
        Skid 구현체의 동작을 테스트하기보다는 Ref 계열 객체가 어떻게 사용될 수 있는지를 소개하는 성격에 가까운 유닛입니다.
        
        이 테스트 유닛에서는 예시로서 카트의 엔진 타입에 따라 바뀌는 HUD 구현체 아키텍처를 제시합니다.
    """.trimIndent()

    init { register() }

    var hudRenderer: HudRenderer<*>? = null

    override fun test(): TestResult {
        KartMountEvents.MOUNT.register(this::onMount)
        KartMountEvents.DISMOUNT.register(this::onDismount)

        return TestResult.TESTING_AUTO
    }

    override fun drawHud(guiGraphics: GuiGraphics, tickDelta: DeltaTracker) {
        val hudRenderer = hudRenderer ?: return

        if (!hudRenderer.renderHud(guiGraphics)) {
            onDismount(null, client.player)
        }
    }

    fun onMount(kartEntity: KartEntity, player: Player) {
        if (!status.testing || hudRenderer != null) return
        if (player != client.player) return

        kartEntity.kart?.access {
            val engine = engine

            // 엔진의 실제 타입을 확인한 뒤, 그 타입이 반영된 KartRef.Specific<E>를 생성합니다.
            // 이후 HUD 렌더러는 공통 타입으로 보관되지만, 각 구현체 내부에서는 엔진 타입이 컴파일 타임에 결정됩니다.
            hudRenderer = when (engine) {
                is XEngine -> XEngineRenderer(KartRef.specify(engine))
                else -> null
            }
        }
    }

    fun onDismount(kartEntity: KartEntity?, player: Player?) {
        if (!status.testing || hudRenderer == null) return

        val clientPlayer = client.player ?: return
        if (player != clientPlayer) return

        autoSuccess()
        hudRenderer = null
    }

    /**
     * HUD 렌더러를 나타내는 추상 클래스입니다.
     * 여기서는 카트의 엔진 타입이 특정되지 않으나, 구현체에서 E를 명시하면 카트의 엔진 타입이 컴파일 타임에 결정됩니다.
     */
    abstract class HudRenderer<E : KartEngine>(val kart: KartRef.Specific<E>) {
        /**
         * HUD 렌더링에 성공했으면 true를 반환합니다.
         *
         * 카트가 더 이상 유효하지 않거나, 기대한 엔진 타입을 만족하지 못해 렌더링할 수 없으면 false를 반환합니다.
         */
        abstract fun renderHud(guiGraphics: GuiGraphics): Boolean
    }

    /**
     * HUD 렌더러의 구현체입니다.
     * 이 클래스의 구현에서는 카트의 엔진 타입이 컴파일 타임에 결정되어 있으므로, X엔진 고유의 특성들을 간단하게 읽을 수 있습니다.
     */
    class XEngineRenderer(kart: KartRef.Specific<XEngine>) : HudRenderer<XEngine>(kart) {
        override fun renderHud(guiGraphics: GuiGraphics): Boolean {
            val engineData = kart.accessEngine { engine ->
                buildString {
                    appendLine("[SKIDMC DEBUG PANEL]")
                    appendLine("is_boosting: ${engine.isBoosting}")
                    // 등등...
                }
            } ?: return false

            guiGraphics.renderDebugPanel(engineData, 10, 10, shadow = false)
            return true
        }
    }
}