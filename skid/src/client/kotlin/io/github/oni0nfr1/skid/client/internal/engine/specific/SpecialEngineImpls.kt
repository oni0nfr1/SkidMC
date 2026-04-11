package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.BoatEngine
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.engine.MKEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import net.minecraft.world.entity.player.Player

internal class MKEngineImpl(
    override val kart: Kart,
    override val rider: Player,
) : MKEngine {
    override val type = KartEngine.Type.MK
}

internal class BoatEngineImpl(
    override val kart: Kart,
    override val rider: Player,
) : BoatEngine {
    override val type = KartEngine.Type.BOAT
}
