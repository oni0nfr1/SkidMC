package io.github.oni0nfr1.skid.client.api.engine

import net.minecraft.network.chat.Component

sealed interface NitroEngine : KartEngine {

    /**
     * 해당하는 카트의 가장 최근에 감지된 게이지값입니다.
     */
    val gauge: Double

    /**
     * 해당하는 카트의 가장 최근에 감지된 속도값입니다.
     *
     * ## NOTE
     * 반환값이 [Double]인 이유는 RUSH+ 엔진의 속도계는 소수점 1자리까지 표시되기 때문입니다.
     * 기본적으로는 소수점 아래가 없습니다.
     */
    val speed: Double

    /**
     * 해당하는 카트의 가장 최근에 감지된 부스터 개수입니다.
     */
    val nitro: Int

    /**
     * @return 주어진 액션바에서 게이지의 값을 추출한 결과를 반환합니다. 게이지로 보이는 텍스트가 없으면 null을 반환합니다.
     */
    fun parseGauge(actionBar: Component): Double?

    /**
     * ## NOTE
     * 반환값이 [Double]인 이유는 RUSH+ 엔진의 속도계는 소수점 1자리까지 표시되기 때문입니다.
     * 기본적으로는 소수점 아래가 없습니다.
     *
     * @return 주어진 액션바에서 속도 값을 추출한 결과를 반환합니다. 속도계로 보이는 텍스트가 없으면 null을 반환합니다.
     */
    fun parseSpeed(actionBar: Component): Double?

    /**
     * @return 주어진 액션바에서 부스터 개수를 추출한 결과를 반환합니다. 니트로 개수가 확인되지 않으면 null을 반환합니다.
     */
    fun parseNitro(actionBar: Component): Int?
}

sealed interface GearlikeEngine : KartEngine {

    /**
     * 해당하는 카트의 가장 최근에 감지된 RPM 값입니다.
     * 게이지가 가득 찰 경우 1.0, 비어 있을 경우를 0.0으로 합니다.
     */
    val rpm: Double

    /**
     * 해당하는 카트의 가장 최근에 감지된 속도값입니다.
     *
     * ## NOTE
     * 반환값이 [Double]인 이유는 RUSH+ 엔진의 속도계는 소수점 1자리까지 표시되기 때문입니다.
     * 기본적으로는 소수점 아래가 없습니다.
     */
    val speed: Double

    /**
     * 해당하는 카트의 가장 최근에 감지된 기어 단수 값입니다.
     */
    val gear: Int

    /**
     * @return 주어진 액션바에서 기어 게이지가 채워진 정도를 추출한 결과를 반환합니다. 게이지로 보이는 텍스트가 없으면 null을 반환합니다.
     */
    fun parseRpm(actionBar: Component): Double?

    /**
     * ## NOTE
     * 반환값이 [Double]인 이유는 RUSH+ 엔진의 속도계는 소수점 1자리까지 표시되기 때문입니다.
     * 기본적으로는 소수점 아래가 없습니다.
     *
     * @return 주어진 액션바에서 속도 값을 추출한 결과를 반환합니다. 속도계로 보이는 텍스트가 없으면 null을 반환합니다.
     */
    fun parseSpeed(actionBar: Component): Double?

    /**
     * @return 주어진 액션바에서 기어 단수를 추출한 결과를 반환합니다. 기어 단 수를 확인할 수 없을 경우 null을 반환합니다.
     */
    fun parseGear(actionBar: Component): Int?
}
