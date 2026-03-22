package io.github.oni0nfr1.skidTest.client

import io.github.oni0nfr1.korigadier.api.Fragment
import io.github.oni0nfr1.skidTest.client.generated.GeneratedSkidTests
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.resources.ResourceLocation

object UnitRegistry {
    // 모드 부트스트랩에서만 작동함
    // 마크 켜지면 바로 유닛 등록
    val units = mutableMapOf<String, TestUnit>()
    val unitCommands = mutableListOf<Fragment<FabricClientCommandSource>>()

    fun bootstrap() {
        GeneratedSkidTests.bootstrap()

        HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
            units.forEach { (name, unit) ->
                val layerId = ResourceLocation.fromNamespaceAndPath("skid-test", name)
                layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, layerId, unit::drawHud)
            }
        }

        SkidTestClient.logger.info("Test Units Loaded:")
        units.forEach { (name, unit) ->
            SkidTestClient.logger.info("$name: $unit")
        }
        SkidTestClient.logger.info("total: ${units.size}")
    }
}
