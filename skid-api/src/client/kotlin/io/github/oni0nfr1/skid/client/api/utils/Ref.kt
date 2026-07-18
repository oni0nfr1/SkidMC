package io.github.oni0nfr1.skid.client.api.utils

import java.util.Optional

interface Ref<T> {
    fun get(): Optional<T>
}

inline fun <T: Any, R> Ref<T>.access(
    block: T.() -> R
): R? {
    val value = get().orElse(null) ?: return null
    return value.block()
}