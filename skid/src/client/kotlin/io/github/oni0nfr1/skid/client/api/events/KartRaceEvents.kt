package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.race.KartRace
import io.github.oni0nfr1.skid.client.api.race.LapTime
import io.github.oni0nfr1.skid.client.api.race.RaceRanking
import io.github.oni0nfr1.skid.client.internal.utils.createEvent

object KartRaceEvents {

    @JvmField
    val START = createEvent { listeners ->
        KartRaceStartCallback { race, startNanos ->
            listeners.forEach { it.onKartRaceStart(race, startNanos) }
        }
    }

    @JvmField
    val END = createEvent { listeners ->
        KartRaceEndCallback { race, endNanos ->
            listeners.forEach { it.onKartRaceEnd(race, endNanos) }
        }
    }

    @JvmField
    val LAP = createEvent { listeners ->
        KartRaceLapFinishCallback { race, lapCount, maxLap, lapTime, lapNanos ->
            listeners.forEach { it.onLapFinish(race, lapCount, maxLap, lapTime, lapNanos) }
        }
    }

    @JvmField
    val RANKING = createEvent { listeners ->
        KartRaceRankingUpdateCallback { race, ranking ->
            listeners.forEach { it.onRankingUpdate(race, ranking) }
        }
    }

    fun interface KartRaceStartCallback {
        fun onKartRaceStart(race: KartRace, startNanos: Long)
    }

    fun interface KartRaceEndCallback {
        fun onKartRaceEnd(race: KartRace, endNanos: Long)
    }

    fun interface KartRaceLapFinishCallback {
        fun onLapFinish(race: KartRace, lapCount: Int, maxLap: Int, lapTime: LapTime, lapNanos: Long)
    }

    fun interface KartRaceRankingUpdateCallback {
        fun onRankingUpdate(race: KartRace, ranking: RaceRanking)
    }
}