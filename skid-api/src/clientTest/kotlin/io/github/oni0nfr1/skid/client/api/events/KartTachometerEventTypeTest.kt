package io.github.oni0nfr1.skid.client.api.events

@Suppress("unused")
private fun tachometerEventsCanBeRegistered() {
    KartTachometerEvents.RECEIVE.register { _, _ -> KartTachometerEvents.Result.SHOW }
    KartTachometerEvents.SPEED.register { _, _ -> KartTachometerEvents.Result.SHOW }
    KartTachometerEvents.NITRO.register { _, _ -> KartTachometerEvents.Result.SHOW }
    KartTachometerEvents.GAUGE.register { _, _ -> KartTachometerEvents.Result.SHOW }
    KartTachometerEvents.RPM.register { _, _ -> KartTachometerEvents.Result.SHOW }
    KartTachometerEvents.GEAR.register { _, _ -> KartTachometerEvents.Result.SHOW }
    KartTachometerEvents.ERS.register { _, _ -> KartTachometerEvents.Result.SHOW }
    KartTachometerEvents.MK_GAUGE.register { _, _ -> KartTachometerEvents.Result.SHOW }
}
