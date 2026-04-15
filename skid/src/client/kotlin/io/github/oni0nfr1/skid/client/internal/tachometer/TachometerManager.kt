package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartManager
import io.github.oni0nfr1.skid.client.api.kart.subject
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.NoTachometerException
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
        }
    }

    val currentTachometerOrNull: KartTachometer?
        get() = _currentTachometer

    val currentTachometer: KartTachometer
        get() = currentTachometerOrNull ?: throw NoTachometerException()

    fun clear() {
        _currentTachometer = null
    }

    fun handleActionbar(kart: Kart, engine: KartEngine, actionBar: Component): KartTachometerEvents.Result {
        val current = _currentTachometer as? TachometerInternal
        val match: TachometerUpdateResult

        if (current != null && current.matches(kart, engine)) {
            match =  current.update(actionBar)
        } else {
            clear()

            val tachometer = createTachometer(kart, engine)
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
        val kart = KartManager.getKartHandle(subject) ?: run {
            clear()
            return
        }
        val engine = kart.engine ?: run {
            clear()
            return
        }

        if (!current.matches(kart, engine)) {
            clear()
        }
    }

    private fun TachometerInternal.matches(kart: Kart, engine: KartEngine): Boolean {
        return kart.entity.id == kartId && engine.type == type
    }

    private fun createTachometer(kart: Kart, engine: KartEngine): KartTachometer {
        val kartId = kart.entity.id
        val revision = nextRevision++

        return when (engine.type) {
            KartEngine.Type.X -> XTachometerImpl(revision, kartId)
            KartEngine.Type.EX -> EXTachometerImpl(revision, kartId)
            KartEngine.Type.JIU -> JiuTachometerImpl(revision, kartId)
            KartEngine.Type.NEW -> NewTachometerImpl(revision, kartId)
            KartEngine.Type.Z7 -> Z7TachometerImpl(revision, kartId)
            KartEngine.Type.V1 -> V1TachometerImpl(revision, kartId)
            KartEngine.Type.A2 -> A2TachometerImpl(revision, kartId)
            KartEngine.Type.LEGACY -> LegacyTachometerImpl(revision, kartId)
            KartEngine.Type.PRO -> ProTachometerImpl(revision, kartId)
            KartEngine.Type.RUSHPLUS -> RushPlusTachometerImpl(revision, kartId)
            KartEngine.Type.CHARGE -> ChargeTachometerImpl(revision, kartId)
            KartEngine.Type.N1 -> N1TachometerImpl(revision, kartId)
            KartEngine.Type.KEY -> KeyTachometerImpl(revision, kartId)
            KartEngine.Type.GEAR -> GearTachometerImpl(revision, kartId)
            KartEngine.Type.F1 -> F1TachometerImpl(revision, kartId)
            KartEngine.Type.RALLY -> RallyTachometerImpl(revision, kartId)
            KartEngine.Type.MK -> MKTachometerImpl(revision, kartId)
            KartEngine.Type.BOAT -> BoatTachometerImpl(revision, kartId)
        }
    }
}
