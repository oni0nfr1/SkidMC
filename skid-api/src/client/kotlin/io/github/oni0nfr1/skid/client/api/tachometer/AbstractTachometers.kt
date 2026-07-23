package io.github.oni0nfr1.skid.client.api.tachometer

/** 클라이언트 화면에 속도가 표시되는 카트의 타코미터입니다. */
sealed interface SpeedTachometer : KartTachometer {

    /**
     * 화면에 표시된 카트의 속도입니다.
     *
     * 단위는 `km/h`이며 정수로 표시된 값을 [Double]로 제공합니다.
     */
    val speed: Double
}

/** 부스터 개수와 충전 게이지가 표시되는 니트로 엔진의 타코미터입니다. */
sealed interface NitroTachometer : SpeedTachometer {

    /** 부스터 게이지 진행도입니다. 값의 범위는 `0.0..1.0`입니다. */
    val gauge: Double

    /** 현재 보유한 부스터 개수입니다. */
    val nitro: Int
}

/** RPM 게이지와 기어 단수가 표시되는 기어 계열 엔진의 타코미터입니다. */
sealed interface GearLikeTachometer : SpeedTachometer {

    /** RPM 게이지 진행도입니다. 값의 범위는 `0.0..1.0`입니다. */
    val rpm: Double

    /** 현재 기어 단수입니다. */
    val gear: Int
}

/** 익시드 게이지가 표시되는 엔진의 타코미터입니다. */
sealed interface ExceedTachometer : KartTachometer {

    /** 익시드 게이지 진행도입니다. 값의 범위는 `0.0f..1.0f`입니다. */
    val exceedGauge: Float
}

/**
 * MK 계열 더미 엔진의 공통 타코미터입니다.
 *
 * [MKTachometer]와 [DSTachometer]가 공유하는 주행 정보 계약입니다.
 */
sealed interface MKLikeTachometer : KartTachometer {
    /** 터보 게이지 진행도입니다. 값의 범위는 `0.0..1.0`입니다. */
    val turboGauge: Double
}
