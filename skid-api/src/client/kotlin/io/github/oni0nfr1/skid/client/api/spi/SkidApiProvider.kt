package io.github.oni0nfr1.skid.client.api.spi

import io.github.oni0nfr1.skid.client.api.kart.Kart
import java.util.Optional
import java.util.UUID

/**
 * SkidMC API와 구현체를 연결하는 Fabric entrypoint 계약입니다.
 *
 * 이 인터페이스는 SkidMC 구현체가 사용하기 위한 SPI입니다. API 소비자가 직접 구현하거나
 * 호출하는 용도가 아닙니다.
 */
interface SkidApiProvider {

    /**
     * [saddleId]와 [saddleUuid]에 대응하는 현재 유효한 카트를 반환합니다.
     *
     * 카트가 제거되었거나 아직 API 객체가 준비되지 않았거나, 같은 ID를 다른 엔티티가
     * 재사용하고 있으면 빈 [Optional]을 반환합니다.
     */
    fun getKart(saddleId: Int, saddleUuid: UUID): Optional<Kart<*>>

    /**
     * [riderId]에 대응하는 현재 유효한 카트를 반환합니다.
     *
     * 플레이어가 카트에 탑승하지 않았거나 카트 API 객체가 아직 준비되지 않았으면 빈
     * [Optional]을 반환합니다.
     */
    fun getKartByRiderId(riderId: Int): Optional<Kart<*>>
}
