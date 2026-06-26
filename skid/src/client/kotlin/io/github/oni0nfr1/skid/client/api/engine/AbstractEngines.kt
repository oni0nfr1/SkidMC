package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.tachometer.ExceedTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearlikeTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.NitroTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.SpeedTachometer

/** 드리프트 상태를 제공하는 엔진입니다. */
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

/** 속도 타코미터를 사용하는 엔진입니다. */
sealed interface SpeedEngine : KartEngine {
    /** 이 엔진에 대응하는 속도 타코미터이며, 현재 타코미터를 사용할 수 없으면 `null`입니다. */
    override val tachometer: SpeedTachometer?
}

/** 부스터와 드리프트 기능을 제공하는 엔진입니다. */
sealed interface NitroEngine : DriftEngine, SpeedEngine {

    /**
     * 해당하는 카트의 부스터 발동 여부입니다.
     *
     * @return 부스터가 발동 중일 경우 `true`를, 아니면 `false`
     */
    val isBoosting: Boolean

    /**
     * 해당 카트가 보유할 수 있는 최대 부스터 개수를 반환합니다.
     *
     * 관련 라이더 메타데이터가 없으면 `0`입니다.
     */
    val maxBoost: Int

    /** 이 엔진에 대응하는 니트로 타코미터이며, 현재 타코미터를 사용할 수 없으면 `null`입니다. */
    override val tachometer: NitroTachometer?
}

/** RPM과 기어 단수를 제공하는 엔진입니다. */
sealed interface GearlikeEngine : DriftEngine, SpeedEngine {
    /** 이 엔진에 대응하는 기어 타코미터이며, 현재 타코미터를 사용할 수 없으면 `null`입니다. */
    override val tachometer: GearlikeTachometer?
}

/** 순간 부스터 상태를 제공하는 니트로 엔진입니다. */
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

/** 듀얼 부스터의 충전 및 발동 상태를 제공하는 니트로 엔진입니다. */
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

/** 드래프트의 충전 및 발동 상태를 제공하는 엔진입니다. */
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

/** 익시드 게이지를 제공하는 엔진입니다. */
sealed interface ExceedEngine : KartEngine {
    /** 이 엔진에 대응하는 익시드 타코미터이며, 현재 타코미터를 사용할 수 없으면 `null`입니다. */
    override val tachometer: ExceedTachometer?
}
