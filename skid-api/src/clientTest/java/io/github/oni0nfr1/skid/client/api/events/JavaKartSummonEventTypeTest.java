package io.github.oni0nfr1.skid.client.api.events;

final class JavaKartSummonEventTypeTest {
    private JavaKartSummonEventTypeTest() {
    }

    static void registerSummonEvents() {
        KartSummonEvents.SUMMON.register(kart -> { });
        KartSummonEvents.REMOVE.register(kart -> { });
    }
}
