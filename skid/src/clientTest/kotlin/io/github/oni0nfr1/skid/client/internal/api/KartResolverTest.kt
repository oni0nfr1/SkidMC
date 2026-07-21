package io.github.oni0nfr1.skid.client.internal.api

import io.github.oni0nfr1.skid.client.api.kart.Kart
import java.lang.reflect.Proxy
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class KartResolverTest {

    private val saddleId = 42
    private val saddleUuid = UUID.fromString("5edc69f1-3457-4b68-a528-e0f0fb57377a")
    private val otherSaddleUuid = UUID.fromString("d05ae0e5-3588-47cd-b170-8185363f693d")
    private val kart = fakeKart()

    @Test
    fun `resolves the tracked kart when both ID and UUID match`() {
        val resolver = resolverReturning(saddleId, saddleUuid)

        assertSame(kart, resolver.resolve(saddleId, saddleUuid).orElseThrow())
    }

    @Test
    fun `rejects a different UUID reusing the same entity ID`() {
        val resolver = resolverReturning(saddleId, otherSaddleUuid)

        assertFalse(resolver.resolve(saddleId, saddleUuid).isPresent)
    }

    @Test
    fun `rejects a lookup result with a different entity ID`() {
        val resolver = resolverReturning(saddleId + 1, saddleUuid)

        assertFalse(resolver.resolve(saddleId, saddleUuid).isPresent)
    }

    @Test
    fun `returns empty when the entity ID is not tracked`() {
        val resolver = KartResolver { null }

        assertFalse(resolver.resolve(saddleId, saddleUuid).isPresent)
    }

    @Test
    fun `provider delegates the complete saddle identity to the resolver`() {
        var lookedUpSaddleId: Int? = null
        val resolver = KartResolver { requestedSaddleId ->
            lookedUpSaddleId = requestedSaddleId
            TrackedKart(kart, saddleId, saddleUuid)
        }
        val provider = SkidApiProviderImpl(resolver)

        assertSame(kart, provider.getKart(saddleId, saddleUuid).orElseThrow())
        assertEquals(saddleId, lookedUpSaddleId)
        assertFalse(provider.getKart(saddleId, otherSaddleUuid).isPresent)
    }

    private fun resolverReturning(
        actualSaddleId: Int,
        actualSaddleUuid: UUID,
    ) = KartResolver {
        TrackedKart(kart, actualSaddleId, actualSaddleUuid)
    }

    @Suppress("UNCHECKED_CAST")
    private fun fakeKart(): Kart<*> = Proxy.newProxyInstance(
        Kart::class.java.classLoader,
        arrayOf(Kart::class.java),
    ) { proxy, method, arguments ->
        when (method.name) {
            "equals" -> proxy === arguments?.firstOrNull()
            "hashCode" -> System.identityHashCode(proxy)
            "toString" -> "FakeKart"
            else -> error("Unexpected Kart method call: ${method.name}")
        }
    } as Kart<*>
}
