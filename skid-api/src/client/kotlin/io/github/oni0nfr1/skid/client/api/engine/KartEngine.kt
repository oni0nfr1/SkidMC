package io.github.oni0nfr1.skid.client.api.engine

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer

/**
 * 카트의 엔진 종류에 따라 달라지는 주행 상태와 기능을 제공하는 typed view입니다.
 *
 * 엔진 객체는 카트의 메타데이터가 준비된 시점에 생성되며 카트가 유효한 동안 유지됩니다.
 * 이 객체의 프로퍼티는 독립적인 엔진 소유 상태가 아니라 연결된 [Kart]의 상태 중 엔진
 * 종류에 따라 달라지는 값을 나타냅니다.
 *
 * 직접 하위 타입 계층은 SkidMC가 정의합니다. API 소비자는 이 인터페이스를 직접
 * 구현하지 않고 [Kart.engine]으로 구현체 모드가 제공하는 typed view를 사용합니다.
 */
sealed interface KartEngine {

    /**
     * 이 엔진이 연결된 카트입니다.
     */
    val kart: Kart<*>

    /**
     * 클라이언트 화면에서 현재 이 엔진에 관해 읽을 수 있는 주행 정보입니다.
     *
     * 구체 엔진 인터페이스는 엔진 종류에 대응하는 타코미터 타입으로 이 프로퍼티를
     * 좁힙니다. 현재 화면이 해당 카트의 정보를 제공하지 않으면 `null`입니다.
     */
    val tachometer: KartTachometer?

}
