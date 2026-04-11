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
        LEGACY(17, 7, false, "1.0", LegacyEngineImpl::class.java), // 타코미터 특이사항: 옛날 디자인
        PRO(18, 8, false, "pro", ProEngineImpl::class.java),
        RUSHPLUS(19, 9, false, "rush+", RushPlusEngineImpl::class.java), // 타코미터 특이사항: 속도 소수점 1자리까지, 퓨전부스터
        CHARGE(20, 10, false, "charge", ChargeEngineImpl::class.java),

        // 더미 엔진
        N1(1000, 1000, true, "n1", N1EngineImpl::class.java),
        // 비어있는 1001번은 RX 엔진, 현재 삭제됨
        KEY(1002, 1002, true, "key", KeyEngineImpl::class.java),
        MK(1003, 1003, true, "mk", MKEngineImpl::class.java), // 타코미터 특이사항: 미니 | 사각사각 | 터보
        BOAT(1004, 1004, true, "boat", BoatEngineImpl::class.java), // 타코미터 특이사항: 타코미터 없음
        GEAR(1005, 1005, true, "gear", GearEngineImpl::class.java), // 타코미터 특이사항: 게이지 대신 엔진 RPM 게이지 표시됨. 부스터 대신 "GEAR N단" 으로 기어 단수가 나옴
        F1(1006, 1006, true, "f1", F1EngineImpl::class.java), // 타코미터 특이사항: GEAR [N]으로 기어 단수 표시, RPM 게이지 표시, ERS [N]으로 ERS 표시
        RALLY(1007, 1007, true, "rally", RallyEngineImpl::class.java); // 타코미터 특이사항: 기어엔진 + 기어단수 GEAR [N]으로

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


/*
 라이프타임 관리
 대구 스폰 패킷이 옴 -> 엔티티 생성 직후 카트 객체 생성
 대구 정보 설정 패킷이 옴 -> 카트 객체에 저장
 주목해야 할 부분: CustomName 등등... (카트가 맞는지를 구별하는 핵심 메타데이터)
 여기까지가 카트 생성 과정

 보통 플레이어가 탄다고 하면 이 이후가 됨
 멀티서버에서의 생성후 자동 탑승도 이 이후에 일어남
 중요한 부분: 카트 자체에는 자세한 정보가 담기지 않음.
 대부분의 정보는 카트를 타고 있는 플레이어에 담김 !!!
 ex) 카트 스탯: 클라이언트 자신의 카트만 알 수 있으며, 플레이어가 든 아이템에 수치가 담김
     어트리뷰트 정보: 자신과 자신이 트래킹 중인 타 유저들의 어트리뷰트로만 알 수 있음
 그렇다면 플레이어가 타지 않은 카트를 INACTIVE로 두고, 플레이어가 타면 ACTIVE로 전환시키는 게 의미가 있음.
 즉, 카트가 스폰되면 Kart 객체는 생성됨.
 그러나 핵심 정보들을 처리해 주는 것(타코미터 파싱, 탑승자 참조, 엔진 고유 데이터 접근)은 KartEngine에 저장되는데,
 UNDEFINED라는 KartEngine의 하위 클래스를 만들어 두고 Kart의 engine 필드에 넣어서 탑승자와 관련된 정보를 요구했을 때 아무것도 안하게 되는 방식임.
 카트의 스탯은 어떻게 읽을 수 있게 할 거냐?
 음
 내 생각에는 Kart에도 구현체 종류를 여러 개 넣는 게 좋아 보임.
 Kart는 인터페이스로 두고, LocalKart는 플레이어 자신이 탄 카트, RemoteKart는 타인이 탄 카트로 둠.

 KartEngine은 sealed class로 둘 것임.
 왜냐하면 KartEngine은 어느 것인지를 구별하는 게 매우 중요한데, abstract로 해 두면 when 분기를 쓸 떄 불리해짐.


 카트 객체 저장은 어디서 하는가?
 따로 저장소를 만드는 게 좋으려나
 그냥 companion object에 MutableMap으로 저장해도 됨
*/




