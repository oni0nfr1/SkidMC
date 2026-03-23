package io.github.oni0nfr1.skidTest.client.units

import io.github.oni0nfr1.skid.client.api.attr.AttrModifierSnapshot
import io.github.oni0nfr1.skid.client.api.events.RiderAttrEvents
import io.github.oni0nfr1.skidTest.annotations.SkidTest
import io.github.oni0nfr1.skidTest.client.TestUnit
import io.github.oni0nfr1.skidTest.client.utils.renderDebugPanel
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

@SkidTest
object AttrPacketTest: TestUnit() {
    override val id = "attr-packet-test"
    override val description = """
        이 유닛에서는 마크라이더 S2C 어트리뷰트의 내용을 직접적으로 확인합니다.
        추가 또는 삭제된 어트리뷰트를 확인하여 API 업데이트가 필요한지를 점검합니다.
    """.trimIndent()

    init { register() }

    private var modifiers: AttrModifierSnapshot? = null

    override fun drawHud(guiGraphics: GuiGraphics, tickDelta: DeltaTracker) {
        if (!status.testing) return
        val modifiers = this.modifiers

        var info = "[SKIDMC DEBUG PANEL]\n"
        modifiers?.forEach { (id, value) ->
            info += "${id.path}: $value\n"
        }
        if (modifiers == null) info += "(EMPTY)\n"

        guiGraphics.renderDebugPanel(info, 10, 10, shadow = false)
    }

    override fun test(): TestResult {
        RiderAttrEvents.RIDER_META_ATTR.register { player, _, modifiers ->
            if (player == client.player) this.modifiers = modifiers
        }

        return TestResult.TESTING
    }
}