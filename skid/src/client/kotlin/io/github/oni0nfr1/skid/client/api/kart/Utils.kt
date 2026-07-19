@file:JvmName("KartUtils")

package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.api.utils.access
import io.github.oni0nfr1.skid.client.internal.kart.KartManager
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Cod
import net.minecraft.world.entity.player.Player

/** 플레이어가 탑승하는 카트의 대구 엔티티 타입입니다. */
typealias KartSaddleEntity = Cod
/** 카트의 위치와 물리 연산 기준으로 사용하는 텍스트 디스플레이 타입입니다. */
typealias KartMainEntity = Display.TextDisplay
/** 카트 모델의 루트로 사용하는 아이템 디스플레이 타입입니다. */
typealias KartModelRoot = Display.ItemDisplay
/** 카트 모델의 방향 정보를 나타내는 아이템 디스플레이 타입입니다. */
typealias KartDirection = Display.ItemDisplay

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
 */
val KartSaddleEntity.kart: KartRef?
    get() = KartManager.getBySaddleId(this.id)?.let { KartRef(this.id) }

/**
 * 이 플레이어가 탑승 중인 카트 참조를 반환합니다.
 *
 * @return 유효한 카트 참조, 카트에 탑승하지 않았으면 `null`
 */
val Player.ridingKart: KartRef?
    get() = KartManager.getByRiderId(this.id)?.let { KartRef(it.saddleId) }

/**
 * 현재 로컬 플레이어 또는 관전 대상이 탑승한 카트의 엔진 타입을 반환합니다.
 *
 * @return 현재 엔진 타입, 대상이나 카트 또는 엔진을 확인할 수 없으면 `null`
 */
val Minecraft.kartEngineType: KartType<*, *>?
    get() = (this.player?.subject as? Player)?.ridingKart?.access { type }
