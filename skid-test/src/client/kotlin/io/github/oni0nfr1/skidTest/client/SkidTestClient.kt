package io.github.oni0nfr1.skidTest.client

import com.mojang.brigadier.Command
import io.github.oni0nfr1.korigadier.api.korigadier
import io.github.oni0nfr1.skidTest.client.TestUnit.TestResult.*
import io.github.oni0nfr1.skidTest.client.utils.sendChat
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SkidTestClient : ClientModInitializer {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(SkidTestClient::class.java)
    }


    override fun onInitializeClient() {
        val client = Minecraft.getInstance()

        UnitRegistry.bootstrap()

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            korigadier(dispatcher) {
                literal("skid-test") {
                    UnitRegistry.unitCommands.forEach { include(it) }

                    literal("list-units") {
                        executes {
                            UnitRegistry.units.forEach { (id, unit) ->
                                logger.info("$id: $unit")

                                val color = when (unit.status) {
                                    SUCCESS -> 0xFF00FF00.toInt()
                                    FAIL -> 0xFFFF0000.toInt()
                                    TESTING_AUTO -> 0xFFFFFF00.toInt()
                                    TESTING -> 0xFFFFFF00.toInt()
                                    INACTIVE -> 0xFF959595.toInt()
                                }

                                val msg = Component.literal("$id: ")
                                    .append(
                                        Component.literal(unit.status.name)
                                            .withColor(color)
                                    )
                                client.sendChat(msg)
                            }
                            Command.SINGLE_SUCCESS
                        }
                    } // literal "list-units"

                } // literal "skid-test"

            } // korigadier root
        }

    } // override fun onInitializeClient()
}
