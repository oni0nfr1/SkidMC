package io.github.oni0nfr1.skid.client.api.events

@Suppress("unused")
private fun summonEventsCanBeRegistered() {
    KartSummonEvents.SUMMON_EARLY.register { kartEntity ->
        kartEntity.id
    }
    KartSummonEvents.SUMMON.register { kart ->
        kart.saddleId
    }
    KartSummonEvents.REMOVE.register { kartEntity ->
        kartEntity.id
    }
}
