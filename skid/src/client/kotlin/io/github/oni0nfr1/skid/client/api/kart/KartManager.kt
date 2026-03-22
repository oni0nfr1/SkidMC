package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.internal.kart.KartImpl
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.world.entity.player.Player

object KartManager {

    private val kartByEntityId: MutableMap<Int, Kart> = mutableMapOf()
    private val kartByRiderId: MutableMap<Int, Kart> = mutableMapOf()

    fun init() {
        kartByEntityId.clear()
        kartByRiderId.clear()

        ClientTickEvents.END_CLIENT_TICK.register {
            kartByEntityId.forEach { (_, kart) ->
                (kart as KartImpl).tick()
            }
        }
    }


    // 카트 엔티티(대구)를 통한 접근

    internal fun addKart(kart: Kart) {
        kartByEntityId[kart.entity.id] = kart
    }

    internal fun removeKart(kart: Kart) {
        kartByEntityId.remove(kart.entity.id)
    }

    /**
     * @return 카트 엔티티([KartEntity]) 를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    @Suppress("UNUSED")
    fun getKart(entity: KartEntity): Kart? {
        return kartByEntityId[entity.id]
    }

    /**
     * @return 카트 엔티티([KartEntity])의 ID를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    @Suppress("UNUSED")
    fun getKartByEntityId(entityId: Int): Kart? {
        return kartByEntityId[entityId]
    }


    // 카트에 탑승한 플레이어를 통해 접근

    internal fun onKartMount(kartEntity: KartEntity, rider: Player) {
        val kart = getKart(kartEntity) as? KartImpl ?: return
        kart.mountPlayer(rider)
        kartByRiderId[rider.id] = kart
    }

    internal fun onKartDismount(kartEntity: KartEntity, rider: Player) {
        val kart = getKart(kartEntity) as? KartImpl ?: return
        kart.dismountPlayer()
        kartByRiderId.remove(rider.id)
    }

    /**
     * @return 카트의 탑승자([Player])를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    @Suppress("UNUSED")
    fun getKart(rider: Player): Kart? {
        return kartByRiderId[rider.id]
    }

    /**
     * @return 카트의 탑승자([Player])의 ID를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    @Suppress("UNUSED")
    fun getKartByRiderId(riderId: Int): Kart? {
        return kartByRiderId[riderId]
    }
}
