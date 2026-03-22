package io.github.oni0nfr1.skid.client.internal.kart

import io.github.oni0nfr1.skid.client.api.attr.realKartEngine
import io.github.oni0nfr1.skid.client.api.kart.KartEntity
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

internal class KartImpl(override val entity: KartEntity) : Kart {

    // 현재의 엔티티 위치를 그대로 반환
    override val position: Vec3
        get() = entity.position()

    @Volatile override var currentPosition: Vec3 = entity.position()
        private set
    @Volatile override var prevPosition: Vec3 = entity.position()
        private set
    @Volatile override var velocity: Vec3 = Vec3.ZERO
        private set

    override var engine: KartEngine? = null
        private set

    fun mountPlayer(player: Player) {
        val engineType = player.realKartEngine
        engine = if (engineType != null) {
            KartEngine.withType(engineType, this, player)
        } else null
    }

    fun dismountPlayer() {
        engine = null
    }

    // 틱 종료시 호출
    fun tick() {
        prevPosition = currentPosition
        currentPosition = entity.position()
        velocity = currentPosition.subtract(prevPosition) // 어차피 1틱 간격이므로 스케일 필요 없음
    }

}