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
     * - 저장된 두 view는 동일한 타코미터 구현체이며 생성 당시의 정확한 KartEngine
     *   인스턴스를 참조한다.
     *
     * THREADING:
     * - 렌더 스레드에서만 접근하고 변경한다.
     */
    private var currentTachometer: ManagedTachometer<*>? = null

    fun init() {
        currentTachometer = null
        ClientTickEvents.END_CLIENT_TICK.register {
            clearIfInvalid()
            currentTachometer?.internal?.tick()
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

        val current = currentTachometer?.internal
        val match: TachometerUpdateResult

        if (current != null && current.matches(kart, engine)) {
            match = current.update(actionBar)
        } else {
            clear()

            val tachometer = createTachometer(engine)
            match = tachometer.internal.update(actionBar)
            if (match.matched) currentTachometer = tachometer
        }

        var receiveResult = KartTachometerEvents.Result.SHOW
        if (match.matched) {
            receiveResult = KartTachometerEvents.RECEIVE.invoker()
                .onActionbarReceive(KartRef(kart.saddle), actionBar)
        }

        return KartTachometerEvents.Result.finalize(match.result, receiveResult)
    }

    private fun clearIfInvalid() {
        val current = currentTachometer?.internal ?: return
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
     * - null이 아닌 반환값은 현재 탑승·관전 대상의 alive 카트와 정확히 같은 [engine]
     *   인스턴스에 바인딩된 타코미터다.
     * - 현재 타코미터가 없거나 대상·카트·엔진 중 하나라도 다르면 `null`이다.
     */
    fun getForEngine(engine: KartEngine): KartTachometer? {
        val current = currentTachometer ?: return null
        val kart = currentSubjectKart() ?: return null
        return current.publicView.takeIf { current.internal.matches(kart, engine) }
    }

    private fun currentSubjectKart(): Kart<*>? {
        val subject = Minecraft.getInstance().player?.subject as? Player ?: return null
        return KartManager.getByRiderId(subject.id)
    }

    private fun createTachometer(engine: KartEngine): ManagedTachometer<*> {
        return when (engine.kart.type) {
            KartType.X -> ManagedTachometer(XTachometerImpl(engine))
            KartType.EX -> ManagedTachometer(EXTachometerImpl(engine))
            KartType.JIU -> ManagedTachometer(JiuTachometerImpl(engine))
            KartType.NEW -> ManagedTachometer(NewTachometerImpl(engine))
            KartType.Z7 -> ManagedTachometer(Z7TachometerImpl(engine))
            KartType.V1 -> ManagedTachometer(V1TachometerImpl(engine))
            KartType.A2 -> ManagedTachometer(A2TachometerImpl(engine))
            KartType.LEGACY -> ManagedTachometer(LegacyTachometerImpl(engine))
            KartType.PRO -> ManagedTachometer(ProTachometerImpl(engine))
            KartType.RUSHPLUS -> ManagedTachometer(RushPlusTachometerImpl(engine))
            KartType.CHARGE -> ManagedTachometer(ChargeTachometerImpl(engine))
            KartType.SR -> ManagedTachometer(SRTachometerImpl(engine))
            KartType.N1 -> ManagedTachometer(N1TachometerImpl(engine))
            KartType.RX -> ManagedTachometer(RXTachometerImpl(engine))
            KartType.KEY -> ManagedTachometer(KeyTachometerImpl(engine))
            KartType.GEAR -> ManagedTachometer(GearTachometerImpl(engine))
            KartType.F1 -> ManagedTachometer(F1TachometerImpl(engine))
            KartType.RALLY -> ManagedTachometer(RallyTachometerImpl(engine))
            KartType.MK -> ManagedTachometer(MKTachometerImpl(engine))
            KartType.DS -> ManagedTachometer(DSTachometerImpl(engine))
            KartType.BOAT -> ManagedTachometer(BoatTachometerImpl(engine))
        }
    }

    /** 하나의 타코미터 구현체를 내부 갱신 계약과 공개 API 계약으로 함께 보관합니다. */
    private class ManagedTachometer<T>(
        private val value: T,
    )
        where
            T : TachometerInternal,
            T : KartTachometer
    {
        val internal: TachometerInternal
            get() = value

        val publicView: KartTachometer
            get() = value
    }
}
