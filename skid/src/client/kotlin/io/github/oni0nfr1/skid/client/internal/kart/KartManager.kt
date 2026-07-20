package io.github.oni0nfr1.skid.client.internal.kart

import io.github.oni0nfr1.skid.client.SkidClient
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.internal.attr.KartTypeResolver
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.player.Player

internal object KartManager {

    private val level: ClientLevel?
        get() = Minecraft.getInstance().level
    private val saddleIdToKart: MutableMap<Int, KartImpl<*, *>> = mutableMapOf()
    private val pendingKartIds: MutableSet<Int> = mutableSetOf()

    /**
     * ready 카트의 rider와 saddle 사이의 역방향 조회 상태입니다.
     *
     * INVARIANT:
     * - 각 rider와 saddle은 최대 하나의 active 관계에만 속한다.
     * - 각 항목에 대응하는 ready Kart의 rider 프로퍼티는 같은 Player를 가리킨다.
     *
     * THREADING:
     * - 렌더 스레드에서만 접근하고 변경한다.
     */
    private val riderIdToSaddleId: MutableMap<Int, Int> = mutableMapOf()

    fun init() {
        clear()
        ClientTickEvents.END_CLIENT_TICK.register {
            saddleIdToKart.forEach { (_, kart) -> kart.tick() }
        }
        ClientPlayConnectionEvents.INIT.register(this::clear)
    }

    fun clear(vararg nothing: Any) {
        saddleIdToKart.values.forEach { it.alive = false }
        saddleIdToKart.clear()
        pendingKartIds.clear()
        riderIdToSaddleId.clear()
    }

    fun trackKart(saddle: KartSaddle) {
        saddleIdToKart.remove(saddle.id)?.alive = false
        pendingKartIds.add(saddle.id)
    }

    fun prepareKart(saddle: KartSaddle): KartImpl<*, *>? {
        val saddleId = saddle.id
        if (saddleId !in pendingKartIds) return null
        val type = KartTypeResolver.resolve(saddle) ?: return null
        val kart = KartFactory.create(saddle, type)

        saddleIdToKart[saddleId] = kart
        pendingKartIds.remove(saddleId)
        return kart
    }

    fun isReady(saddleId: Int): Boolean =
        saddleId in saddleIdToKart && saddleId !in pendingKartIds

    fun getKartType(saddleId: Int) = saddleIdToKart[saddleId]?.type

    fun mountRider(riderId: Int, saddleId: Int): Boolean {
        val kart = getBySaddleId(saddleId) ?: return false
        val rider = level?.getEntity(riderId) as? Player ?: return false
        if (riderIdToSaddleId[riderId] == saddleId && kart.rider === rider) return false

        val previousSaddleId = riderIdToSaddleId[riderId]
        if (previousSaddleId != null && previousSaddleId != saddleId) {
            SkidClient.LOGGER.warn(
                "Active kart passenger changed saddles without a completed dismount: " +
                    "previousSaddleId={}, newSaddleId={}, riderId={}; repairing the relation",
                previousSaddleId,
                saddleId,
                riderId,
            )
            getBySaddleId(previousSaddleId)?.dismountPlayer()
            riderIdToSaddleId.remove(riderId)
        }

        val displacedRiderIds = getRiderIdsBySaddleId(saddleId) - riderId
        if (displacedRiderIds.isNotEmpty()) {
            SkidClient.LOGGER.warn(
                "Active kart has multiple riders: saddleId={}, previousRiderIds={}, newRiderId={}; " +
                    "removing the previous active relations",
                saddleId,
                displacedRiderIds,
                riderId,
            )
            displacedRiderIds.forEach(riderIdToSaddleId::remove)
        }

        kart.mountPlayer(rider)
        riderIdToSaddleId[riderId] = saddleId
        return true
    }

    fun getSaddleIdByRiderId(riderId: Int): Int? = riderIdToSaddleId[riderId]

    fun getRiderIdsBySaddleId(saddleId: Int): Set<Int> = riderIdToSaddleId
        .filterValues { it == saddleId }
        .keys

    fun getTrackedSaddleIds(): Set<Int> = buildSet {
        addAll(saddleIdToKart.keys)
        addAll(pendingKartIds)
    }

    fun removeKart(saddleId: Int): Boolean {
        val removed = saddleIdToKart.remove(saddleId)
        val wasPending = pendingKartIds.remove(saddleId)
        if (removed == null) return wasPending

        riderIdToSaddleId.entries.removeIf { it.value == saddleId }
        removed.alive = false
        return true
    }

    fun dismountRider(riderId: Int): Int? {
        val saddleId = riderIdToSaddleId.remove(riderId) ?: return null
        getBySaddleId(saddleId)?.dismountPlayer()
        return saddleId
    }

    fun getBySaddleId(saddleId: Int): KartImpl<*, *>? {
        val handle = saddleIdToKart[saddleId]
        if (handle?.alive == false) {
            removeKart(saddleId)
            return null
        }
        return handle
    }

    fun getByRiderId(riderId: Int): KartImpl<*, *>? {
        val saddleId = riderIdToSaddleId[riderId] ?: return null
        return getBySaddleId(saddleId) ?: run {
            riderIdToSaddleId.remove(riderId)
            null
        }
    }
}
