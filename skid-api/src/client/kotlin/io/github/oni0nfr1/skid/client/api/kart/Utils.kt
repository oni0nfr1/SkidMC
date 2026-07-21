@file:JvmName("KartUtils")

package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.spi.SkidApiProviderLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

/**
 * 탑승 상태를 판정할 로컬 플레이어 또는 현재 관전 대상을 반환합니다.
 *
 * @return 관전 중이면 카메라 엔티티, 그렇지 않으면 로컬 플레이어 자신
 */
val LocalPlayer.subject: Entity?
    get() {
        val client = Minecraft.getInstance()
        return if (this.isSpectator) client.cameraEntity else this
    }

/** 현재 로컬 플레이어의 카트 탑승 또는 관전 상태를 반환합니다. */
val LocalPlayer.mountStatus: MountType
    get() {
        val subject = this.subject ?: return MountType.Dismounted()

        return if (this.ridingKart != null) {
            MountType.Mounted()
        } else if (subject != this && (subject as? Player)?.ridingKart != null) {
            MountType.Spectating(subject)
        } else {
            MountType.Dismounted()
        }
    }

/**
 * 이 대구 엔티티에 대응하는 카트 참조를 반환합니다.
 *
 * @return 유효한 카트 참조, SkidMC가 추적 중인 카트가 아니면 `null`
 * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 호출한 경우
 */
val KartSaddle.kart: KartRef?
    get() {
        checkRenderThread()
        return SkidApiProviderLoader.provider.getKart(this.id, this.uuid)
            .map { KartRef(this) }
            .orElse(null)
    }

/**
 * 이 플레이어가 탑승 중인 카트 참조를 반환합니다.
 *
 * @return 유효한 카트 참조, 카트에 탑승하지 않았으면 `null`
 * @throws IllegalStateException 렌더 스레드가 아닌 곳에서 호출한 경우
 */
val Player.ridingKart: KartRef?
    get() {
        checkRenderThread()
        return SkidApiProviderLoader.provider.getKartByRiderId(this.id)
            .map { KartRef(it.saddle) }
            .orElse(null)
    }

private fun checkRenderThread() {
    check(Minecraft.getInstance().isSameThread) {
        "Kart can only be accessed on the render thread"
    }
}
