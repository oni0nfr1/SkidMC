package io.github.oni0nfr1.skid.client.internal.kart

import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.internal.attr.KartTypeResolver
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.player.Player

internal object KartManager {

    private val level: ClientLevel?
        get() = Minecraft.getInstance().level
    private val saddleIdToKart: MutableMap<Int, KartImpl> = mutableMapOf()
    private val pendingKartIds: MutableSet<Int> = mutableSetOf()
    private val saddleIdToType: MutableMap<Int, KartType<*, *>> = mutableMapOf()
    private val riderIdToSaddleId: MutableMap<Int, Int> = mutableMapOf()

    fun init() {
        clear()
        ClientTickEvents.END_CLIENT_TICK.register {
            saddleIdToKart.forEach { (_, kart) -> kart.tick() }
        }
        ClientPlayConnectionEvents.INIT.register(this::clear)
        ClientPlayConnectionEvents.JOIN.register(this::clear)
        ClientPlayConnectionEvents.DISCONNECT.register(this::clear)
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(this::clear)
    }

    fun clear(vararg nothing: Any) {
        saddleIdToKart.clear()
        pendingKartIds.clear()
        saddleIdToType.clear()
        riderIdToSaddleId.clear()
    }

    fun addKart(kart: KartImpl) {
        saddleIdToKart[kart.saddleId] = kart
        saddleIdToType.remove(kart.saddleId)
        pendingKartIds.add(kart.saddleId)
    }

    fun prepareKart(saddle: KartSaddle): KartType<*, *>? {
        val saddleId = saddle.id
        if (saddleId !in saddleIdToKart) return null
        if (saddleId !in pendingKartIds) return null
        val type = KartTypeResolver.resolve(saddle) ?: return null

        saddleIdToType[saddleId] = type
        pendingKartIds.remove(saddleId)
        return type
    }

    fun isReady(saddleId: Int): Boolean =
        saddleId in saddleIdToType && saddleId !in pendingKartIds

    fun getKartType(saddleId: Int): KartType<*, *>? = saddleIdToType[saddleId]

    fun mountRider(riderId: Int, saddleId: Int): Boolean {
        val kart = getBySaddleId(saddleId) ?: return false
        val rider = level?.getEntity(riderId) as? Player ?: return false

        kart.mountPlayer(rider)
        riderIdToSaddleId[riderId] = saddleId
        return true
    }

    fun removeKart(saddleId: Int): Boolean {
        val removed = saddleIdToKart.remove(saddleId) ?: return false

        riderIdToSaddleId.entries.removeIf { it.value == saddleId }
        pendingKartIds.remove(saddleId)
        saddleIdToType.remove(saddleId)
        removed.alive = false
        return true
    }

    fun dismountRider(riderId: Int): Int? {
        val saddleId = riderIdToSaddleId.remove(riderId) ?: return null
        getBySaddleId(saddleId)?.dismountPlayer()
        return saddleId
    }

    fun getBySaddleId(saddleId: Int): KartImpl? {
        val handle = saddleIdToKart[saddleId]
        if (handle?.alive == false) {
            removeKart(saddleId)
            return null
        }
        return handle
    }

    fun getByRiderId(riderId: Int): KartImpl? {
        val saddleId = riderIdToSaddleId[riderId] ?: return null
        return getBySaddleId(saddleId) ?: run {
            riderIdToSaddleId.remove(riderId)
            null
        }
    }
}
