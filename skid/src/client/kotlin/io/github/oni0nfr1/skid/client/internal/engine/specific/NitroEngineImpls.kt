package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.*
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.*
import io.github.oni0nfr1.skid.client.internal.engine.KartEngineImpl

internal class XEngineImpl(kart: Kart<XEngine, XTachometer>) :
    KartEngineImpl<XEngine, XTachometer>(kart), XEngine

internal class EXEngineImpl(kart: Kart<EXEngine, EXTachometer>) :
    KartEngineImpl<EXEngine, EXTachometer>(kart), EXEngine

internal class JiuEngineImpl(kart: Kart<JiuEngine, JiuTachometer>) :
    KartEngineImpl<JiuEngine, JiuTachometer>(kart), JiuEngine

internal class NewEngineImpl(kart: Kart<NewEngine, NewTachometer>) :
    KartEngineImpl<NewEngine, NewTachometer>(kart), NewEngine

internal class Z7EngineImpl(kart: Kart<Z7Engine, Z7Tachometer>) :
    KartEngineImpl<Z7Engine, Z7Tachometer>(kart), Z7Engine

internal class V1EngineImpl(kart: Kart<V1Engine, V1Tachometer>) :
    KartEngineImpl<V1Engine, V1Tachometer>(kart), V1Engine

internal class A2EngineImpl(kart: Kart<A2Engine, A2Tachometer>) :
    KartEngineImpl<A2Engine, A2Tachometer>(kart), A2Engine

internal class LegacyEngineImpl(kart: Kart<LegacyEngine, LegacyTachometer>) :
    KartEngineImpl<LegacyEngine, LegacyTachometer>(kart), LegacyEngine

internal class ProEngineImpl(kart: Kart<ProEngine, ProTachometer>) :
    KartEngineImpl<ProEngine, ProTachometer>(kart), ProEngine

internal class RushPlusEngineImpl(kart: Kart<RushPlusEngine, RushPlusTachometer>) :
    KartEngineImpl<RushPlusEngine, RushPlusTachometer>(kart), RushPlusEngine

internal class ChargeEngineImpl(kart: Kart<ChargeEngine, ChargeTachometer>) :
    KartEngineImpl<ChargeEngine, ChargeTachometer>(kart), ChargeEngine

internal class SREngineImpl(kart: Kart<SREngine, SRTachometer>) :
    KartEngineImpl<SREngine, SRTachometer>(kart), SREngine

internal class N1EngineImpl(kart: Kart<N1Engine, N1Tachometer>) :
    KartEngineImpl<N1Engine, N1Tachometer>(kart), N1Engine

internal class RXEngineImpl(kart: Kart<RXEngine, RXTachometer>) :
    KartEngineImpl<RXEngine, RXTachometer>(kart), RXEngine

internal class KeyEngineImpl(kart: Kart<KeyEngine, KeyTachometer>) :
    KartEngineImpl<KeyEngine, KeyTachometer>(kart), KeyEngine
