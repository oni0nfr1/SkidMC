package io.github.oni0nfr1.skid.client.api.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

@JvmSynthetic
internal inline fun <reified T> createEvent(
    noinline invokerFactory: (Array<T>) -> T,
): Event<T> = EventFactory.createArrayBacked(T::class.java, invokerFactory)
