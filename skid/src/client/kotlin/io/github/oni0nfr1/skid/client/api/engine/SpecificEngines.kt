package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.tachometer.*

// 니트로 엔진
/** X 엔진의 기능과 타코미터 타입을 제공합니다. */
interface XEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine { override val tachometer: XTachometer? }
/** EX 엔진의 기능과 타코미터 타입을 제공합니다. */
interface EXEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine { override val tachometer: EXTachometer? }
/** JIU 엔진의 기능과 타코미터 타입을 제공합니다. */
interface JiuEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: JiuTachometer? }
/** NEW 엔진의 기능과 타코미터 타입을 제공합니다. */
interface NewEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: NewTachometer? }
/** Z7 엔진의 기능과 타코미터 타입을 제공합니다. */
interface Z7Engine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: Z7Tachometer? }
/** V1 엔진의 기능과 타코미터 타입을 제공합니다. */
interface V1Engine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine, ExceedEngine { override val tachometer: V1Tachometer? }
/** A2 엔진의 기능과 타코미터 타입을 제공합니다. */
interface A2Engine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: A2Tachometer? }
/** 1.0 엔진의 기능과 타코미터 타입을 제공합니다. */
interface LegacyEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine { override val tachometer: LegacyTachometer? }
/** PRO 엔진의 기능과 타코미터 타입을 제공합니다. */
interface ProEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: ProTachometer? }
/** RUSH+ 엔진의 기능과 타코미터 타입을 제공합니다. */
interface RushPlusEngine : NitroEngine, InstantBoostEngine, DraftEngine, ExceedEngine { override val tachometer: RushPlusTachometer? }
/** CHARGE 엔진의 기능과 타코미터 타입을 제공합니다. */
interface ChargeEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: ChargeTachometer? }
/** SR 엔진의 기능과 타코미터 타입을 제공합니다. */
interface SREngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: SRTachometer? }
// 니트로 엔진 (더미)
/** N1 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface N1Engine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: N1Tachometer? }
/** RX 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface RXEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: RXTachometer? }
/** KEY 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface KeyEngine : NitroEngine { override val tachometer: KeyTachometer? }

// 기어류 엔진 (더미)
/** GEAR 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface GearEngine : GearlikeEngine, DraftEngine { override val tachometer: GearTachometer? }
/** RALLY 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface RallyEngine : GearlikeEngine, DraftEngine { override val tachometer: RallyTachometer? }
/** F1 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface F1Engine : GearlikeEngine, DraftEngine { override val tachometer: F1Tachometer? }

// 기타 엔진 (더미)
/** MK 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface MKEngine : DriftEngine, DraftEngine { override val tachometer: MKTachometer? }
/** BOAT 더미 엔진의 기능과 타코미터 타입을 제공합니다. */
interface BoatEngine : KartEngine { override val tachometer: BoatTachometer? }
