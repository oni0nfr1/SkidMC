package io.github.oni0nfr1.skid.client.internal.engine

import io.github.oni0nfr1.skid.client.api.attr.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.attr.getRiderMeta
import io.github.oni0nfr1.skid.client.api.kart.Kart
import net.minecraft.world.entity.player.Player

internal abstract class RegularEngineImpl(
    kart: Kart,
    rider: Player,
) : KartEngineImpl(kart, rider) {
    val isDrifting: Boolean
        get() = rider.getRiderMeta(KnownAttrModId.IS_DRIFTING) == 1.0
}