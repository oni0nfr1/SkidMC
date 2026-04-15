package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import net.minecraft.world.phys.Vec3

/**
 * 주의: Kart 핸들 객체를 사용하는 방식은 멀티스레드 환경에서 예기치 못한 예외가 던져질 수 있습니다.
 */
interface Kart {

    /**
     * 현재 해당 객체가 유효한지를 나타냅니다.
     */
    val alive: Boolean

    /**
     * 카트에 해당하는 엔티티의 ID를 반환합니다.
     * 객체가 무효화되어도 안전하게 접근할 수 있습니다.
     */
    val entityId: Int

    /**
     * 카트에 해당하는 엔티티를 반환합니다.
     */
    val entity: KartEntity

    /**
     * 카트 엔티티의 현재 위치입니다.
     */
    val position: Vec3

    /**
     * 지난 틱 종료 시점에서 마지막으로 기록된 카트 자신의 위치입니다.
     * 매 틱마다의 위치 기록 등의 목적에 사용할 경우 효과적이나, 실시간성이 떨어집니다.
     *
     * 정확한 위치를 원할 때는 [position]을 사용하세요.
     */
    val currentPosition: Vec3

    /**
     * [currentPosition]보다 1틱 전의 위치가 저장됩니다.
     */
    val prevPosition: Vec3

    /**
     * 틱에 기반하여 카트의 속도를 제공합니다.
     *
     * 단위는 **block/tick**입니다.
     *
     * 마크라이더 카트는 텔레포트 방식으로 이동하기 때문에, 카트의 속도를 측정할 때 엔티티 속도값이 아닌 Skid에서 계산한 속도값을 써주세요.
     */
    val velocity: Vec3

    /**
     * 카트 엔진 객체에 접근할 수 있는 프로퍼티입니다.
     *
     * 플레이어가 탑승하고 있지 않아 엔진이 존재하지 않으면 null이 됩니다.
     */
    val engine: KartEngine?
}

