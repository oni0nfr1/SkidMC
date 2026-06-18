package io.github.oni0nfr1.skid.client.internal.engine

import io.github.oni0nfr1.skid.client.api.attr.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.attr.getRiderMeta
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.subject
import io.github.oni0nfr1.skid.client.api.tachometer.KartTachometer
import io.github.oni0nfr1.skid.client.internal.kart.KartImpl
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.player.Player

internal abstract class KartEngineImpl(
    val kart: Kart,
    val rider: Player,
) {
    // implementation of KartEngine
    val currentLap: Int
        get() = rider.getRiderMeta(KnownAttrModId.CURRENT_LAP)?.toInt() ?: 0
    open val tachometer: KartTachometer?
        get() {
            val client = Minecraft.getInstance()
            return if (rider == client.player?.subject) TachometerManager.currentTachometerOrNull else null
        }

    // implementation of RegularEngine
    val isDrifting: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.IS_DRIFTING) == 1.0
    val accurateDriftState: Boolean
        get() {
            val internalKart = kart as? KartImpl ?: return false
            return internalKart.internalModelOrNull?.passengers?.flatMap { it.passengers }?.any {
                it.customName?.string == "mcrider-drift-effect" && it is Display && it.viewRange > 0
            } ?: false
        }

    // implementation of NitroEngine
    val isBoosting: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.BOOST_STATE) != 0.0
    val maxBoost: Int
        get() = rider.getRiderMeta(KnownAttrModId.KART_MAX_BOOST_COUNT)?.toInt() ?: 0

    // implementation of InstantBoostEngine
    val instantBoostReady: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.ACTIVE_INSTANT_BOOST) != 0.0
    val instantBoostEnabled: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.FORCE_INSTANT_BOOST) != 0.0

    // implementation of DualBoostEngine
    val dualBoostActive: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.BOOST_STATE) == 3.0
    val dualBoostCharging: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.BOOST_STATE) == 2.0

    // implementation of DraftEngine
    val draftActive: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.DRAFT_STATE) == 2.0
    val draftCharging: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.DRAFT_STATE) == 1.0

}
