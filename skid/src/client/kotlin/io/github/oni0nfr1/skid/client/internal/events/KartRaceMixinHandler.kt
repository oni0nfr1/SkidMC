package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.world.scores.Scoreboard

object KartRaceMixinHandler {
    private val client: Minecraft by MCClient
    private val scoreboard: Scoreboard?
        get() = client.level?.scoreboard

    private const val RACE_TIMER_OBJECTIVE_NAME = "timerdisplay"
}