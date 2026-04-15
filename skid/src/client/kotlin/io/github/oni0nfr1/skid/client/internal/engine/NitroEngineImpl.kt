package io.github.oni0nfr1.skid.client.internal.engine

import io.github.oni0nfr1.skid.client.api.attr.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.attr.getRiderMeta
import io.github.oni0nfr1.skid.client.api.kart.Kart
import net.minecraft.world.entity.player.Player

internal abstract class NitroEngineImpl(
    kart: Kart,
    rider: Player,
) : RegularEngineImpl(kart, rider) {

    val isBoosting: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.BOOST_STATE) != 0.0
    val maxBoost: Int
        get() = rider.getRiderMeta(KnownAttrModId.KART_MAX_BOOST_COUNT)?.toInt() ?: 0
    val instantBoostReady: Boolean
        get() = (rider.getRiderMeta(KnownAttrModId.ACTIVE_INSTANT_BOOST) ?: 0.0) != 0.0
}
