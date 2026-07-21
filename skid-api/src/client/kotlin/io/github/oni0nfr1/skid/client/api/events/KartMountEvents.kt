package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.KartRef
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import net.minecraft.world.entity.player.Player

/**
 * 카트 탑승, 하차 및 관전 상태 변경 이벤트를 제공합니다.
 *
 * 탑승과 관전은 엔티티 관계만 필요한 빠른 수명 주기와 준비된 카트가 필요한 수명 주기를
 * 함께 제공합니다. 두 수명 주기는 하나의 공통 종료 이벤트로 정리합니다.
 *
 * ```text
 * MOUNT_EARLY ───────────────→ DISMOUNT
 * MOUNT_EARLY → MOUNT ───────→ DISMOUNT
 *
 * SPECTATE_EARLY ─────────────→ SPECTATE_END
 * SPECTATE_EARLY → SPECTATE ──→ SPECTATE_END
 * ```
 *
 * 준비 전에 관계가 끝나면 [MOUNT]나 [SPECTATE] 없이 종료 이벤트가 호출될 수 있습니다.
 * 따라서 리스너는 자신이 사용한 시작 이벤트에 대응하는 상태만 정리해야 합니다.
 */
object KartMountEvents {

    /**
     * 엔티티가 카트 엔티티에 탑승할 때 관계당 최대 한 번 호출됩니다.
     *
     * 클라이언트 측의 바닐라 처리 직후 렌더 스레드에서 호출됩니다. 이 시점에는 카트의
     * 첫 어트리뷰트가 아직 적용되지 않았을 수 있으므로 [Player.ridingKart]는 `null`일 수
     * 있습니다. 준비 여부와 관계없이 탑승 관계를 식별할 수 있도록 saddle 엔티티를
     * 전달합니다.
     */
    @JvmField
    val MOUNT_EARLY = createEvent { listeners ->
        KartMountEarlyCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartMount(kartEntity, rider)
            }
        }
    }

    /**
     * [MOUNT_EARLY] 이후 플레이어의 탑승 정보와 카트의 첫 어트리뷰트 갱신이 모두
     * 적용되면 관계당 최대 한 번 호출됩니다.
     *
     * 렌더 스레드에서 호출되며, 이 시점에는 [Player.ridingKart]와 콜백의 [KartRef]로
     * 준비된 카트에 접근할 수 있습니다.
     */
    @JvmField
    val MOUNT = createEvent { listeners ->
        KartMountCallback { kart, rider ->
            for (listener in listeners) {
                listener.onKartMount(kart, rider)
            }
        }
    }

    /**
     * [MOUNT_EARLY]가 호출된 탑승 관계가 끝날 때 공통 종료 이벤트로 호출됩니다.
     *
     * 준비 전에 관계가 끝나면 [MOUNT] 없이 호출될 수 있습니다. [MOUNT]까지 호출된
     * 관계에서는 이 콜백이 끝날 때까지 [Player.ridingKart]로 준비된 카트에 접근할 수
     * 있습니다. 준비 전 관계도 정리할 수 있도록 saddle 엔티티를 전달합니다.
     * 준비 전에 사라진 saddle에서는 해당 ID의 [KartRef]가 resolve되지 않습니다.
     *
     * 클라이언트 측의 바닐라 처리 직후 렌더 스레드에서 호출됩니다. 단, 카트 엔티티가
     * 직접 사라지는 경우에는 엔티티 제거 직전에 호출됩니다.
     */
    @JvmField
    val DISMOUNT = createEvent { listeners ->
        KartDismountCallback { kartEntity, rider ->
            for (listener in listeners) {
                listener.onKartDismount(kartEntity, rider)
            }
        }
    }

    /**
     * 로컬 플레이어가 카트 탑승자를 관전하기 시작할 때 관계당 최대 한 번 호출됩니다.
     *
     * 카메라 대상 변경이 실제로 반영된 직후 렌더 스레드에서 호출됩니다. 준비 여부와
     * 관계없이 관전 관계를 식별할 수 있도록 saddle 엔티티를 전달합니다.
     */
    @JvmField
    val SPECTATE_EARLY = createEvent { listeners ->
        KartSpectateEarlyCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kartEntity, rider, target)
            }
        }
    }

    /**
     * [SPECTATE_EARLY] 이후 대상 카트의 첫 어트리뷰트가 준비되면 관계당 최대 한 번
     * 호출됩니다.
     *
     * 대상 카트가 이미 준비된 경우에는 카메라 대상 변경 직후 호출됩니다. 렌더 스레드에서
     * 호출되며 콜백의 [KartRef]로 준비된 카트에 접근할 수 있습니다.
     */
    @JvmField
    val SPECTATE = createEvent { listeners ->
        KartSpectateCallback { kart, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectate(kart, rider, target)
            }
        }
    }

    /**
     * [SPECTATE_EARLY]가 호출된 관전 관계가 끝날 때 공통 종료 이벤트로 호출됩니다.
     *
     * 준비 전에 관계가 끝나면 [SPECTATE] 없이 호출될 수 있습니다. 카메라 대상 변경,
     * 대상의 하차, 대상 카트 제거 또는 클라이언트 월드 종료 시 렌더 스레드에서 호출됩니다.
     * 준비 전에 사라진 saddle에서는 해당 ID의 [KartRef]가 resolve되지 않습니다.
     */
    @JvmField
    val SPECTATE_END = createEvent { listeners ->
        KartSpectateEndCallback { kartEntity, rider, target ->
            for (listener in listeners) {
                listener.onKartSpectateEnd(kartEntity, rider, target)
            }
        }
    }

    /** 준비 전 카트 탑승 이벤트를 처리합니다. */
    fun interface KartMountEarlyCallback {
        /**
         * @param kartEntity 탑승 대상 카트의 saddle 엔티티
         * @param rider 카트에 탑승한 플레이어
         */
        fun onKartMount(kartEntity: KartSaddle, rider: Player)
    }

    /** 준비된 카트 탑승 이벤트를 처리합니다. */
    fun interface KartMountCallback {
        /**
         * @param kart 탑승 대상 카트의 참조
         * @param rider 카트에 탑승한 플레이어
         */
        fun onKartMount(kart: KartRef, rider: Player)
    }

    /** 카트 하차 이벤트를 처리합니다. */
    fun interface KartDismountCallback {
        /**
         * @param kartEntity 하차 대상 카트의 saddle 엔티티
         * @param rider 카트에서 내린 플레이어
         */
        fun onKartDismount(kartEntity: KartSaddle, rider: Player)
    }

    /** 준비 전 카트 탑승자 관전 시작 이벤트를 처리합니다. */
    fun interface KartSpectateEarlyCallback {
        /**
         * @param kartEntity 관전 대상이 탑승한 카트의 saddle 엔티티
         * @param rider 관전 중인 로컬 플레이어
         * @param target 카트에 탑승한 관전 대상 플레이어
         */
        fun onKartSpectate(kartEntity: KartSaddle, rider: Player, target: Player)
    }

    /** 준비된 카트 탑승자 관전 시작 이벤트를 처리합니다. */
    fun interface KartSpectateCallback {
        /**
         * @param kart 관전 대상이 탑승한 카트의 참조
         * @param rider 관전 중인 로컬 플레이어
         * @param target 카트에 탑승한 관전 대상 플레이어
         */
        fun onKartSpectate(kart: KartRef, rider: Player, target: Player)
    }

    /** 카트 탑승자 관전 종료 이벤트를 처리합니다. */
    fun interface KartSpectateEndCallback {
        /**
         * @param kartEntity 이전 관전 대상이 탑승했던 카트의 saddle 엔티티
         * @param rider 관전을 종료한 로컬 플레이어
         * @param target 이전 관전 대상 플레이어
         */
        fun onKartSpectateEnd(kartEntity: KartSaddle, rider: Player, target: Player)
    }
}
