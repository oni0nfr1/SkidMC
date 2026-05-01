package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.KartSaddleEntity
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import net.minecraft.world.entity.player.Player

object KartMountEvents {

    /**
     * 엔티티가 카트 엔티티에 탑승할 때 호출됩니다.
     * - 클라이언트 측의 바닐리 처리 직후 시점에 렌더 스레드에서 호출됩니다.
     *
     * ## 주의
     * 이 시점에는 카트 탑승자의 어트리뷰트 정보가 아직 적용되지 않았습니다.
     * 따라서 탑승자의 [Player.ridingKart]가 null이며 [KartSaddleEntity.kart]의 엔진 정보 또한 초기화되지 않은 상태(null)입니다.
     */
    @Suppress("UNUSED") @JvmField
    val MOUNT_EARLY = createEvent { listeners ->
        KartMountCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartMount(kartEntity, rider)
            }
        }
    }

    /**
     * 엔티티가 카트 엔티티에 탑승할 때 호출됩니다.
     * - 플레이어가 카트에 탑승한 직후 첫 어트리뷰트 갱신을 받는 시점에 호출됩니다.
     */
    @Suppress("UNUSED") @JvmField
    val MOUNT = createEvent { listeners ->
        KartMountCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartMount(kartEntity, rider)
            }
        }
    }

    /**
     * 엔티티가 카트에서 내릴 때 호출됩니다.
     * - 클라이언트 측의 바닐라 처리 직후 시점에 렌더 스레드에서 호출됩니다.
     * - 단, 카트 엔티티가 직접 사라짐으로 인해 내려지는 경우는 다른 바닐라 처리 직후, 엔티티 제거 직전에 호출됩니다.
     */
    @Suppress("UNUSED") @JvmField
    val DISMOUNT = createEvent { listeners ->
        KartDismountCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartDismount(kartEntity, rider)
            }
        }
    }

    @Suppress("UNUSED") @JvmField
    val SPECTATE_EARLY = createEvent { listeners ->
        KartSpectateCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kartEntity, rider, target)
            }
        }
    }

    @Suppress("UNUSED") @JvmField
    val SPECTATE = createEvent { listeners ->
        KartSpectateCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kartEntity, rider, target)
            }
        }
    }

    @Suppress("UNUSED") @JvmField
    val SPECTATE_END = createEvent { listeners ->
        KartSpectateEndCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectateEnd(kartEntity, rider, target)
            }
        }
    }

    fun interface KartMountCallback {
        fun onKartMount(kartEntity: KartSaddleEntity, rider: Player)
    }

    fun interface KartDismountCallback {
        fun onKartDismount(kartEntity: KartSaddleEntity, rider: Player)
    }

    fun interface KartSpectateCallback {
        fun onKartSpectate(kartEntity: KartSaddleEntity, rider: Player, target: Player)
    }

    fun interface KartSpectateEndCallback {
        fun onKartSpectateEnd(kartEntity: KartSaddleEntity, rider: Player, target: Player)
    }
}