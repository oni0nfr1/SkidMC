package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.api.events.KartTachometerEvents
import io.github.oni0nfr1.skid.client.api.kart.subject
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object KartTachometerMixinHandler {
    private val client: Minecraft by MCClient

    /**
     * [ClientboundSetActionBarTextPacket]을 수신하여 [Gui.setOverlayMessage]를 호출하기 직전 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onSetActionBarText
     */
    @JvmStatic
    fun onSetActionbarPacket(packet: ClientboundSetActionBarTextPacket, ci: CallbackInfo) {
        val text = packet.text
        val subject = client.player?.subject as? Player ?: run {
            TachometerManager.clear()
            return
        }
        val kart = KartManager.getByRiderId(subject.id) ?: run {
            TachometerManager.clear()
            return
        }
        val engine = kart.engine ?: run {
            TachometerManager.clear()
            return
        }

        val result = TachometerManager.handleActionbar(kart, engine, text)
        if (result == KartTachometerEvents.Result.BLOCK) ci.cancel()
    }
}