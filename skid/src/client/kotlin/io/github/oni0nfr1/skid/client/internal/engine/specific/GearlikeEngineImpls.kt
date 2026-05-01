package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.F1Engine
import io.github.oni0nfr1.skid.client.api.engine.GearEngine
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.engine.RallyEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.F1Tachometer
import io.github.oni0nfr1.skid.client.api.tachometer.GearTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.RallyTachometer
import io.github.oni0nfr1.skid.client.internal.engine.KartEngineImpl
import net.minecraft.world.entity.player.Player

internal class GearEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), GearEngine {
    override val type = KartEngine.Type.GEAR
    override val tachometer: GearTachometer?
        get() = super.tachometer as? GearTachometer
}

internal class F1EngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), F1Engine {
    override val type = KartEngine.Type.F1
    override val tachometer: F1Tachometer?
        get() = super.tachometer as? F1Tachometer
}

internal class RallyEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), RallyEngine {
    override val type = KartEngine.Type.RALLY
    override val tachometer: RallyTachometer?
        get() = super.tachometer as? RallyTachometer
}
