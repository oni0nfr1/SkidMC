package io.github.oni0nfr1.skid.client.api.kart

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.utils.KartType
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.animal.Cod
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

typealias KartSaddle = Cod
typealias KartMain = Display.TextDisplay
typealias KartModelRoot = Display.ItemDisplay

interface Kart<out ENGINE : KartEngine> {

    /**
     * 이 객체가 현재 유효한지 여부입니다.
     */
    val alive: Boolean

    /**
     * 카트의 물리 연산 메인이 되는 텍스트 디스플레이 엔티티를 반환합니다.
     */
    val entity: KartMain

    /**
     * 플레이어가 탑승하는 카트의 대구 엔티티를 반환합니다.
     */
    val saddle: KartSaddle

    /**
     * 카트의 모델링 엔티티 메인이 되는 아이템 디스플레이 엔티티를 반환합니다.
     */
    val model: KartModelRoot

    /**
     * 이 카트의 탑승자입니다.
     */
    val rider: Player?

    /**
     * 이 카트의 엔진 타입입니다.
     */
    val type: KartType<ENGINE>

    /**
     * 카트 엔진 객체에 접근할 수 있는 프로퍼티입니다.
     */
    val engine: ENGINE

    /**
     * 카트 메인 엔티티의 현재 위치입니다.
     */
    val position: Vec3

    /**
     * 틱에 기반하여 카트의 속도를 제공합니다.
     *
     * 단위는 `block/tick`입니다.
     *
     * 마크라이더 카트는 텔레포트 방식으로 이동하기 때문에, 카트의 속도를 측정할 때 엔티티 속도값이 아닌 Skid에서 계산한 속도값을 써주세요.
     */
    val velocity: Vec3

}
