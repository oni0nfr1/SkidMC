package io.github.oni0nfr1.skid.client.api.events;

final class JavaKartTachometerEventTypeTest {
    private JavaKartTachometerEventTypeTest() {
    }

    static void registerTachometerEvents() {
        KartTachometerEvents.RECEIVE.register((kart, text) -> KartTachometerEvents.Result.SHOW);
        KartTachometerEvents.SPEED.register((kart, speed) -> KartTachometerEvents.Result.SHOW);
        KartTachometerEvents.NITRO.register((kart, nitro) -> KartTachometerEvents.Result.SHOW);
        KartTachometerEvents.GAUGE.register((kart, gauge) -> KartTachometerEvents.Result.SHOW);
        KartTachometerEvents.RPM.register((kart, rpm) -> KartTachometerEvents.Result.SHOW);
        KartTachometerEvents.GEAR.register((kart, gear) -> KartTachometerEvents.Result.SHOW);
        KartTachometerEvents.ERS.register((kart, ers) -> KartTachometerEvents.Result.SHOW);
        KartTachometerEvents.TURBO_GAUGE.register((kart, gauge) -> KartTachometerEvents.Result.SHOW);
    }
}
