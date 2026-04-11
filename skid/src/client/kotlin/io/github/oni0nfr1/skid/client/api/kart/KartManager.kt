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

    /**
     * KartManager에서 [kart]를 제거한 뒤 그것을 비활성화합니다.
     * 이 함수 호출 이후에는 [kart]의 수명주기가 끝나며 접근 시 [StaleKartException]을 던집니다.
     */
    internal fun removeKart(kart: Kart) {
        val removed = kartByEntityId.remove(kart.entity.id)
        (removed as? KartImpl)?.alive = false
    }

    /**
     * @return 카트 엔티티([KartEntity]) 를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    @Suppress("UNUSED")
    fun getKart(entity: KartEntity): KartRef? {
        return getKartHandle(entity)?.let{ KartRef(it) }
    }

    /**
     * @return 카트 엔티티([KartEntity])의 ID를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    fun getKartByEntityId(entityId: Int): KartRef? {
        return getKartHandleByEntityId(entityId)?.let{ KartRef(it) }
    }

    internal fun getKartHandleByEntityId(entityId: Int): Kart? {
        return kartByEntityId[entityId]
    }

    internal fun getKartHandle(entity: KartEntity): Kart? {
        return kartByEntityId[entity.id]
    }


    // 카트에 탑승한 플레이어를 통해 접근

    internal fun onKartMount(kartEntity: KartEntity, rider: Player) {
        val kart = getKartHandle(kartEntity) as? KartImpl ?: return
        kart.mountPlayer(rider)
        kartByRiderId[rider.id] = kart
    }

    internal fun onKartDismount(kartEntity: KartEntity, rider: Player) {
        val kart = getKartHandle(kartEntity) as? KartImpl ?: return
        kart.dismountPlayer()
        kartByRiderId.remove(rider.id)
    }

    /**
     * @return 카트의 탑승자([Player])를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    @Suppress("UNUSED")
    fun getKart(rider: Player): KartRef? {
        return getKartHandle(rider)?.let { KartRef(it) }
    }

    internal fun isRiding(rider: Player): Boolean {
        return kartByRiderId[rider.id] != null
    }

    internal fun getKartHandle(rider: Player): Kart? {
        return kartByRiderId[rider.id]
    }

    /**
     * @return 카트의 탑승자([Player])의 ID를 통해 [KartManager]에 등록된 카트를 반환합니다.
     *
     * 해당하는 카트를 찾지 못할 경우 null을 반환합니다.
     */
    fun getKartByRiderId(riderId: Int): KartRef? {
        return getKartHandleByRiderId(riderId)?.let { KartRef(it) }
    }

    internal fun getKartHandleByRiderId(riderId: Int): Kart? {
        return kartByRiderId[riderId]
    }
}
