package io.github.oni0nfr1.skid.client.internal.kart

import io.github.oni0nfr1.skid.client.api.attr.realKartEngine
import io.github.oni0nfr1.skid.client.api.kart.KartEntity
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.StaleKartException
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

internal class KartImpl(entity: KartEntity) : Kart {

    override var alive = true
        set(value) {
            if (!value) _entity = null
            field = value
        }

    private var _entity: KartEntity? = entity
    override val entity: KartEntity
        get() = _entity ?: throw StaleKartException()

    // 현재의 엔티티 위치를 그대로 반환
    override val position: Vec3
        get() = _entity?.position() ?: throw StaleKartException()

    override var currentPosition: Vec3 = entity.position()
        get() = if (alive) field else throw StaleKartException()
        private set
    override var prevPosition: Vec3 = entity.position()
        get() = if (alive) field else throw StaleKartException()
        private set
    override var velocity: Vec3 = Vec3.ZERO
        get() = if (alive) field else throw StaleKartException()
        private set

    override var engine: KartEngine? = null
        private set

    fun mountPlayer(player: Player) {
        val engineType = player.realKartEngine
        engine = engineType?.let { KartEngine.withType(it, this, player) }
    }

    fun dismountPlayer() {
        engine = null
    }

    // 틱 종료시 호출
    fun tick() {
        prevPosition = currentPosition
        _entity?.let { currentPosition = it.position() }
        velocity = currentPosition.subtract(prevPosition) // 어차피 1틱 간격이므로 스케일 필요 없음
    }

    override fun hashCode(): Int {
        return entity.id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (!alive) {
            other === null
        } else this === other
    }
}