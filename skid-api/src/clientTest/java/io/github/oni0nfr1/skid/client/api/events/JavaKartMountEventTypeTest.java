package io.github.oni0nfr1.skid.client.api.events;

final class JavaKartMountEventTypeTest {
    private JavaKartMountEventTypeTest() {
    }

    static void registerMountAndSpectateEvents() {
        KartMountEvents.MOUNT_EARLY.register((kart, rider) -> { });
        KartMountEvents.MOUNT.register((kart, rider) -> { });
        KartMountEvents.DISMOUNT.register((kart, rider) -> { });
        KartMountEvents.SPECTATE_EARLY.register((kart, rider, target) -> { });
        KartMountEvents.SPECTATE.register((kart, rider, target) -> { });
        KartMountEvents.SPECTATE_END.register((kart, rider, target) -> { });
    }
}
