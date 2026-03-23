package io.github.oni0nfr1.skidTest.client

import com.mojang.brigadier.Command
import io.github.oni0nfr1.korigadier.api.fragment
import io.github.oni0nfr1.skidTest.client.utils.sendChat
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

abstract class TestUnit {

    companion object {
        val client: Minecraft by lazy {
            Minecraft.getInstance()
        }
    }

    abstract val id: String
    abstract val description: String
    var status: TestResult = TestResult.INACTIVE

    abstract fun test(): TestResult

    fun start() {
        if (status != TestResult.INACTIVE) {
            val msg = Component.literal("test $id is already started.")
                .withColor(0xFFFF0000.toInt())
            client.sendChat(msg)
        } else {
            val msg = Component.literal("test $id started.")
                .withColor(0xFF00FF00.toInt())
            client.sendChat(msg)

            client.sendChat("[Test Instructions]")
            client.sendChat(description)

            status = test()
        }
    }

    fun success() {
        if (status != TestResult.TESTING) {
            val msg = Component.literal("test $id is not running or is auto-testing.")
                .withColor(0xFFFF0000.toInt())
            client.sendChat(msg)
        } else {
            val msg = Component.literal("test $id success.")
                .withColor(0xFF00FF00.toInt())
            client.sendChat(msg)

            status = TestResult.SUCCESS
        }
    }

    fun fail() {
        if (status != TestResult.TESTING) {
            val msg = Component.literal("test $id is not running or is auto-testing.")
                .withColor(0xFFFF0000.toInt())
            client.sendChat(msg)
        } else {
            val msg = Component.literal("test $id failed.")
                .withColor(0xFFFF0000.toInt())
            client.gui.chat.addMessage(msg)
            client.narrator.sayNow(msg)

            status = TestResult.FAIL
        }
    }

    fun register() {
        UnitRegistry.units[id] = this
        UnitRegistry.unitCommands.add(unitCommand)
    }

    open fun drawHud(guiGraphics: GuiGraphics, tickDelta: DeltaTracker) {
        // 기본 구현은 비어 있음
        // 테스트 중에 표시해야 할 HUD가 있을 경우 여기에 표시
    }

    open val unitCommand
        get() = fragment<FabricClientCommandSource> {
            literal(id) {
                literal("start") {
                    executes {
                        this@TestUnit.start()
                        Command.SINGLE_SUCCESS
                    }
                }

                literal("success") {
                    executes {
                        this@TestUnit.success()
                        Command.SINGLE_SUCCESS
                    }
                }

                literal("fail") {
                    executes {
                        this@TestUnit.fail()
                        Command.SINGLE_SUCCESS
                    }
                }
            }
        }

    enum class TestResult(val success: Boolean, val done: Boolean, val testing: Boolean) {
        SUCCESS(true, true, false),
        FAIL(false, true, false),
        TESTING_AUTO(false, false, true),
        TESTING(false, false, true),
        INACTIVE(false, false, false),
    }
}
