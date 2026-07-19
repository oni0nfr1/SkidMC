package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.subject
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.tachometer.specific.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

internal object TachometerManager {
    private var _currentTachometer : KartTachometer? = null
    private var nextRevision = 1L

    fun init() {
        _currentTachometer = null
        ClientTickEvents.END_CLIENT_TICK.register {
            clearIfInvalid()
            val current = _currentTachometer as? TachometerInternal
            current?.tick()
        }
    }

    val currentTachometerOrNull: KartTachometer?
        get() = _currentTachometer

    fun clear() {
        _currentTachometer = null
    }

    fun handleActionbar(
        kart: Kart<*, *>,
        engine: KartEngine,
        actionBar: Component,
    ): KartTachometerEvents.Result {
        val current = _currentTachometer as? TachometerInternal
        val match: TachometerUpdateResult

        if (current != null && current.matches(kart, engine)) {
            match =  current.update(actionBar)
        } else {
            clear()

            val tachometer = createTachometer(kart)
            match = (tachometer as TachometerInternal).update(actionBar)
            if (match.matched) _currentTachometer = tachometer
        }

        var receiveResult = KartTachometerEvents.Result.SHOW
        if (match.matched) receiveResult = KartTachometerEvents.RECEIVE.invoker().onActionbarReceive(kart, engine, actionBar)

        return KartTachometerEvents.Result.finalize(match.result, receiveResult)
    }

    private fun clearIfInvalid() {
        val current = _currentTachometer as? TachometerInternal ?: return
        val subject = Minecraft.getInstance().player?.subject as? Player ?: run {
            clear()
            return
        }
        val kart = KartManager.getByRiderId(subject.id) ?: run {
            clear()
            return
        }
        val engine = kart.engine

        if (!current.matches(kart, engine)) {
            clear()
        }
    }

    private fun TachometerInternal.matches(kart: Kart<*, *>, engine: KartEngine): Boolean {
        return kart.saddle.id == kartId && kart.type === type && kart.engine === engine
    }

    fun getForKart(kartId: Int, type: KartType<*, *>): KartTachometer? {
        val current = _currentTachometer as? TachometerInternal ?: return null
        return current.takeIf { it.kartId == kartId && it.type === type }
    }

    private fun createTachometer(kart: Kart<*, *>): KartTachometer {
        val kartId = kart.saddle.id
        val revision = nextRevision++

        return when (kart.type) {
            KartType.X -> XTachometerImpl(revision, kartId)
            KartType.EX -> EXTachometerImpl(revision, kartId)
            KartType.JIU -> JiuTachometerImpl(revision, kartId)
            KartType.NEW -> NewTachometerImpl(revision, kartId)
            KartType.Z7 -> Z7TachometerImpl(revision, kartId)
            KartType.V1 -> V1TachometerImpl(revision, kartId)
            KartType.A2 -> A2TachometerImpl(revision, kartId)
            KartType.LEGACY -> LegacyTachometerImpl(revision, kartId)
            KartType.PRO -> ProTachometerImpl(revision, kartId)
            KartType.RUSHPLUS -> RushPlusTachometerImpl(revision, kartId)
            KartType.CHARGE -> ChargeTachometerImpl(revision, kartId)
            KartType.SR -> SRTachometerImpl(revision, kartId)
            KartType.N1 -> N1TachometerImpl(revision, kartId)
            KartType.RX -> RXTachometerImpl(revision, kartId)
            KartType.KEY -> KeyTachometerImpl(revision, kartId)
            KartType.GEAR -> GearTachometerImpl(revision, kartId)
            KartType.F1 -> F1TachometerImpl(revision, kartId)
            KartType.RALLY -> RallyTachometerImpl(revision, kartId)
            KartType.MK -> MKTachometerImpl(revision, kartId)
            KartType.BOAT -> BoatTachometerImpl(revision, kartId)
        }
    }
}
