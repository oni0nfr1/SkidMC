package io.github.oni0nfr1.skid.client.api.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class KartTypeTest {

    @Test
    fun `all kart types have unique codes`() {
        assertEquals(21, KartType.entries.size)
        assertEquals(KartType.entries.size, KartType.entries.map { it.engineCode }.toSet().size)
        assertEquals(KartType.entries.size, KartType.entries.map { it.attrEngineCode }.toSet().size)
    }

    @Test
    fun `kart types can be found by both codes`() {
        for (type in KartType.entries) {
            assertSame(type, KartType.fromEngineCode(type.engineCode))
            assertSame(type, KartType.fromAttrEngineCode(type.attrEngineCode))
        }
    }

    @Test
    fun `DS uses the next dummy engine code`() {
        assertEquals(1008, KartType.DS.engineCode)
        assertEquals(1008, KartType.DS.attrEngineCode)
    }

    @Test
    fun `unknown codes return null`() {
        assertNull(KartType.fromEngineCode(Int.MIN_VALUE))
        assertNull(KartType.fromAttrEngineCode(Int.MIN_VALUE))
    }
}
