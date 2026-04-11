package io.github.oni0nfr1.skid.client.api.engine

sealed interface RegularEngine : KartEngine {

    /**
     * 해당하는 카트의 드리프트 상태를 나타냅니다.
     *
     * 드리프트 중일 경우 true, 아니면 false를 반환합니다.
     */
    val isDrifting: Boolean

}

sealed interface NitroEngine : RegularEngine {

    /**
     * 해당하는 카트의 부스터 발동 여부입니다.
     *
     * 부스터가 발동 중일 경우 true를, 아니면 false를 반환합니다.
     */
    val isBoosting: Boolean

    /**
     * 해당하는 카트의 최대 부스터 개수를 반환합니다.
     */
    val maxBoost: Int

    /**
     * 해당하는 카트가 순간 부스터 발동이 가능지 여부를 반환합니다.
     *
     * 발동 가능한 경우 true, 아니면 false를 반환합니다.
     */
    val instantBoostReady: Boolean
}

sealed interface GearlikeEngine : RegularEngine
