package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.*
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.internal.engine.NitroEngineImpl
import net.minecraft.world.entity.player.Player

internal class XEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), XEngine {
    override val type = KartEngine.Type.X
}

internal class EXEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), EXEngine {
    override val type = KartEngine.Type.EX
}

internal class JiuEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), JiuEngine {
    override val type = KartEngine.Type.JIU
}

internal class NewEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), NewEngine {
    override val type = KartEngine.Type.NEW
}

internal class Z7EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), Z7Engine {
    override val type = KartEngine.Type.Z7
}

internal class V1EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), V1Engine {
    override val type = KartEngine.Type.V1
}

internal class A2EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), A2Engine {
    override val type = KartEngine.Type.A2
}

internal class LegacyEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), LegacyEngine {
    override val type = KartEngine.Type.LEGACY
}

internal class ProEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), ProEngine {
    override val type = KartEngine.Type.PRO
}

internal class RushPlusEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), RushPlusEngine {
    override val type = KartEngine.Type.RUSHPLUS
}

internal class ChargeEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), ChargeEngine {
    override val type = KartEngine.Type.CHARGE
}

internal class SREngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), SREngine {
    override val type = KartEngine.Type.SR
}

internal class N1EngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), N1Engine {
    override val type = KartEngine.Type.N1
}

internal class RXEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), RXEngine {
    override val type = KartEngine.Type.RX
}

internal class KeyEngineImpl(kart: Kart, rider: Player) : NitroEngineImpl(kart, rider), KeyEngine {
    override val type = KartEngine.Type.KEY
}
