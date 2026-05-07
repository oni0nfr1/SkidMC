package io.github.oni0nfr1.skid.client.api.tachometer

interface XTachometer : NitroTachometer
interface EXTachometer : NitroTachometer
interface JiuTachometer : NitroTachometer
interface NewTachometer : NitroTachometer
interface Z7Tachometer : NitroTachometer
interface V1Tachometer : NitroTachometer, ExceedTachometer
interface A2Tachometer : NitroTachometer
interface LegacyTachometer : NitroTachometer
interface ProTachometer : NitroTachometer
interface RushPlusTachometer : NitroTachometer, ExceedTachometer { val fusionActive: Boolean }
interface ChargeTachometer : NitroTachometer { val chargerGauge: Float }
interface SRTachometer : NitroTachometer
// 니트로 엔진 타코미터 (더미)
interface N1Tachometer : NitroTachometer
interface RXTachometer : NitroTachometer
interface KeyTachometer : NitroTachometer

interface GearTachometer : GearlikeTachometer
interface RallyTachometer : GearlikeTachometer
interface F1Tachometer : GearlikeTachometer { val ers: Int }

interface MKTachometer : KartTachometer { val turboGauge: Double }
interface BoatTachometer : KartTachometer
