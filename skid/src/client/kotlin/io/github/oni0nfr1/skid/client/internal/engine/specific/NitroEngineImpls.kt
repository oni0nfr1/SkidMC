package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.*
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.*
import io.github.oni0nfr1.skid.client.internal.engine.KartEngineImpl

internal class XEngineImpl(kart: Kart<XEngine>) :
    KartEngineImpl<XEngine, XTachometer>(kart), XEngine

internal class EXEngineImpl(kart: Kart<EXEngine>) :
    KartEngineImpl<EXEngine, EXTachometer>(kart), EXEngine

internal class JiuEngineImpl(kart: Kart<JiuEngine>) :
    KartEngineImpl<JiuEngine, JiuTachometer>(kart), JiuEngine

internal class NewEngineImpl(kart: Kart<NewEngine>) :
    KartEngineImpl<NewEngine, NewTachometer>(kart), NewEngine

internal class Z7EngineImpl(kart: Kart<Z7Engine>) :
    KartEngineImpl<Z7Engine, Z7Tachometer>(kart), Z7Engine

internal class V1EngineImpl(kart: Kart<V1Engine>) :
    KartEngineImpl<V1Engine, V1Tachometer>(kart), V1Engine

internal class A2EngineImpl(kart: Kart<A2Engine>) :
    KartEngineImpl<A2Engine, A2Tachometer>(kart), A2Engine

internal class LegacyEngineImpl(kart: Kart<LegacyEngine>) :
    KartEngineImpl<LegacyEngine, LegacyTachometer>(kart), LegacyEngine

internal class ProEngineImpl(kart: Kart<ProEngine>) :
    KartEngineImpl<ProEngine, ProTachometer>(kart), ProEngine

internal class RushPlusEngineImpl(kart: Kart<RushPlusEngine>) :
    KartEngineImpl<RushPlusEngine, RushPlusTachometer>(kart), RushPlusEngine

internal class ChargeEngineImpl(kart: Kart<ChargeEngine>) :
    KartEngineImpl<ChargeEngine, ChargeTachometer>(kart), ChargeEngine

internal class SREngineImpl(kart: Kart<SREngine>) :
    KartEngineImpl<SREngine, SRTachometer>(kart), SREngine

internal class N1EngineImpl(kart: Kart<N1Engine>) :
    KartEngineImpl<N1Engine, N1Tachometer>(kart), N1Engine

internal class RXEngineImpl(kart: Kart<RXEngine>) :
    KartEngineImpl<RXEngine, RXTachometer>(kart), RXEngine

internal class KeyEngineImpl(kart: Kart<KeyEngine>) :
    KartEngineImpl<KeyEngine, KeyTachometer>(kart), KeyEngine
