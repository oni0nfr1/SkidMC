package io.github.oni0nfr1.skid.client.internal.engine

import io.github.oni0nfr1.skid.client.api.kart.Kart
import net.minecraft.world.entity.player.Player

internal abstract class KartEngineImpl(
    val kart: Kart,
    val rider: Player,
)
