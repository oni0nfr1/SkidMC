package io.github.oni0nfr1.skid.client.api.tachometer

/** X 엔진의 타코미터입니다. */
interface XTachometer : NitroTachometer
/** EX 엔진의 타코미터입니다. */
interface EXTachometer : NitroTachometer
/** JIU 엔진의 타코미터입니다. */
interface JiuTachometer : NitroTachometer
/** NEW 엔진의 타코미터입니다. */
interface NewTachometer : NitroTachometer
/** Z7 엔진의 타코미터입니다. */
interface Z7Tachometer : NitroTachometer
/** V1 엔진의 니트로 및 익시드 타코미터입니다. */
interface V1Tachometer : NitroTachometer, ExceedTachometer
/** A2 엔진의 타코미터입니다. */
interface A2Tachometer : NitroTachometer
/** 1.0 엔진의 타코미터입니다. */
interface LegacyTachometer : NitroTachometer
/** PRO 엔진의 타코미터입니다. */
interface ProTachometer : NitroTachometer
/** RUSH+ 엔진의 니트로 및 익시드 타코미터입니다. */
interface RushPlusTachometer : NitroTachometer, ExceedTachometer {
    /** 퓨전 부스터가 발동 중인지 여부입니다. */
    val fusionActive: Boolean
}
/** CHARGE 엔진의 타코미터입니다. */
interface ChargeTachometer : NitroTachometer {
    /** 차저 게이지 진행도입니다. 값의 범위는 `0.0f..1.0f`입니다. */
    val chargerGauge: Float
}
/** SR 엔진의 타코미터입니다. */
interface SRTachometer : NitroTachometer
// 니트로 엔진 타코미터 (더미)
/** N1 더미 엔진의 타코미터입니다. */
interface N1Tachometer : NitroTachometer
/** RX 더미 엔진의 타코미터입니다. */
interface RXTachometer : NitroTachometer
/** KEY 더미 엔진의 타코미터입니다. */
interface KeyTachometer : NitroTachometer

/** GEAR 더미 엔진의 타코미터입니다. */
interface GearTachometer : GearlikeTachometer
/** RALLY 더미 엔진의 타코미터입니다. */
interface RallyTachometer : GearlikeTachometer
/** F1 더미 엔진의 타코미터입니다. */
interface F1Tachometer : GearlikeTachometer {
    /** 액션바에 표시된 ERS 충전량입니다. */
    val ers: Int
}

/** MK 더미 엔진의 타코미터입니다. */
interface MKTachometer : KartTachometer {
    /** 터보 게이지 진행도입니다. 값의 범위는 `0.0..1.0`입니다. */
    val turboGauge: Double
}
/** BOAT 더미 엔진의 타코미터입니다. */
interface BoatTachometer : KartTachometer
