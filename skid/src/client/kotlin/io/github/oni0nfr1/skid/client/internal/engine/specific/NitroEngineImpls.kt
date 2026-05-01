package io.github.oni0nfr1.skid.client.internal.engine.specific

import io.github.oni0nfr1.skid.client.api.engine.*
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.tachometer.*
import io.github.oni0nfr1.skid.client.internal.engine.KartEngineImpl
import net.minecraft.world.entity.player.Player

internal class XEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), XEngine {
    override val type = KartEngine.Type.X
    override val tachometer: XTachometer?
        get() = super.tachometer as? XTachometer
}

internal class EXEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), EXEngine {
    override val type = KartEngine.Type.EX
    override val tachometer: EXTachometer?
        get() = super.tachometer as? EXTachometer
}

internal class JiuEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), JiuEngine {
    override val type = KartEngine.Type.JIU
    override val tachometer: JiuTachometer?
        get() = super.tachometer as? JiuTachometer
}

internal class NewEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), NewEngine {
    override val type = KartEngine.Type.NEW
    override val tachometer: NewTachometer?
        get() = super.tachometer as? NewTachometer
}

internal class Z7EngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), Z7Engine {
    override val type = KartEngine.Type.Z7
    override val tachometer: Z7Tachometer?
        get() = super.tachometer as? Z7Tachometer
}

internal class V1EngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), V1Engine {
    override val type = KartEngine.Type.V1
    override val tachometer: V1Tachometer?
        get() = super.tachometer as? V1Tachometer
}

internal class A2EngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), A2Engine {
    override val type = KartEngine.Type.A2
    override val tachometer: A2Tachometer?
        get() = super.tachometer as? A2Tachometer
}

internal class LegacyEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), LegacyEngine {
    override val type = KartEngine.Type.LEGACY
    override val tachometer: LegacyTachometer?
        get() = super.tachometer as? LegacyTachometer
}

internal class ProEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), ProEngine {
    override val type = KartEngine.Type.PRO
    override val tachometer: ProTachometer?
        get() = super.tachometer as? ProTachometer
}

internal class RushPlusEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), RushPlusEngine {
    override val type = KartEngine.Type.RUSHPLUS
    override val tachometer: RushPlusTachometer?
        get() = super.tachometer as? RushPlusTachometer
}

internal class ChargeEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), ChargeEngine {
    override val type = KartEngine.Type.CHARGE
    override val tachometer: ChargeTachometer?
        get() = super.tachometer as? ChargeTachometer
}

internal class SREngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), SREngine {
    override val type = KartEngine.Type.SR
    override val tachometer: SRTachometer?
        get() = super.tachometer as? SRTachometer
}

internal class N1EngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), N1Engine {
    override val type = KartEngine.Type.N1
    override val tachometer: N1Tachometer?
        get() = super.tachometer as? N1Tachometer
}

internal class RXEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), RXEngine {
    override val type = KartEngine.Type.RX
    override val tachometer: RXTachometer?
        get() = super.tachometer as? RXTachometer
}

internal class KeyEngineImpl(kart: Kart, rider: Player) : KartEngineImpl(kart, rider), KeyEngine {
    override val type = KartEngine.Type.KEY
    override val tachometer: KeyTachometer?
        get() = super.tachometer as? KeyTachometer
}
