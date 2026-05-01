@file:JvmName("KartUtils")

package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Cod
import net.minecraft.world.entity.player.Player

typealias KartSaddleEntity = Cod
typealias KartMainEntity = Display.TextDisplay
typealias KartModelRoot = Display.ItemDisplay
typealias KartDirection = Display.ItemDisplay

val LocalPlayer.subject: Entity?
    get() {
        val client = Minecraft.getInstance()
        return if (this.isSpectator) client.cameraEntity else this
    }

val LocalPlayer.mountStatus: MountType
    get() {
        val subject = this.subject ?: return MountType.Dismounted()

        return if (this.ridingKart != null) {
            MountType.Mounted()
        } else if (subject != this && (subject as? Player)?.ridingKart != null) {
            MountType.Spectating(subject)
        } else {
            MountType.Dismounted()
        }
    }

val KartSaddleEntity.kart: KartRef?
    get() = KartRef(KartManager.getBySaddleId(this.id) ?: return null)

val Player.ridingKart: KartRef?
    get() = KartRef(KartManager.getByRiderId(this.id) ?: return null)

val Minecraft.kartEngineType: KartEngine.Type?
    get() = (this.player?.subject as? Player)?.ridingKart?.access { engine?.type }