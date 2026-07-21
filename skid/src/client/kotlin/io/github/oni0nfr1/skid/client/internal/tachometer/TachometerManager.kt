package io.github.oni0nfr1.skid.client.internal.tachometer

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartRef
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

    /**
     * 마지막으로 현재 탑승·관전 대상에서 정상 파싱된 타코미터입니다.
     *
     * INVARIANT:
     * - 저장된 타코미터는 생성 당시의 정확한 KartEngine 인스턴스를 참조한다.
     *
     * THREADING:
     * - 렌더 스레드에서만 접근하고 변경한다.
     */
    private var currentTachometer: TachometerInternal? = null

    fun init() {
        currentTachometer = null
        ClientTickEvents.END_CLIENT_TICK.register {
            clearIfInvalid()
            currentTachometer?.tick()
        }
    }

    fun clear() {
        currentTachometer = null
    }

    /**
     * REQUIRES:
     * - [kart]는 alive이며 [engine]을 현재 엔진으로 소유하고, [engine]도 [kart]를 참조한다.
     */
    fun handleActionbar(
        kart: Kart<*>,
        engine: KartEngine,
        actionBar: Component,
    ): KartTachometerEvents.Result {
        check(kart.alive && kart.engine === engine && engine.kart === kart) {
            "Cannot update a tachometer with a stale or mismatched kart engine"
        }

        val current = currentTachometer
        val match: TachometerUpdateResult

        if (current != null && current.matches(kart, engine)) {
            match = current.update(actionBar)
        } else {
            clear()

            val tachometer = createTachometer(engine)
            match = tachometer.update(actionBar)
            if (match.matched) currentTachometer = tachometer
        }

        var receiveResult = KartTachometerEvents.Result.SHOW
        if (match.matched) {
            receiveResult = KartTachometerEvents.RECEIVE.invoker()
                .onActionbarReceive(KartRef(kart.saddle.id), actionBar)
        }

        return KartTachometerEvents.Result.finalize(match.result, receiveResult)
    }

    private fun clearIfInvalid() {
        val current = currentTachometer ?: return
        val kart = currentSubjectKart() ?: run {
            clear()
            return
        }
        val engine = kart.engine

        if (!current.matches(kart, engine)) {
            clear()
        }
    }

    private fun TachometerInternal.matches(kart: Kart<*>, engine: KartEngine): Boolean {
        return kart.alive && this.engine === engine && engine.kart === kart && kart.engine === engine
    }

    /**
     * ENSURES:
     * - [engine]이 현재 탑승·관전 대상의 정확한 alive 엔진이면 그 엔진에 바인딩된 타코미터를 반환한다.
     * - 현재 타코미터가 없거나 대상·카트·엔진 중 하나라도 달라졌으면 `null`을 반환한다.
     */
    fun getForEngine(engine: KartEngine): KartTachometer? {
        val current = currentTachometer ?: return null
        val kart = currentSubjectKart() ?: return null
        return current.takeIf { it.matches(kart, engine) }
    }

    private fun currentSubjectKart(): Kart<*>? {
        val subject = Minecraft.getInstance().player?.subject as? Player ?: return null
        return KartManager.getByRiderId(subject.id)
    }

    private fun createTachometer(engine: KartEngine): TachometerInternal {
        return when (engine.kart.type) {
            KartType.X -> XTachometerImpl(engine)
            KartType.EX -> EXTachometerImpl(engine)
            KartType.JIU -> JiuTachometerImpl(engine)
            KartType.NEW -> NewTachometerImpl(engine)
            KartType.Z7 -> Z7TachometerImpl(engine)
            KartType.V1 -> V1TachometerImpl(engine)
            KartType.A2 -> A2TachometerImpl(engine)
            KartType.LEGACY -> LegacyTachometerImpl(engine)
            KartType.PRO -> ProTachometerImpl(engine)
            KartType.RUSHPLUS -> RushPlusTachometerImpl(engine)
            KartType.CHARGE -> ChargeTachometerImpl(engine)
            KartType.SR -> SRTachometerImpl(engine)
            KartType.N1 -> N1TachometerImpl(engine)
            KartType.RX -> RXTachometerImpl(engine)
            KartType.KEY -> KeyTachometerImpl(engine)
            KartType.GEAR -> GearTachometerImpl(engine)
            KartType.F1 -> F1TachometerImpl(engine)
            KartType.RALLY -> RallyTachometerImpl(engine)
            KartType.MK -> MKTachometerImpl(engine)
            KartType.BOAT -> BoatTachometerImpl(engine)
        }
    }
}
