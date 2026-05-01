package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.BoatEngine
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.engine.MKEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.BoatTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.MKTachometer
import io.github.oni0nfr1.skid.client.internal.engine.KartEngineImpl
import net.minecraft.world.entity.player.Player

internal class MKEngineImpl(
    kart: Kart,
    rider: Player,
) : KartEngineImpl(kart, rider), MKEngine {
    override val type = KartEngine.Type.MK
    override val tachometer: MKTachometer?
        get() = super.tachometer as? MKTachometer
}

internal class BoatEngineImpl(
    kart: Kart,
    rider: Player,
) : KartEngineImpl(kart, rider), BoatEngine {
    override val type = KartEngine.Type.BOAT
    override val tachometer: BoatTachometer?
        get() = super.tachometer as? BoatTachometer
}
