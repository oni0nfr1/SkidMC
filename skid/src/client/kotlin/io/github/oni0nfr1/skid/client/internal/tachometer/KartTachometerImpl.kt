package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.api.utils.KartType
import net.minecraft.network.chat.Component

internal abstract class KartTachometerImpl(
    final override val revision: Long,
    final override val kartId: Int,
    final override val type: KartType<*>,
) : TachometerInternal {

    final override var text: Component = Component.empty()
        protected set

    protected fun commit(actionBar: Component) {
        text = actionBar
    }
}
