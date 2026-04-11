package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.F1Engine
import io.github.oni0nfr1.skid.client.api.engine.GearEngine
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.engine.RallyEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.engine.GearlikeEngineImpl
import net.minecraft.world.entity.player.Player

internal class GearEngineImpl(kart: Kart, rider: Player) : GearlikeEngineImpl(kart, rider), GearEngine {
    override val type = KartEngine.Type.GEAR
}

internal class F1EngineImpl(kart: Kart, rider: Player) : GearlikeEngineImpl(kart, rider), F1Engine {
    override val type = KartEngine.Type.F1
}

internal class RallyEngineImpl(kart: Kart, rider: Player) : GearlikeEngineImpl(kart, rider), RallyEngine {
    override val type = KartEngine.Type.RALLY
}
