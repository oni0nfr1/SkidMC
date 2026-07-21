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
    override val kart: Kart<ENGINE>,
) : KartEngine
    where
        ENGINE : KartEngine,
        TACHOMETER : KartTachometer
{
    override val tachometer: TACHOMETER?
        get() {
            @Suppress("UNCHECKED_CAST")
            return TachometerManager.getForEngine(this) as? TACHOMETER
        }

    val currentLap: Int
        get() = kart.saddle.getKartInfo(KnownAttrModId.CTX_CURRENT_LAP)?.toInt() ?: 0

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
