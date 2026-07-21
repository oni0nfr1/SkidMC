package io.github.oni0nfr1.skid.client.api.events.unstable

import io.github.oni0nfr1.skid.client.api.events.createEvent
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle

/**
 * SkidMC가 인식하는 카트 정보 modifier별 변경 이벤트를 제공합니다.
 *
 * 마크라이더 데이터팩의 modifier 구성을 직접 반영하므로 의도적으로 unstable 상태를
 * 유지하며 패치 버전에서도 호환성 없이 변경될 수 있습니다. 카트 상태 변경 정보의 제공
 * 자체는 계속 지원합니다.
 *
 * 이벤트는 새 값이 엔티티에 적용된 후 렌더 스레드에서 호출됩니다. 새 스냅샷에서
 * modifier가 사라지는 경우는 이벤트를 발행하지 않습니다.
 */
object KartAttrModifierEvents {
    /** 선택한 엔진 코드가 변경될 때 호출됩니다. */
    @JvmField val ID_ENGINE = createModifierEvent()
    /** 현재 카트의 실제 엔진 코드가 변경될 때 호출됩니다. */
    @JvmField val ID_ENGINE_REAL = createModifierEvent()
    /** 트랙의 최대 랩 수가 변경될 때 호출됩니다. */
    @JvmField val CTX_MAX_LAP = createModifierEvent()
    /** 카트의 현재 랩이 변경될 때 호출됩니다. */
    @JvmField val CTX_CURRENT_LAP = createModifierEvent()
    /** 순간 부스터 기능의 활성화 상태가 변경될 때 호출됩니다. */
    @JvmField val CAN_IBOOST = createModifierEvent()
    /** 순간 부스터 사용 가능 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_IBOOST = createModifierEvent()
    /** 드리프트 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_DRIFTING = createModifierEvent()
    /** 일반·듀얼 부스터 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_NITRO = createModifierEvent()
    /** 최대 부스터 수가 변경될 때 호출됩니다. */
    @JvmField val CAP_NITRO_COUNT = createModifierEvent()
    /** 드래프트 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_DRAFT_ACCEL = createModifierEvent()
    /** 보유한 팀 부스터 개수가 변경될 때 호출됩니다. */
    @JvmField val STATE_TEAM_NITRO_COUNT = createModifierEvent()
    /** 카트 성능 제한 단계가 변경될 때 호출됩니다. */
    @JvmField val CTX_PERF_LIMIT = createModifierEvent()
    /** 카트 타이어 설정이 변경될 때 호출됩니다. */
    @JvmField val ID_TIRE = createModifierEvent()
    /** 카트 바디 종류가 변경될 때 호출됩니다. */
    @JvmField val ID_BODY_TYPE = createModifierEvent()
    /** 카트 모델 회전 허용 상태가 변경될 때 호출됩니다. */
    @JvmField val STATE_MODEL_ROTATION_ALLOWED = createModifierEvent()

    private fun createModifierEvent() = createEvent { listeners ->
        KartAttrModifierCallback { saddle, previousValue, value ->
            for (listener in listeners) {
                listener.onChange(saddle, previousValue, value)
            }
        }
    }

    /** 개별 카트 정보 modifier의 추가 또는 값 변경을 처리합니다. */
    fun interface KartAttrModifierCallback {
        /**
         * @param saddle 값이 변경된 카트의 대구 엔티티
         * @param previousValue 이전 값. 처음 추가된 modifier이면 `null`
         * @param value 엔티티에 적용된 새 값
         */
        fun onChange(saddle: KartSaddle, previousValue: Double?, value: Double)
    }
}
