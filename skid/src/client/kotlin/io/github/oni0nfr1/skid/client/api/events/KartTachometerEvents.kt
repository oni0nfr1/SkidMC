package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import net.minecraft.network.chat.Component

/**
 * 카트 타코미터 액션바의 파싱 결과를 단계별로 제공합니다.
 *
 * ## 참고
 * SkidMC의 액션바 처리 파이프라인은 **서버**로부터 액션바 패킷이 도착했을 때만 작동합니다.
 *
 * 따라서 타 클라이언트 모드에서 액션바 출력 메서드를 호출하더라도 SkidMC에서는 그것을 처리하지 않습니다.
 */
object KartTachometerEvents {

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신할 경우 호출됩니다.
     * 모든 다른 타코미터 이벤트가 호출되고 나서, 결과적으로 알맞는 타코미터라는 결론이 나올 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val RECEIVE = createEvent { listeners ->
        KartTachometerCallback { kart, engine, text ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onActionbarReceive(kart, engine, text)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 속도값이 파싱될 경우 호출됩니다.
     *
     * [Kart.velocity]와 달리 HUD 표시 등의 시각적 목적으로 사용하는 것을 권장합니다.
     *
     * 렌더 스레드에서 호출됩니다.
     * @see Kart.velocity
     */
    @Suppress("UNUSED")
    @JvmField
    val SPEED = createEvent { listeners ->
        KartSpeedCallback { speed ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onSpeedUpdate(speed)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 부스터 개수 값이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val NITRO = createEvent { listeners ->
        KartNitroCallback { nitro ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onNitroUpdate(nitro)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 게이지 값이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val GAUGE = createEvent { listeners ->
        KartGaugeCallback { gauge ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onGaugeUpdate(gauge)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 기어류 엔진 RPM 값이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val RPM = createEvent { listeners ->
        KartRpmCallback { rpm ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onRpmUpdate(rpm)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 기어류 엔진 기어 단수가 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val GEAR = createEvent { listeners ->
        KartGearCallback { rpm ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onGearUpdate(rpm)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 F1 엔진의 ERS 충전량이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val ERS = createEvent { listeners ->
        KartErsCallback { ers ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onErsUpdate(ers)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 마리오카트 엔진의 터보 게이지 충전량이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val MK_GAUGE = createEvent { listeners ->
        MarioKartGaugeCallback { gauge ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onGaugeUpdate(gauge)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /** 정상 파싱된 타코미터 액션바를 처리합니다. */
    fun interface KartTachometerCallback {
        /**
         * @param kart 액션바와 연결된 현재 카트
         * @param engine 액션바와 연결된 현재 엔진
         * @param text 수신한 원본 액션바 컴포넌트
         * @return 액션바 표시 여부
         */
        fun onActionbarReceive(kart: Kart, engine: KartEngine, text: Component): Result
    }

    /** 파싱된 속도 값을 처리합니다. */
    fun interface KartSpeedCallback {
        /**
         * @param speed 액션바에 표시된 속도(`km/h`)
         * @return 액션바 표시 여부
         */
        fun onSpeedUpdate(speed: Double): Result
    }

    /** 파싱된 부스터 개수를 처리합니다. */
    fun interface KartNitroCallback {
        /**
         * @param nitro 현재 보유한 부스터 개수
         * @return 액션바 표시 여부
         */
        fun onNitroUpdate(nitro: Int): Result
    }

    /** 파싱된 부스터 게이지를 처리합니다. */
    fun interface KartGaugeCallback {
        /**
         * @param gauge `0.0..1.0` 범위의 부스터 게이지 진행도
         * @return 액션바 표시 여부
         */
        fun onGaugeUpdate(gauge: Double): Result
    }

    /** 파싱된 MK 터보 게이지를 처리합니다. */
    fun interface MarioKartGaugeCallback {
        /**
         * @param gauge `0.0..1.0` 범위의 터보 게이지 진행도
         * @return 액션바 표시 여부
         */
        fun onGaugeUpdate(gauge: Double): Result
    }

    /** 파싱된 RPM 게이지를 처리합니다. */
    fun interface KartRpmCallback {
        /**
         * @param rpm `0.0..1.0` 범위의 RPM 게이지 진행도
         * @return 액션바 표시 여부
         */
        fun onRpmUpdate(rpm: Double): Result
    }

    /** 파싱된 기어 단수를 처리합니다. */
    fun interface KartGearCallback {
        /**
         * @param gear 현재 기어 단수
         * @return 액션바 표시 여부
         */
        fun onGearUpdate(gear: Int): Result
    }

    /** 파싱된 F1 ERS 값을 처리합니다. */
    fun interface KartErsCallback {
        /**
         * @param ers 액션바에 표시된 ERS 충전량
         * @return 액션바 표시 여부
         */
        fun onErsUpdate(ers: Int): Result
    }

    /**
     * 액션바 이벤트의 처리 결과 반환 타입입니다.
     * - [SHOW]: 액션바 메시지를 정상적으로 처리합니다.
     * - [BLOCK]: 액션바 메시지가 화면에서 가려집니다. (이후 이벤트 리스너들은 정상적으로 작동합니다.)
     */
    enum class Result {
        SHOW,
        BLOCK;

        /** 여러 이벤트 결과를 병합하는 함수를 제공합니다. */
        companion object {
            /**
             * 여러 이벤트 결과를 하나의 최종 결과로 병합합니다.
             *
             * 하나라도 [BLOCK]이면 [BLOCK], 모두 [SHOW]이면 [SHOW]를 반환합니다.
             *
             * @param results 병합할 이벤트 결과
             * @return 병합된 최종 결과
             */
            fun finalize(vararg results: Result): Result {
                results.forEach {
                    if (it == BLOCK) return BLOCK
                }
                return SHOW
            }
        }
    }
}
