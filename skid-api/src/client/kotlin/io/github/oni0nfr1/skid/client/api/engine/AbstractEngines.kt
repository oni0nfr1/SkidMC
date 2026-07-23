package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.ExceedTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearLikeTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.MKLikeTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.NitroTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.SpeedTachometer

/** 드리프트 상태를 제공하는 엔진입니다. */
sealed interface DriftEngine : KartEngine {
    override val kart: Kart<DriftEngine>

    /** 해당 카트가 드리프트 중인지 여부입니다. */
    val isDrifting: Boolean

    /** 공중 상태를 제외하고 틱 단위로 판정한 드리프트 상태입니다. */
    val accurateDriftState: Boolean
}

/** 속도 타코미터를 사용하는 엔진입니다. */
sealed interface SpeedEngine : KartEngine {
    override val kart: Kart<SpeedEngine>
    override val tachometer: SpeedTachometer?
}

/** 부스터와 드리프트 기능을 제공하는 엔진입니다. */
sealed interface NitroEngine : DriftEngine, SpeedEngine {
    override val kart: Kart<NitroEngine>
    override val tachometer: NitroTachometer?

    /** 해당 카트가 부스터를 사용 중인지 여부입니다. */
    val isBoosting: Boolean

    /** 해당 카트가 보유할 수 있는 최대 부스터 개수입니다. */
    val maxBoost: Int
}

/** RPM과 기어 단수를 제공하는 엔진입니다. */
sealed interface GearLikeEngine : DriftEngine, SpeedEngine {
    override val kart: Kart<GearLikeEngine>
    override val tachometer: GearLikeTachometer?
}

/** 순간 부스터 상태를 제공하는 니트로 엔진입니다. */
sealed interface InstantBoostEngine : NitroEngine {
    override val kart: Kart<InstantBoostEngine>

    /** 지금 순간 부스터를 발동할 수 있는지 여부입니다. */
    val instantBoostReady: Boolean

    /** 순간 부스터 기능이 활성화되어 있는지 여부입니다. */
    val instantBoostEnabled: Boolean
}

/** 듀얼 부스터의 충전 및 발동 상태를 제공하는 니트로 엔진입니다. */
sealed interface DualBoostEngine : NitroEngine {
    override val kart: Kart<DualBoostEngine>

    /** 듀얼 부스터가 발동 중인지 여부입니다. */
    val dualBoostActive: Boolean

    /** 듀얼 부스터가 충전 중인지 여부입니다. */
    val dualBoostCharging: Boolean
}

/** 드래프트의 충전 및 발동 상태를 제공하는 엔진입니다. */
sealed interface DraftEngine : KartEngine {
    override val kart: Kart<DraftEngine>

    /** 드래프트가 발동 중인지 여부입니다. */
    val draftActive: Boolean

    /** 드래프트가 충전 중인지 여부입니다. */
    val draftCharging: Boolean
}

/** 익시드 게이지를 제공하는 엔진입니다. */
sealed interface ExceedEngine : KartEngine {
    override val kart: Kart<ExceedEngine>
    override val tachometer: ExceedTachometer?
}

/**
 * 터보 게이지를 사용하는 MK 계열 엔진입니다.
 *
 * [MKEngine]과 [DSEngine]이 공유하는 기능 계약이며, 각 엔진은 자신의 구체적인 카트와
 * 타코미터 타입으로 프로퍼티를 좁힙니다.
 */
sealed interface MKLikeEngine : DriftEngine, DraftEngine {
    override val kart: Kart<MKLikeEngine>
    override val tachometer: MKLikeTachometer?
}
