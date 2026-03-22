package io.github.oni0nfr1.skid.client.internal.utils

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.util.Optional
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal inline fun <reified T> createEvent(noinline invokerFactory: (Array<T>) -> T): Event<T> {
    return EventFactory.createArrayBacked(T::class.java, invokerFactory)
}

internal inline fun <T> Component.visit(style: Style, crossinline action: (Style, String) -> Optional<T>): Optional<T> {
    return this.visit<T>({ style, text ->
        return@visit action(style, text)
    }, style)
}

internal object MCClient : ReadOnlyProperty<Any, Minecraft> {
    @Volatile
    private var client: Minecraft? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): Minecraft {
        if (client == null) {
            client = Minecraft.getInstance()
                ?: throw IllegalStateException("Minecraft not initialized. this property should not be read that early.")
            return client!!
        }
        return client!!
    }
}