package io.github.oni0nfr1.skid.client.internal.api

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.spi.SkidApiProvider
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import java.util.Optional
import java.util.UUID

class SkidApiProviderImpl internal constructor(
    private val kartResolver: KartResolver,
) : SkidApiProvider {

    constructor() : this(
        KartResolver { saddleId ->
            KartManager.getBySaddleId(saddleId)?.let { kart ->
                TrackedKart(kart, kart.saddleId, kart.saddleUuid)
            }
        },
    )

    override fun getKart(saddleId: Int, saddleUuid: UUID): Optional<Kart<*>> =
        kartResolver.resolve(saddleId, saddleUuid)

    override fun getKartByRiderId(riderId: Int): Optional<Kart<*>> =
        Optional.ofNullable(KartManager.getByRiderId(riderId))
}
