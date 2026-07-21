package io.github.oni0nfr1.skid.client.api.events

@Suppress("unused")
private fun mountAndSpectateEventsCanBeRegistered() {
    KartMountEvents.MOUNT_EARLY.register { _, _ -> }
    KartMountEvents.MOUNT.register { _, _ -> }
    KartMountEvents.DISMOUNT.register { _, _ -> }
    KartMountEvents.SPECTATE_EARLY.register { _, _, _ -> }
    KartMountEvents.SPECTATE.register { _, _, _ -> }
    KartMountEvents.SPECTATE_END.register { _, _, _ -> }
}
