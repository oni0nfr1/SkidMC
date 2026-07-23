package io.github.oni0nfr1.skid.client.internal.engine

import io.github.oni0nfr1.skid.client.api.attr.getKartInfo
import io.github.oni0nfr1.skid.client.api.attr.unstable.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.internal.kart.KartImpl
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.minecraft.world.entity.Display

internal abstract class KartEngineImpl<ENGINE, TACHOMETER>(
    val kart: Kart<ENGINE>,
)
    where
        ENGINE : KartEngine,
        TACHOMETER : KartTachometer
{
    /**
     * INVARIANT:
     * - 모든 구체 하위 클래스는 [ENGINE]에 대응하는 공개 엔진 인터페이스를 구현하므로
     *   이 객체와 공개 엔진 view는 항상 동일한 인스턴스다.
     */
    private val engineView: KartEngine
        get() {
            check(this is KartEngine) {
                "Concrete kart engine implementations must implement their public engine interface"
            }
            return this
        }

    /**
     * INVARIANT:
     * - [TachometerManager]는 [engineView]와 동일한 엔진 인스턴스로 생성된 타코미터만
     *   반환하며, 구체 엔진 factory는 [ENGINE]과 [TACHOMETER]의 올바른 조합만 생성한다.
     */
    val tachometer: TACHOMETER?
        get() {
            @Suppress("UNCHECKED_CAST")
            return TachometerManager.getForEngine(engineView) as? TACHOMETER
        }

    // implementation of RegularEngine
    val isDrifting: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.STATE_DRIFTING) == 1.0
    val accurateDriftState: Boolean
        get() {
            val internalKart = kart as? KartImpl<*> ?: return false
            return internalKart.internalModelOrNull?.passengers?.flatMap { it.passengers }?.any {
                it.customName?.string == "mcrider-drift-effect" && it is Display && it.viewRange > 0
            } ?: false
        }

    // implementation of NitroEngine
    val isBoosting: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.STATE_NITRO) != 0.0
    val maxBoost: Int
        get() = kart.saddle.getKartInfo(KnownAttrModId.CAP_NITRO_COUNT)?.toInt() ?: 0

    // implementation of InstantBoostEngine
    val instantBoostReady: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.STATE_IBOOST) != 0.0
    val instantBoostEnabled: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.CAN_IBOOST) != 0.0

    // implementation of DualBoostEngine
    val dualBoostActive: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.STATE_NITRO) == 3.0
    val dualBoostCharging: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.STATE_NITRO) == 2.0

    // implementation of DraftEngine
    val draftActive: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.STATE_DRAFT_ACCEL) == 2.0
    val draftCharging: Boolean
        get() = kart.saddle.getKartInfo(KnownAttrModId.STATE_DRAFT_ACCEL) == 1.0

}
