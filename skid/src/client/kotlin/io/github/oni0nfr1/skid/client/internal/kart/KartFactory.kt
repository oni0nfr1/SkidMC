package io.github.oni0nfr1.skid.client.internal.kart

import io.github.oni0nfr1.skid.client.api.engine.*
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.internal.engine.specific.*

internal object KartFactory {

    fun create(saddle: KartSaddle, type: KartType<*>): KartImpl<*> = when (type) {
        KartType.X -> create(saddle, KartType.X, ::XEngineImpl)
        KartType.EX -> create(saddle, KartType.EX, ::EXEngineImpl)
        KartType.JIU -> create(saddle, KartType.JIU, ::JiuEngineImpl)
        KartType.NEW -> create(saddle, KartType.NEW, ::NewEngineImpl)
        KartType.Z7 -> create(saddle, KartType.Z7, ::Z7EngineImpl)
        KartType.V1 -> create(saddle, KartType.V1, ::V1EngineImpl)
        KartType.A2 -> create(saddle, KartType.A2, ::A2EngineImpl)
        KartType.LEGACY -> create(saddle, KartType.LEGACY, ::LegacyEngineImpl)
        KartType.PRO -> create(saddle, KartType.PRO, ::ProEngineImpl)
        KartType.RUSHPLUS -> create(saddle, KartType.RUSHPLUS, ::RushPlusEngineImpl)
        KartType.CHARGE -> create(saddle, KartType.CHARGE, ::ChargeEngineImpl)
        KartType.SR -> create(saddle, KartType.SR, ::SREngineImpl)
        KartType.N1 -> create(saddle, KartType.N1, ::N1EngineImpl)
        KartType.RX -> create(saddle, KartType.RX, ::RXEngineImpl)
        KartType.KEY -> create(saddle, KartType.KEY, ::KeyEngineImpl)
        KartType.MK -> create(saddle, KartType.MK, ::MKEngineImpl)
        KartType.DS -> create(saddle, KartType.DS, ::DSEngineImpl)
        KartType.BOAT -> create(saddle, KartType.BOAT, ::BoatEngineImpl)
        KartType.GEAR -> create(saddle, KartType.GEAR, ::GearEngineImpl)
        KartType.F1 -> create(saddle, KartType.F1, ::F1EngineImpl)
        KartType.RALLY -> create(saddle, KartType.RALLY, ::RallyEngineImpl)
    }

    private fun <ENGINE : KartEngine> create(
        saddle: KartSaddle,
        type: KartType<ENGINE>,
        createEngine: (Kart<ENGINE>) -> ENGINE,
    ): KartImpl<ENGINE> {
        val kart = KartImpl(saddle, type)
        kart.initializeEngine(createEngine(kart))
        return kart
    }
}
