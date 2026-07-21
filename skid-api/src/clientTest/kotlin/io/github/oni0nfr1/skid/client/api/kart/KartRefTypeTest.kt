package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.NitroEngine
import io.github.oni0nfr1.skid.client.api.engine.XEngine
import io.github.oni0nfr1.skid.client.api.kart.unstable.currentLap
import io.github.oni0nfr1.skid.client.api.kart.unstable.maxLap
import io.github.oni0nfr1.skid.client.api.tachometer.NitroTachometer
import io.github.oni0nfr1.skid.client.api.tachometer.XTachometer
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.api.utils.Ref
import io.github.oni0nfr1.skid.client.api.utils.access
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Player

@Suppress("unused", "UNUSED_VARIABLE")
private fun specifiedKartTypeIsInferred(ref: KartRef) {
    val specified: Ref<Kart<XEngine>> = ref.specify(KartType.X)

    specified.access {
        val typedEngine: XEngine = engine
        val typedTachometer: XTachometer? = engine.tachometer
    }
}

@Suppress("unused", "UNUSED_VARIABLE")
private fun kartEngineTypeIsCovariant(kart: Kart<XEngine>) {
    val nitroKart: Kart<NitroEngine> = kart
    val currentLap: Int = kart.currentLap
    val maxLap: Int? = kart.maxLap
}

@Suppress("unused", "UNUSED_VARIABLE")
private fun intermediateEngineRetainsItsTachometerType(engine: NitroEngine) {
    val kart: Kart<NitroEngine> = engine.kart
    val tachometer: NitroTachometer? = engine.tachometer
}

@Suppress("unused", "UNUSED_VARIABLE")
private fun engineRetainsItsConcreteKartType(engine: XEngine) {
    val kart: Kart<XEngine> = engine.kart
}

@Suppress("unused", "UNUSED_VARIABLE")
private fun kartAccessorsExposeApiTypes(
    saddle: KartSaddle,
    rider: Player,
    localPlayer: LocalPlayer,
) {
    val directRef: KartRef = KartRef(saddle)
    val kart: KartRef? = saddle.kart
    val ridingKart: KartRef? = rider.ridingKart
    val mountStatus: MountType = localPlayer.mountStatus
}
