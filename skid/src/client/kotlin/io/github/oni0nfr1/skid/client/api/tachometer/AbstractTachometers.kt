package io.github.oni0nfr1.skid.client.api.tachometer

sealed interface RegularKartTachometer : KartTachometer {
    val speed: Double
}

sealed interface NitroTachometer : RegularKartTachometer {
    val gauge: Double
    val nitro: Int
}

sealed interface GearlikeTachometer : RegularKartTachometer {
    val rpm: Double
    val gear: Int
}
