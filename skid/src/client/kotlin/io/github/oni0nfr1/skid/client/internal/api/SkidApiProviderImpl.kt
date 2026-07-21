package io.github.oni0nfr1.skid.client.internal.api

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.spi.SkidApiProvider
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import java.util.Optional

class SkidApiProviderImpl : SkidApiProvider {
    override fun getKart(saddleId: Int): Optional<Kart<*>> =
        Optional.ofNullable(KartManager.getBySaddleId(saddleId))

    override fun getKartByRiderId(riderId: Int): Optional<Kart<*>> =
        Optional.ofNullable(KartManager.getByRiderId(riderId))
}
