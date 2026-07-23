package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.A2Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.BoatTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.ChargeTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.DSTachometer
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
import io.github.oni0nfr1.skid.client.api.tachometer.RXTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RallyTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RushPlusTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.SRTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.V1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.XTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.Z7Tachometer

/** X 엔진의 기능과 타코미터 타입을 제공합니다. */
interface XEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine {
    override val kart: Kart<XEngine>
    override val tachometer: XTachometer?
}

/** EX 엔진의 기능과 타코미터 타입을 제공합니다. */
interface EXEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine {
    override val kart: Kart<EXEngine>
    override val tachometer: EXTachometer?
}

/** JIU 엔진의 기능과 타코미터 타입을 제공합니다. */
interface JiuEngine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<JiuEngine>
    override val tachometer: JiuTachometer?
}

/** NEW 엔진의 기능과 타코미터 타입을 제공합니다. */
interface NewEngine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<NewEngine>
    override val tachometer: NewTachometer?
}

/** Z7 엔진의 기능과 타코미터 타입을 제공합니다. */
interface Z7Engine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<Z7Engine>
    override val tachometer: Z7Tachometer?
}

/** V1 엔진의 기능과 타코미터 타입을 제공합니다. */
interface V1Engine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine, ExceedEngine {
    override val kart: Kart<V1Engine>
    override val tachometer: V1Tachometer?
}

/** A2 엔진의 기능과 타코미터 타입을 제공합니다. */
interface A2Engine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<A2Engine>
    override val tachometer: A2Tachometer?
}

/** 1.0 엔진의 기능과 타코미터 타입을 제공합니다. */
interface LegacyEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine {
    override val kart: Kart<LegacyEngine>
    override val tachometer: LegacyTachometer?
}

/** PRO 엔진의 기능과 타코미터 타입을 제공합니다. */
interface ProEngine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<ProEngine>
    override val tachometer: ProTachometer?
}

/** RUSH+ 엔진의 기능과 타코미터 타입을 제공합니다. */
interface RushPlusEngine : NitroEngine, InstantBoostEngine, DraftEngine, ExceedEngine {
    override val kart: Kart<RushPlusEngine>
    override val tachometer: RushPlusTachometer?
}

/** CHARGE 엔진의 기능과 타코미터 타입을 제공합니다. */
interface ChargeEngine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<ChargeEngine>
    override val tachometer: ChargeTachometer?
}

/** SR 엔진의 기능과 타코미터 타입을 제공합니다. */
interface SREngine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<SREngine>
    override val tachometer: SRTachometer?
}

/** N1 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface N1Engine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<N1Engine>
    override val tachometer: N1Tachometer?
}

/** RX 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface RXEngine : NitroEngine, InstantBoostEngine, DraftEngine {
    override val kart: Kart<RXEngine>
    override val tachometer: RXTachometer?
}

/** KEY 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface KeyEngine : NitroEngine {
    override val kart: Kart<KeyEngine>
    override val tachometer: KeyTachometer?
}

/** GEAR 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface GearEngine : GearLikeEngine, DraftEngine {
    override val kart: Kart<GearEngine>
    override val tachometer: GearTachometer?
}

/** RALLY 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface RallyEngine : GearLikeEngine, DraftEngine {
    override val kart: Kart<RallyEngine>
    override val tachometer: RallyTachometer?
}

/** F1 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface F1Engine : GearLikeEngine, DraftEngine {
    override val kart: Kart<F1Engine>
    override val tachometer: F1Tachometer?
}

/** MK 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface MKEngine : MKLikeEngine {
    override val kart: Kart<MKEngine>
    override val tachometer: MKTachometer?
}

/** DS 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface DSEngine : MKLikeEngine {
    override val kart: Kart<DSEngine>
    override val tachometer: DSTachometer?
}

/** BOAT 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface BoatEngine : KartEngine {
    override val kart: Kart<BoatEngine>
    override val tachometer: BoatTachometer?
}
