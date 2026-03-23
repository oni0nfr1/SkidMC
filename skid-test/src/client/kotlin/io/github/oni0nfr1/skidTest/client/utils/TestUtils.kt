package io.github.oni0nfr1.skidTest.client.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

fun Minecraft.sendChat(msg: String) {
    this.gui.chat.addMessage(Component.literal(msg))
    this.narrator.sayNow(msg)
}

fun Minecraft.sendChat(msg: Component) {
    this.gui.chat.addMessage(msg)
    this.narrator.sayNow(msg)
}

fun GuiGraphics.renderDebugPanel(
    text: String,
    startX: Int,
    startY: Int,
    colorArgb: Int = 0xFFFFFFFF.toInt(),
    shadow: Boolean = true,
    extraLineSpacing: Int = 0,
    padding: Int = 10
) {
    val lines: List<String> = text.split('\n')
    val font = Minecraft.getInstance().font

    val textWidth = lines.maxOf { font.width(it) }
    val textHeight = lines.size * font.lineHeight + (lines.size - 1) * extraLineSpacing

    this.fill(
        startX, startY,
        startX + textWidth + padding * 2,
        startY + textHeight + padding * 2,
        0x80000000.toInt()
    )

    lines.forEachIndexed { lineIndex, lineText ->
        val lineY: Int = startY + lineIndex * (font.lineHeight + extraLineSpacing) + padding
        this.drawString(font, lineText, startX + padding, lineY, colorArgb, shadow)
    }
}