package io.github.oni0nfr1.skid.client.api.tachometer

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import net.minecraft.network.chat.Component

sealed interface KartTachometer {
    val type: KartEngine.Type
    val text: Component
    val rawString: String
        get() = text.string
}
