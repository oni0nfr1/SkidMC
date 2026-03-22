package io.github.oni0nfr1.skid.client.api.engine

import net.minecraft.network.chat.Component

// 니트로 엔진
interface XEngine : NitroEngine
interface EXEngine : NitroEngine
interface JiuEngine : NitroEngine
interface NewEngine : NitroEngine
interface Z7Engine : NitroEngine
interface V1Engine : NitroEngine
interface A2Engine : NitroEngine
interface LegacyEngine : NitroEngine
interface ProEngine : NitroEngine
interface RushPlusEngine : NitroEngine
interface ChargeEngine : NitroEngine
// 니트로 엔진 (더미)
interface N1Engine : NitroEngine
interface KeyEngine : NitroEngine

// 기어류 엔진 (더미)
interface GearEngine : GearlikeEngine
interface RallyEngine : GearlikeEngine
interface F1Engine : GearlikeEngine {

    val ers: Int

    /**
     * 주어진 액션바에서 ERS 값을 추출한 결과를 반환합니다. ERS 값이 확인되지 않으면 null을 반환합니다.
     */
    fun parseErs(actionBar: Component): Int?
}

// 기타 엔진 (더미)
interface MKEngine : KartEngine {

    val turboGauge: Double

    /**
     * 주어진 액션바에서 마리오카트 엔진 터보 게이지 값을 추출한 결과를 반환합니다.
     * 게이지 값이 확인되지 않을 경우 null을 반환합니다.
     */
    fun parseTurboGauge(actionBar: Component): Double?
}
interface BoatEngine : KartEngine
