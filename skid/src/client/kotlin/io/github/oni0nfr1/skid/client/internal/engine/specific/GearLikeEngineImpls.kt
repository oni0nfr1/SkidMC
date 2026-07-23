package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.F1Engine
import io.github.oni0nfr1.skid.client.api.engine.GearEngine
import io.github.oni0nfr1.skid.client.api.engine.RallyEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.F1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RallyTachometer
import io.github.oni0nfr1.skid.client.internal.engine.KartEngineImpl

internal class GearEngineImpl(kart: Kart<GearEngine>) :
    KartEngineImpl<GearEngine, GearTachometer>(kart), GearEngine

internal class F1EngineImpl(kart: Kart<F1Engine>) :
    KartEngineImpl<F1Engine, F1Tachometer>(kart), F1Engine

internal class RallyEngineImpl(kart: Kart<RallyEngine>) :
    KartEngineImpl<RallyEngine, RallyTachometer>(kart), RallyEngine
