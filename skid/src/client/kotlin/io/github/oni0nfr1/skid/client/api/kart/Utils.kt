@file:JvmName("KartUtils")

package io.github.oni0nfr1.skid.client.api.kart

import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Cod
import net.minecraft.world.entity.player.Player

typealias KartEntity = Cod

val LocalPlayer.subject: Entity?
    get() {
        val client = Minecraft.getInstance()
        return if (this.isSpectator) client.cameraEntity else this
    }

val KartEntity.kart: Kart?
    get() = KartManager.getKart(this)

val Player.ridingKart: Kart?
    get() = KartManager.getKart(this)