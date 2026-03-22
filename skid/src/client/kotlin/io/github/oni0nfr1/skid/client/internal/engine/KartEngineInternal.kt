package io.github.oni0nfr1.skid.client.internal.engine

import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import net.minecraft.network.chat.Component

interface KartEngineInternal {
    fun dispatchTachometerEvents(actionBar: Component): KartTachometerEvents.Result
}