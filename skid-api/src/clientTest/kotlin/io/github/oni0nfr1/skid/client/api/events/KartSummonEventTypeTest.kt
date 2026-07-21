package io.github.oni0nfr1.skid.client.api.events

@Suppress("unused")
private fun summonEventsCanBeRegistered() {
    KartSummonEvents.SUMMON.register { _ -> }
    KartSummonEvents.REMOVE.register { _ -> }
}
