package io.github.oni0nfr1.skid.client.api.tachometer

/**
 * 클라이언트 측 액션바에 속도가 표시되는 카트의 타코미터를 나타내는 인터페이스입니다.
 */
sealed interface SpeedTachometer : KartTachometer {

    /**
     * 해당하는 카트의 속도를 제공합니다.
     *
     * 단위는 `km/h`이며 액션바에 표시된 정수 값을 `Double`로 제공합니다.
     * 물리 관련 연산보다는 주행 속도 표시 등의 시각적 표현에 사용하는 것을 권장합니다.
     */
    val speed: Double
}

/** 부스터 개수와 충전 게이지가 표시되는 니트로 엔진의 타코미터입니다. */
sealed interface NitroTachometer : SpeedTachometer {

    /**
     * 카트의 부스터 게이지 진행도를 반환합니다.
     *
     * 0부터 1 사이의 실수로 표현되며,
     * 비어 있을 경우 0, 가득 채워질 경우 1입니다.
     */
    val gauge: Double

    /**
     * 현재 플레이어가 보유한 부스터의 개수를 반환합니다.
     */
    val nitro: Int
}

/** RPM 게이지와 기어 단수가 표시되는 기어 계열 엔진의 타코미터입니다. */
sealed interface GearlikeTachometer : SpeedTachometer {

    /**
     * 카트의 rpm 게이지 값을 반환합니다.
     *
     * 0부터 1 사이의 실수로 표현되며,
     * 비어 있을 경우 0, 가득 채워질 경우 1입니다.
     */
    val rpm: Double

    /**
     * 카트의 기어 단수를 반환합니다.
     */
    val gear: Int
}

/** 익시드 게이지가 표시되는 엔진의 타코미터입니다. */
sealed interface ExceedTachometer : KartTachometer {

    /**
     * 카트의 익시드 게이지 진행도를 반환합니다.
     *
     * 0부터 1 사이의 실수로 표현되며,
     * 비어 있을 경우 0, 가득 채워질 경우 1입니다.
     */
    val exceedGauge: Float
}
