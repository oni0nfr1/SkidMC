package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.NitroEngine
import io.github.oni0nfr1.skid.client.api.engine.XEngine
import io.github.oni0nfr1.skid.client.api.tachometer.NitroTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.XTachometer
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.api.utils.Ref
import io.github.oni0nfr1.skid.client.api.utils.access

@Suppress("unused", "UNUSED_VARIABLE")
private fun specifiedKartTypeIsInferred(ref: KartRef) {
    val specified: Ref<XEngineKart> = ref.specify(KartType.X)

    specified.access {
        val typedEngine: XEngine = engine
        val typedTachometer: XTachometer? = tachometer
    }
}

@Suppress("unused", "UNUSED_VARIABLE")
private fun kartTypeParametersAreCovariant(kart: Kart<XEngine, XTachometer>) {
    val nitroKart: Kart<NitroEngine, NitroTachometer> = kart
}

@Suppress("unused", "UNUSED_VARIABLE")
private fun engineRetainsItsConcreteKartType(engine: XEngine) {
    val kart: XEngineKart = engine.kart
}
