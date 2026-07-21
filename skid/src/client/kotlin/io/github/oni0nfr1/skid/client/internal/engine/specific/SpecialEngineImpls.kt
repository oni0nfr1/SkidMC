package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.BoatEngine
import io.github.oni0nfr1.skid.client.api.engine.MKEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.BoatTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.MKTachometer
import io.github.oni0nfr1.skid.client.internal.engine.KartEngineImpl

internal class MKEngineImpl(kart: Kart<MKEngine>) :
    KartEngineImpl<MKEngine, MKTachometer>(kart), MKEngine

internal class BoatEngineImpl(kart: Kart<BoatEngine>) :
    KartEngineImpl<BoatEngine, BoatTachometer>(kart), BoatEngine
