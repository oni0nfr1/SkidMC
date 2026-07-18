package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.kart.Kart

/**
 * 카트의 엔진 종류에 따라 달라지는 주행 상태와 기능을 제공합니다.
 *
 * 엔진 객체는 카트의 메타데이터가 준비된 시점에 생성되며 카트가 유효한 동안 유지됩니다.
 * 탑승자와 타코미터는 엔진이 아닌 [Kart]의 현재 상태로 제공됩니다.
 */
interface KartEngine {

    /**
     * 이 엔진이 연결된 카트입니다.
     */
    val kart: Kart<*, *>

}
