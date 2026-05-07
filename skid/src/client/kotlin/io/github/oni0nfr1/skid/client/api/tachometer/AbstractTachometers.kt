package io.github.oni0nfr1.skid.client.api.tachometer

sealed interface SpeedTachometer : KartTachometer {
    val speed: Double
}

sealed interface NitroTachometer : SpeedTachometer {
    val gauge: Double
    val nitro: Int
}

sealed interface GearlikeTachometer : SpeedTachometer {
    val rpm: Double
    val gear: Int
}

sealed interface ExceedTachometer : KartTachometer {
    val exceedGauge: Float
}