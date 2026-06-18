package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.tachometer.ExceedTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearlikeTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.NitroTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.SpeedTachometer

sealed interface DriftEngine : KartEngine {

    /**
     * 해당하는 카트의 드리프트 상태를 나타냅니다.
     *
     * 드리프트 중일 경우 `true`, 아니면 `false`를 반환합니다.
     *
     * 상황에 따라서 감지가 1틱씩 밀릴 수 있습니다.
     * 틱 단위 정확성이 필요하면 [accurateDriftState]를 사용해 주세요.
     */
    val isDrifting: Boolean

    /**
     * 해당하는 카트의 드리프트 상태를 나타냅니다.
     *
     * [isDrifting]과는 다르게, 공중에 떠 있는 상황에서는 `false`이며,
     * 어트리뷰트 기반 방식으로 동작하지 않기 때문에 완전한 틱 단위 정확도를 제공합니다.
     */
    val accurateDriftState: Boolean
}

sealed interface SpeedEngine : KartEngine {
    override val tachometer: SpeedTachometer?
}

sealed interface NitroEngine : DriftEngine, SpeedEngine {

    /**
     * 해당하는 카트의 부스터 발동 여부입니다.
     *
     * @return 부스터가 발동 중일 경우 `true`를, 아니면 `false`
     */
    val isBoosting: Boolean

    /**
     * 해당하는 카트의 최대 부스터 개수를 반환합니다.
     */
    val maxBoost: Int

    override val tachometer: NitroTachometer?
}

sealed interface GearlikeEngine : DriftEngine, SpeedEngine {
    override val tachometer: GearlikeTachometer?
}

sealed interface InstantBoostEngine : NitroEngine {
    /**
     * 해당하는 카트가 지금 순간 부스터 발동이 가능한지 여부를 반환합니다.
     *
     * @return 발동 가능한 경우 `true`, 아니면 `false`
     */
    val instantBoostReady: Boolean

    /**
     * 해당하는 카트의 순간 부스터 기능이 활성화되어 있는지 여부를 반환합니다.
     *
     * @return 기능이 활성화된 경우 `true`, 아니면 `false`
     */
    val instantBoostEnabled: Boolean
}

sealed interface DualBoostEngine : NitroEngine {
    /**
     * 해당하는 카트의 듀얼 부스터가 발동 중인지를 반환합니다.
     *
     * @return 발동 중인 경우 `true`, 아니면 `false`
     */
    val dualBoostActive: Boolean

    /**
     * 해당하는 카트의 듀얼 부스터가 충전 중인지를 반환합니다.
     *
     * @return 충전 중인 경우 `true`, 아니면 `false`
     */
    val dualBoostCharging: Boolean
}

sealed interface DraftEngine : KartEngine {
    /**
     * 해당하는 카트의 드래프트가 발동 중인지를 반환합니다.
     *
     * @return 발동 중인 경우 `true`, 아니면 `false`
     */
    val draftActive: Boolean

    /**
     * 해당하는 카트의 드래프트가 충전 중인지를 반환합니다.
     *
     * @return 충전 중인 경우 `true`, 아니면 `false`
     */
    val draftCharging: Boolean
}

sealed interface ExceedEngine : KartEngine {
    override val tachometer: ExceedTachometer?
}