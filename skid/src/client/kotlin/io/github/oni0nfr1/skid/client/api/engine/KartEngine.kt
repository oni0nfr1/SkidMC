package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.SkidClient
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.engine.specific.*
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

/**
 * 카트의 엔진을 나타내는 객체입니다.
 *
 * 엔진마다 구현이 달라지는 메서드들을 여기서 호출할 수 있으며, 타입 체크를 통해 엔진 종류를 확인할 수 있습니다.
 */
sealed interface KartEngine {

    companion object {
        protected val client: Minecraft by MCClient

        internal fun withType(type: Type, kart: Kart, rider: Player): KartEngine {
            val clazz = type.clazz
            return clazz.constructors.first().newInstance(kart, rider) as KartEngine
        }
    }

    val kart: Kart
    val rider: Player
    val type: Type

    enum class Type(val engineCode: Int, val attrEngineCode: Int, val isDummy: Boolean, val engineName: String, internal val clazz: Class<out KartEngine>) {
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

        // 더미 엔진
        N1(1000, 1000, true, "n1", N1EngineImpl::class.java),
        // 비어있는 1001번은 RX 엔진, 현재 삭제됨
        KEY(1002, 1002, true, "key", KeyEngineImpl::class.java),
        MK(1003, 1003, true, "mk", MKEngineImpl::class.java),
        BOAT(1004, 1004, true, "boat", BoatEngineImpl::class.java),
        GEAR(1005, 1005, true, "gear", GearEngineImpl::class.java),
        F1(1006, 1006, true, "f1", F1EngineImpl::class.java),
        RALLY(1007, 1007, true, "rally", RallyEngineImpl::class.java);

        companion object {
            private val byEngineCode = entries.associateBy { it.engineCode }
            private val byAttrEngineCode = entries.associateBy { it.attrEngineCode }

            /**
             * @return 카트 엔진 코드를 [KartEngine.Type]으로 변환한 결과.
             *
             * 유효하지 않은 엔진 코드일 경우 null을 반환합니다.
             */
            @JvmStatic
            fun getByCode(engineCode: Int): Type? {
                val result = byEngineCode[engineCode]
                if (result == null) SkidClient.LOGGER.warn("unknown engine code: $engineCode")
                return result
            }

            @JvmStatic
            fun getByRawModifier(raw: Double): Type? {
                val result = byAttrEngineCode[raw.toInt()]
                if (result == null) SkidClient.LOGGER.warn("unknown modifier engine code: $raw")
                return result
            }
        }
    }
}



