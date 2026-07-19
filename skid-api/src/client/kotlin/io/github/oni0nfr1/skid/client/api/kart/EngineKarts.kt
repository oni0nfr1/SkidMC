package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.A2Engine
import io.github.oni0nfr1.skid.client.api.engine.BoatEngine
import io.github.oni0nfr1.skid.client.api.engine.ChargeEngine
import io.github.oni0nfr1.skid.client.api.engine.EXEngine
import io.github.oni0nfr1.skid.client.api.engine.F1Engine
import io.github.oni0nfr1.skid.client.api.engine.GearEngine
import io.github.oni0nfr1.skid.client.api.engine.JiuEngine
import io.github.oni0nfr1.skid.client.api.engine.KeyEngine
import io.github.oni0nfr1.skid.client.api.engine.LegacyEngine
import io.github.oni0nfr1.skid.client.api.engine.MKEngine
import io.github.oni0nfr1.skid.client.api.engine.N1Engine
import io.github.oni0nfr1.skid.client.api.engine.NewEngine
import io.github.oni0nfr1.skid.client.api.engine.ProEngine
import io.github.oni0nfr1.skid.client.api.engine.RallyEngine
import io.github.oni0nfr1.skid.client.api.engine.RushPlusEngine
import io.github.oni0nfr1.skid.client.api.engine.RXEngine
import io.github.oni0nfr1.skid.client.api.engine.SREngine
import io.github.oni0nfr1.skid.client.api.engine.V1Engine
import io.github.oni0nfr1.skid.client.api.engine.XEngine
import io.github.oni0nfr1.skid.client.api.engine.Z7Engine
import io.github.oni0nfr1.skid.client.api.tachometer.A2Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.BoatTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.ChargeTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.EXTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.F1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.JiuTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.KeyTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.LegacyTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.MKTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.N1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.NewTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.ProTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RallyTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RushPlusTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RXTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.SRTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.V1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.XTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.Z7Tachometer

/** X 엔진 카트의 구체 타입입니다. */
typealias XEngineKart = Kart<XEngine, XTachometer>

/** EX 엔진 카트의 구체 타입입니다. */
typealias EXEngineKart = Kart<EXEngine, EXTachometer>

/** JIU 엔진 카트의 구체 타입입니다. */
typealias JiuEngineKart = Kart<JiuEngine, JiuTachometer>

/** NEW 엔진 카트의 구체 타입입니다. */
typealias NewEngineKart = Kart<NewEngine, NewTachometer>

/** Z7 엔진 카트의 구체 타입입니다. */
typealias Z7EngineKart = Kart<Z7Engine, Z7Tachometer>

/** V1 엔진 카트의 구체 타입입니다. */
typealias V1EngineKart = Kart<V1Engine, V1Tachometer>

/** A2 엔진 카트의 구체 타입입니다. */
typealias A2EngineKart = Kart<A2Engine, A2Tachometer>

/** 1.0 엔진 카트의 구체 타입입니다. */
typealias LegacyEngineKart = Kart<LegacyEngine, LegacyTachometer>

/** PRO 엔진 카트의 구체 타입입니다. */
typealias ProEngineKart = Kart<ProEngine, ProTachometer>

/** RUSH+ 엔진 카트의 구체 타입입니다. */
typealias RushPlusEngineKart = Kart<RushPlusEngine, RushPlusTachometer>

/** CHARGE 엔진 카트의 구체 타입입니다. */
typealias ChargeEngineKart = Kart<ChargeEngine, ChargeTachometer>

/** SR 엔진 카트의 구체 타입입니다. */
typealias SREngineKart = Kart<SREngine, SRTachometer>

/** N1 엔진 카트의 구체 타입입니다. */
typealias N1EngineKart = Kart<N1Engine, N1Tachometer>

/** RX 엔진 카트의 구체 타입입니다. */
typealias RXEngineKart = Kart<RXEngine, RXTachometer>

/** KEY 엔진 카트의 구체 타입입니다. */
typealias KeyEngineKart = Kart<KeyEngine, KeyTachometer>

/** MK 엔진 카트의 구체 타입입니다. */
typealias MKEngineKart = Kart<MKEngine, MKTachometer>

/** BOAT 엔진 카트의 구체 타입입니다. */
typealias BoatEngineKart = Kart<BoatEngine, BoatTachometer>

/** GEAR 엔진 카트의 구체 타입입니다. */
typealias GearEngineKart = Kart<GearEngine, GearTachometer>

/** F1 엔진 카트의 구체 타입입니다. */
typealias F1EngineKart = Kart<F1Engine, F1Tachometer>

/** RALLY 엔진 카트의 구체 타입입니다. */
typealias RallyEngineKart = Kart<RallyEngine, RallyTachometer>
