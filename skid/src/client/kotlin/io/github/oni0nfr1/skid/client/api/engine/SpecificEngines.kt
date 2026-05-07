package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.tachometer.*

// 니트로 엔진
interface XEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine { override val tachometer: XTachometer? }
interface EXEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine { override val tachometer: EXTachometer? }
interface JiuEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: JiuTachometer? }
interface NewEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: NewTachometer? }
interface Z7Engine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: Z7Tachometer? }
interface V1Engine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine, ExceedEngine { override val tachometer: V1Tachometer? }
interface A2Engine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: A2Tachometer? }
interface LegacyEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine { override val tachometer: LegacyTachometer? }
interface ProEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: ProTachometer? }
interface RushPlusEngine : NitroEngine, InstantBoostEngine, DraftEngine, ExceedEngine { override val tachometer: RushPlusTachometer? }
interface ChargeEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: ChargeTachometer? }
interface SREngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: SRTachometer? }
// 니트로 엔진 (더미)
interface N1Engine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: N1Tachometer? }
interface RXEngine : NitroEngine, InstantBoostEngine, DraftEngine { override val tachometer: RXTachometer? }
interface KeyEngine : NitroEngine { override val tachometer: KeyTachometer? }

// 기어류 엔진 (더미)
interface GearEngine : GearlikeEngine, DraftEngine { override val tachometer: GearTachometer? }
interface RallyEngine : GearlikeEngine, DraftEngine { override val tachometer: RallyTachometer? }
interface F1Engine : GearlikeEngine, DraftEngine { override val tachometer: F1Tachometer? }

// 기타 엔진 (더미)
interface MKEngine : DriftEngine, DraftEngine { override val tachometer: MKTachometer? }
interface BoatEngine : KartEngine { override val tachometer: BoatTachometer? }
