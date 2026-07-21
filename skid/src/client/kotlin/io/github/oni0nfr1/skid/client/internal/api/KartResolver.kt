package io.github.oni0nfr1.skid.client.internal.api

import io.github.oni0nfr1.skid.client.api.kart.Kart
import java.util.Optional
import java.util.UUID

internal data class TrackedKart(
    val kart: Kart<*>,
    val saddleId: Int,
    val saddleUuid: UUID,
)

internal fun interface TrackedKartLookup {
    fun getBySaddleId(saddleId: Int): TrackedKart?
}

/** ID 기반 조회 결과가 요청한 엔티티 identity와 같은 카트인지 검증합니다. */
internal class KartResolver(
    private val lookup: TrackedKartLookup,
) {
    fun resolve(saddleId: Int, saddleUuid: UUID): Optional<Kart<*>> {
        val tracked = lookup.getBySaddleId(saddleId) ?: return Optional.empty()
        if (tracked.saddleId != saddleId || tracked.saddleUuid != saddleUuid) {
            return Optional.empty()
        }
        return Optional.of(tracked.kart)
    }
}
