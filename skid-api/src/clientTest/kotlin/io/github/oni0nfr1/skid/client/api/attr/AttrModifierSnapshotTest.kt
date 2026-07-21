package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.resources.ResourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AttrModifierSnapshotTest {
    @Test
    fun `snapshot copies values and supports key lookup`() {
        val key = ResourceLocation.fromNamespaceAndPath("skid-test", "value")
        val source = linkedMapOf(key to 1.0)
        val snapshot = AttrModifierSnapshot(source)

        source[key] = 2.0

        assertEquals(1.0, snapshot[key])
        assertEquals(1.0, snapshot["skid-test", "value"])
        assertNull(snapshot["missing", "value"])
    }

    @Test
    fun `default namespace lookup uses minecraft`() {
        val key = ResourceLocation.withDefaultNamespace("value")
        val snapshot = AttrModifierSnapshot(mapOf(key to 3.0))

        assertEquals(3.0, snapshot["value"])
    }
}
