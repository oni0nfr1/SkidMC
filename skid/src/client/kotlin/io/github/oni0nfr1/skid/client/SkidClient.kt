package io.github.oni0nfr1.skid.client

import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** SkidMC의 클라이언트 Fabric 진입점입니다. */
class SkidClient : ClientModInitializer {

    internal companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(SkidClient::class.java)
    }

    /** 카트와 타코미터 추적기를 초기화합니다. */
    override fun onInitializeClient() {
        KartManager.init()
        TachometerManager.init()
    }
}
