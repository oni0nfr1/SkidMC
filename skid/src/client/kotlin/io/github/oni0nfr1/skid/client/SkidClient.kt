package io.github.oni0nfr1.skid.client

import io.github.oni0nfr1.skid.client.api.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.schedule.Ticker
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SkidClient : ClientModInitializer {

    internal companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SkidClient::class.java)
    }

    override fun onInitializeClient() {
        KartManager.init()
        TachometerManager.init()
        Ticker.init()
    }
}
