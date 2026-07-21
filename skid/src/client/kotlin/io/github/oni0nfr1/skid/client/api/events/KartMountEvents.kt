package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skid.client.api.kart.kart
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import net.minecraft.world.entity.player.Player

/** 카트 탑승, 하차 및 관전 상태 변경 이벤트를 제공합니다. */
object KartMountEvents {

    /**
     * 엔티티가 카트 엔티티에 탑승할 때 호출됩니다.
     * - 클라이언트 측의 바닐라 처리 직후 시점에 렌더 스레드에서 호출됩니다.
     *
     * ## 주의
     * 이 시점에는 카트의 첫 어트리뷰트 정보가 아직 적용되지 않았을 수 있습니다.
     * 따라서 카트가 준비되기 전에는 [Player.ridingKart]와 [KartSaddle.kart]가 모두 null입니다.
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
     * - 플레이어의 탑승 정보와 카트의 첫 어트리뷰트 갱신이 모두 적용된 시점에 호출됩니다.
     * - 이 시점에는 [Player.ridingKart]와 [KartSaddle.kart]로 준비된 카트에 접근할 수 있습니다.
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


    /**
     * 로컬 플레이어가 카트 탑승자를 관전하기 시작할 때 호출됩니다.
     *
     * 카메라 대상 변경이 실제로 반영된 직후, 대상 카트의 첫 어트리뷰트가 준비되기 전에 렌더 스레드에서 호출될 수 있습니다.
     */
    @Suppress("UNUSED") @JvmField
    val SPECTATE_EARLY = createEvent { listeners ->
        KartSpectateCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kartEntity, rider, target)
            }
        }
    }

    /**
     * 로컬 플레이어가 카트 탑승자를 관전하기 시작하고 대상 카트의 첫 어트리뷰트가 준비되면 호출됩니다.
     *
     * 대상 카트의 메타데이터가 이미 준비된 경우에는 카메라 대상 변경이 실제로 반영된 직후 호출됩니다.
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED") @JvmField
    val SPECTATE = createEvent { listeners ->
        KartSpectateCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kartEntity, rider, target)
            }
        }
    }

    /**
     * 로컬 플레이어가 카트 탑승자 관전을 종료할 때 호출됩니다.
     *
     * 카메라 대상 변경이 실제로 반영된 직후 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED") @JvmField
    val SPECTATE_END = createEvent { listeners ->
        KartSpectateEndCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectateEnd(kartEntity, rider, target)
            }
        }
    }

    /** 카트 탑승 이벤트를 처리합니다. */
    fun interface KartMountCallback {
        /**
         * @param kartEntity 탑승 대상 카트의 대구 엔티티
         * @param rider 카트에 탑승한 플레이어
         */
        fun onKartMount(kartEntity: KartSaddle, rider: Player)
    }

    /** 카트 하차 이벤트를 처리합니다. */
    fun interface KartDismountCallback {
        /**
         * @param kartEntity 하차 대상 카트의 대구 엔티티
         * @param rider 카트에서 내린 플레이어
         */
        fun onKartDismount(kartEntity: KartSaddle, rider: Player)
    }

    /** 카트 탑승자 관전 시작 이벤트를 처리합니다. */
    fun interface KartSpectateCallback {
        /**
         * @param kartEntity 관전 대상이 탑승한 카트의 대구 엔티티
         * @param rider 관전 중인 로컬 플레이어
         * @param target 카트에 탑승한 관전 대상 플레이어
         */
        fun onKartSpectate(kartEntity: KartSaddle, rider: Player, target: Player)
    }

    /** 카트 탑승자 관전 종료 이벤트를 처리합니다. */
    fun interface KartSpectateEndCallback {
        /**
         * @param kartEntity 이전 관전 대상이 탑승한 카트의 대구 엔티티
         * @param rider 관전을 종료한 로컬 플레이어
         * @param target 이전 관전 대상 플레이어
         */
        fun onKartSpectateEnd(kartEntity: KartSaddle, rider: Player, target: Player)
    }
}
