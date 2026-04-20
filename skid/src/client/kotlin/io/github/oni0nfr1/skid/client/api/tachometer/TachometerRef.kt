package io.github.oni0nfr1.skid.client.api.tachometer

import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerInternal
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.minecraft.client.Minecraft

class TachometerRef<T: KartTachometer>(tachometer: T) {

    private val revision = (tachometer as TachometerInternal).revision

    inline fun <R> access(block: T.() -> R): R? {
        if (Minecraft.getInstance().isSameThread) {
            return handle?.block()
        } else {
            error("Tachometer can only be accessed in Render Thread")
        }
    }

    val handle: T?
        get() {
            val current = TachometerManager.currentTachometerOrNull ?: return null
            if ((current as TachometerInternal).revision == revision) {
                @Suppress("UNCHECKED_CAST")
                return current as T
            }
            return null
        }
}
