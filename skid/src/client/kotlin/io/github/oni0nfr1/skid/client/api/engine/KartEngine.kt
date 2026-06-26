package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.SkidClient
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.internal.engine.specific.*
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

/**
 * 카트와 탑승자의 주행 상태를 엔진 종류에 맞게 제공합니다.
 *
 * 엔진마다 구현이 달라지는 메서드들을 여기서 호출할 수 있으며, 타입 체크를 통해 엔진 종류를 확인할 수 있습니다.
 */
sealed interface KartEngine {

    /** 엔진 구현 생성에 사용하는 내부 팩토리입니다. */
    companion object {
        protected val client: Minecraft by MCClient

        internal fun withType(type: Type, kart: Kart, rider: Player): KartEngine {
            val clazz = type.clazz
            return clazz.constructors.first().newInstance(kart, rider) as KartEngine
        }
    }

    /**
     * 이 엔진이 연결된 [Kart] 객체입니다.
     */
    val kart: Kart

    /**
     * 이 엔진이 연결된 카트의 탑승자입니다.
     */
    val rider: Player

    /**
     * 엔진의 식별 타입입니다.
     */
    val type: Type

    /**
     * 이 엔진에 대응하는 현재 타코미터입니다.
     *
     * 엔진의 탑승자가 현재 클라이언트 플레이어 또는 관전 대상이 아니거나,
     * 아직 유효한 타코미터가 수신되지 않았으면 `null`입니다.
     */
    val tachometer: KartTachometer?

    /**
     * 카트가 현재까지 주행한 랩 수를 반환합니다.
     *
     * 레이스 중이 아닐 경우 기본값은 0입니다.
     */
    val currentLap: Int

    /**
     * SkidMC가 인식하는 카트 엔진 타입입니다.
     *
     * @property engineCode `/trigger setengine set <값>`에서 사용하는 엔진 코드
     * @property attrEngineCode 라이더 메타데이터 어트리뷰트로 수신되는 엔진 코드
     * @property isDummy 정식 엔진이 아닌 호환용 더미 엔진인지 여부
     * @property engineName 주행 중 플레이어 위 텍스트에서 사용하는 표준 엔진 이름
     */
    enum class Type(
        val engineCode: Int,
        val attrEngineCode: Int,
        val isDummy: Boolean,
        val engineName: String,
        internal val clazz: Class<out KartEngine>
    ) {
        // 공식 엔진
        X(10, 0, false, "x", XEngineImpl::class.java),
        EX(11, 1, false, "ex", EXEngineImpl::class.java),
        JIU(12, 2, false, "jiu", JiuEngineImpl::class.java),
        NEW(13, 3, false, "new", NewEngineImpl::class.java),
        Z7(14, 4, false, "z7", Z7EngineImpl::class.java),
        V1(15, 5, false, "v1", V1EngineImpl::class.java),
        A2(16, 6, false, "a2", A2EngineImpl::class.java),
        LEGACY(17, 7, false, "1.0", LegacyEngineImpl::class.java),
        PRO(18, 8, false, "pro", ProEngineImpl::class.java),
        RUSHPLUS(19, 9, false, "rush+", RushPlusEngineImpl::class.java),
        CHARGE(20, 10, false, "charge", ChargeEngineImpl::class.java),
        SR(21, 11, false, "sr", SREngineImpl::class.java),

        // 더미 엔진
        N1(1000, 1000, true, "n1", N1EngineImpl::class.java),
        RX(1001, 1001, true, "rx", RXEngineImpl::class.java),
        KEY(1002, 1002, true, "key", KeyEngineImpl::class.java),
        MK(1003, 1003, true, "mk", MKEngineImpl::class.java),
        BOAT(1004, 1004, true, "boat", BoatEngineImpl::class.java),
        GEAR(1005, 1005, true, "gear", GearEngineImpl::class.java),
        F1(1006, 1006, true, "f1", F1EngineImpl::class.java),
        RALLY(1007, 1007, true, "rally", RallyEngineImpl::class.java);

        /** 엔진 코드를 타입으로 변환하는 조회 함수를 제공합니다. */
        companion object {
            private val byEngineCode = entries.associateBy { it.engineCode }
            private val byAttrEngineCode = entries.associateBy { it.attrEngineCode }

            /**
             * [engineCode]와 일치하는 엔진 타입을 반환합니다.
             *
             * @param engineCode 변환할 카트 엔진 코드
             * @return 대응하는 엔진 타입, 알려지지 않은 코드이면 `null`
             */
            @JvmStatic
            fun getByCode(engineCode: Int): Type? {
                val result = byEngineCode[engineCode]
                if (result == null) SkidClient.LOGGER.warn("unknown engine code: $engineCode")
                return result
            }

            /**
             * 라이더 메타데이터의 raw 엔진 값을 엔진 타입으로 변환합니다.
             *
             * 소수점 이하는 버리고 [attrEngineCode]와 비교합니다.
             *
             * @param raw 변환할 라이더 메타데이터 값
             * @return 대응하는 엔진 타입, 알려지지 않은 코드이면 `null`
             */
            @JvmStatic
            fun getByRawModifier(raw: Double): Type? {
                val result = byAttrEngineCode[raw.toInt()]
                if (result == null) SkidClient.LOGGER.warn("unknown modifier engine code: $raw")
                return result
            }
        }
    }
}

