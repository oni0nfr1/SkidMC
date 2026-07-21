package io.github.oni0nfr1.skid.client.api.kart

import net.minecraft.world.entity.Entity

/** 로컬 플레이어의 카트 탑승 또는 관전 상태입니다. */
sealed interface MountType {
    /** 카트에 탑승하지 않았고 카트 탑승자를 관전하지도 않는 상태입니다. */
    class Dismounted : MountType

    /** 로컬 플레이어가 카트에 직접 탑승한 상태입니다. */
    class Mounted : MountType

    /**
     * 카트에 탑승한 다른 플레이어를 관전하는 상태입니다.
     *
     * @property camera 현재 카메라가 따라가는 관전 대상
     */
    class Spectating(val camera: Entity) : MountType
}
