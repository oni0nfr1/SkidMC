package io.github.oni0nfr1.skid.client.api.events;

final class JavaKartSummonEventTypeTest {
    private JavaKartSummonEventTypeTest() {
    }

    static void registerSummonEvents() {
        KartSummonEvents.SUMMON_EARLY.register(kartEntity -> {
            kartEntity.getId();
        });
        KartSummonEvents.SUMMON.register(kart -> {
            kart.getSaddleId();
        });
        KartSummonEvents.REMOVE.register(kartEntity -> {
            kartEntity.getId();
        });
    }
}
