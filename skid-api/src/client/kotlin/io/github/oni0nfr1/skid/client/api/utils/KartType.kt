package io.github.oni0nfr1.skid.client.api.utils

import io.github.oni0nfr1.skid.client.api.engine.*
import io.github.oni0nfr1.skid.client.api.tachometer.*

/**
 * 카트 엔진과 해당 엔진에서 사용하는 타코미터 타입의 조합을 나타냅니다.
 *
 * 각 카트 타입은 구체적인 [KartEngine]과 [KartTachometer] 타입을 함께 지정하므로,
 * 타입이 지정된 카트 참조에 접근할 때 엔진과 타코미터 타입을 동시에 추론할 수 있습니다.
 *
 * 직접 하위 타입을 구현할 수 없으며 SkidMC가 제공하는 타입만 사용할 수 있습니다.
 *
 * @property engineCode `/trigger setengine set <값>`에서 사용하는 엔진 코드
 * @property attrEngineCode 카트 어트리뷰트로 수신되는 엔진 코드
 * @property engineName 주행 중 표시되는 표준 엔진 이름
 * @property engineKind 엔진의 용도
 * @param ENGINE 카트 엔진 타입
 * @param TACHOMETER 해당 엔진에서 사용하는 타코미터 타입
 */
sealed class KartType<out ENGINE, out TACHOMETER>(
    val engineCode: Int,
    val attrEngineCode: Int,
    val engineName: String,
    val engineKind: EngineKind,
)
    where
        ENGINE : KartEngine,
        TACHOMETER : KartTachometer
{
    data object X : KartType<XEngine, XTachometer>(
        engineCode = 10,
        attrEngineCode = 0,
        engineName = "x",
        engineKind = EngineKind.NORMAL,
    )

    data object EX : KartType<EXEngine, EXTachometer>(
        engineCode = 11,
        attrEngineCode = 1,
        engineName = "ex",
        engineKind = EngineKind.NORMAL,
    )

    data object JIU : KartType<JiuEngine, JiuTachometer>(
        engineCode = 12,
        attrEngineCode = 2,
        engineName = "jiu",
        engineKind = EngineKind.NORMAL,
    )

    data object NEW : KartType<NewEngine, NewTachometer>(
        engineCode = 13,
        attrEngineCode = 3,
        engineName = "new",
        engineKind = EngineKind.NORMAL,
    )

    data object Z7 : KartType<Z7Engine, Z7Tachometer>(
        engineCode = 14,
        attrEngineCode = 4,
        engineName = "z7",
        engineKind = EngineKind.NORMAL,
    )

    data object V1 : KartType<V1Engine, V1Tachometer>(
        engineCode = 15,
        attrEngineCode = 5,
        engineName = "v1",
        engineKind = EngineKind.NORMAL,
    )

    data object A2 : KartType<A2Engine, A2Tachometer>(
        engineCode = 16,
        attrEngineCode = 6,
        engineName = "a2",
        engineKind = EngineKind.NORMAL,
    )

    data object LEGACY : KartType<LegacyEngine, LegacyTachometer>(
        engineCode = 17,
        attrEngineCode = 7,
        engineName = "1.0",
        engineKind = EngineKind.NORMAL,
    )

    data object PRO : KartType<ProEngine, ProTachometer>(
        engineCode = 18,
        attrEngineCode = 8,
        engineName = "pro",
        engineKind = EngineKind.NORMAL,
    )

    data object RUSHPLUS : KartType<RushPlusEngine, RushPlusTachometer>(
        engineCode = 19,
        attrEngineCode = 9,
        engineName = "rush+",
        engineKind = EngineKind.NORMAL,
    )

    data object CHARGE : KartType<ChargeEngine, ChargeTachometer>(
        engineCode = 20,
        attrEngineCode = 10,
        engineName = "charge",
        engineKind = EngineKind.NORMAL,
    )

    data object SR : KartType<SREngine, SRTachometer>(
        engineCode = 21,
        attrEngineCode = 11,
        engineName = "sr",
        engineKind = EngineKind.NORMAL,
    )

    data object N1 : KartType<N1Engine, N1Tachometer>(
        engineCode = 1000,
        attrEngineCode = 1000,
        engineName = "n1",
        engineKind = EngineKind.DUMMY,
    )

    data object RX : KartType<RXEngine, RXTachometer>(
        engineCode = 1001,
        attrEngineCode = 1001,
        engineName = "rx",
        engineKind = EngineKind.DUMMY,
    )

    data object KEY : KartType<KeyEngine, KeyTachometer>(
        engineCode = 1002,
        attrEngineCode = 1002,
        engineName = "key",
        engineKind = EngineKind.DUMMY,
    )

    data object MK : KartType<MKEngine, MKTachometer>(
        engineCode = 1003,
        attrEngineCode = 1003,
        engineName = "mk",
        engineKind = EngineKind.DUMMY,
    )

    data object BOAT : KartType<BoatEngine, BoatTachometer>(
        engineCode = 1004,
        attrEngineCode = 1004,
        engineName = "boat",
        engineKind = EngineKind.DUMMY,
    )

    data object GEAR : KartType<GearEngine, GearTachometer>(
        engineCode = 1005,
        attrEngineCode = 1005,
        engineName = "gear",
        engineKind = EngineKind.DUMMY,
    )

    data object F1 : KartType<F1Engine, F1Tachometer>(
        engineCode = 1006,
        attrEngineCode = 1006,
        engineName = "f1",
        engineKind = EngineKind.DUMMY,
    )

    data object RALLY : KartType<RallyEngine, RallyTachometer>(
        engineCode = 1007,
        attrEngineCode = 1007,
        engineName = "rally",
        engineKind = EngineKind.DUMMY,
    )

    companion object {
        /** SkidMC가 지원하는 모든 카트 타입입니다. */
        @JvmField
        val entries: List<KartType<*, *>> = listOf(
            X,
            EX,
            JIU,
            NEW,
            Z7,
            V1,
            A2,
            LEGACY,
            PRO,
            RUSHPLUS,
            CHARGE,
            SR,
            N1,
            RX,
            KEY,
            MK,
            BOAT,
            GEAR,
            F1,
            RALLY,
        )

        private val byEngineCode = entries.associateBy(KartType<*, *>::engineCode)
        private val byAttrEngineCode = entries.associateBy(KartType<*, *>::attrEngineCode)

        init {
            require(byEngineCode.size == entries.size) { "duplicate kart engine code" }
            require(byAttrEngineCode.size == entries.size) { "duplicate kart attribute engine code" }
        }

        /**
         * 명령용 [engineCode]에 대응하는 카트 타입을 반환합니다.
         *
         * 알려지지 않은 코드이면 `null`을 반환합니다.
         */
        @JvmStatic
        fun fromEngineCode(engineCode: Int): KartType<*, *>? = byEngineCode[engineCode]

        /**
         * 어트리뷰트용 [attrEngineCode]에 대응하는 카트 타입을 반환합니다.
         *
         * 알려지지 않은 코드이면 `null`을 반환합니다.
         */
        @JvmStatic
        fun fromAttrEngineCode(attrEngineCode: Int): KartType<*, *>? =
            byAttrEngineCode[attrEngineCode]
    }
}
