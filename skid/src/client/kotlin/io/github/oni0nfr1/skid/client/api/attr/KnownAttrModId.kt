package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.resources.ResourceLocation

/**
 * SkidMC가 인식하는 카트 메타데이터 modifier ID 모음입니다.
 *
 * ## API 안정성
 * 이 객체의 필드와 각 modifier의 의미는 안정적인 API로 간주되지 않습니다.
 * API 버전에 따라 필드가 예고 없이 변경되거나 제거될 수 있습니다.
 *
 * 실제 모드에서 이 값을 직접 사용하는 경우 API 버전을 여러 단계 건너뛰어 업그레이드하지 않는 것을 권장합니다.
 * 버전별 변경 사항을 순서대로 확인하고 각 modifier의 의미와 값 범위가 변경되지 않았는지 검토해야 합니다.
 *
 * 필드 이름은 원본 modifier key가 아니라 값의 역할을 나타냅니다.
 */
object KnownAttrModId {
    // 엔진 구분
    /** 플레이어가 선택한 엔진 코드를 담는 modifier ID입니다. */
    @JvmField val ID_ENGINE: ResourceLocation = ResourceLocation.withDefaultNamespace("data-engine")
    /** 현재 탑승한 카트의 실제 엔진 코드를 담는 modifier ID입니다. */
    @JvmField val ID_ENGINE_REAL: ResourceLocation = ResourceLocation.withDefaultNamespace("data-engine-real")

    // 랩 수 관련
    /** 트랙의 최대 랩 수를 담는 modifier ID입니다. */
    @JvmField val CTX_MAX_LAP: ResourceLocation = ResourceLocation.withDefaultNamespace("data-max-lap")
    /** 플레이어의 현재 랩을 담는 modifier ID입니다. */
    @JvmField val CTX_CURRENT_LAP: ResourceLocation = ResourceLocation.withDefaultNamespace("data-current-lap")

    // 순간부스터 상태
    /** 순간 부스터 기능의 활성화 상태를 담는 modifier ID입니다. */
    @JvmField val CAN_IBOOST: ResourceLocation = ResourceLocation.withDefaultNamespace("skill-instant-boost")
    /** 순간 부스터의 사용 가능 상태를 담는 modifier ID입니다. */
    @JvmField val STATE_IBOOST: ResourceLocation = ResourceLocation.withDefaultNamespace("state-instant-boost")

    // 정규 카트 상태
    /** 드리프트 상태를 담는 modifier ID입니다. */
    @JvmField val STATE_DRIFTING: ResourceLocation = ResourceLocation.withDefaultNamespace("state-drift")
    /** 일반·듀얼 부스터 상태를 담는 modifier ID입니다. */
    @JvmField val STATE_NITRO: ResourceLocation = ResourceLocation.withDefaultNamespace("state-boost")
    /** 카트가 보유할 수 있는 최대 부스터 수를 담는 modifier ID입니다. */
    @JvmField val CAP_NITRO_COUNT: ResourceLocation = ResourceLocation.withDefaultNamespace("data-max-boost-count")
    /** 드래프트 충전 및 발동 상태를 담는 modifier ID입니다. */
    @JvmField val STATE_DRAFT_ACCEL: ResourceLocation = ResourceLocation.withDefaultNamespace("state-draft")
    /** 보유한 팀 부스터 개수 값을 담는 modifier ID입니다. */
    @JvmField val STATE_TEAM_NITRO_COUNT: ResourceLocation = ResourceLocation.withDefaultNamespace("data-team-boost-count")

    // 특수 설정
    /** 카트 성능 제한 단계를 담는 modifier ID입니다. */
    @JvmField val CTX_PERF_LIMIT: ResourceLocation = ResourceLocation.withDefaultNamespace("data-performance-limit-level")
    /** 카트 타이어 설정을 담는 modifier ID입니다. */
    @JvmField val ID_TIRE: ResourceLocation = ResourceLocation.withDefaultNamespace("data-tire")
    /**
     * 플레이어의 조작을 통해 카트바디의 모델링이 회전하게 되는지 여부를 담는 modifier ID입니다.
     *
     * 이 값이 1일 경우 공식 모드 MCRider에서는 클라이언트 측에서 카트 모델을 플레이어 시선으로 강제 정렬하게 됩니다.
     */
    @JvmField val STATE_MODEL_ROTATION_ALLOWED = ResourceLocation.withDefaultNamespace("state-allow-model-rotation")
    /** 카트 바디 종류를 담는 modifier ID입니다. */
    @JvmField val ID_BODY_TYPE: ResourceLocation = ResourceLocation.withDefaultNamespace("data-is-bike")
}
