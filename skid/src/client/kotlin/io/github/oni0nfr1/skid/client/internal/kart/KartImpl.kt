package io.github.oni0nfr1.skid.client.internal.kart

import io.github.oni0nfr1.skid.client.api.attr.realKartEngine
import io.github.oni0nfr1.skid.client.api.kart.KartSaddleEntity
import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartMainEntity
import io.github.oni0nfr1.skid.client.api.kart.KartModelRoot
import io.github.oni0nfr1.skid.client.api.kart.StaleKartException
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

internal class KartImpl(saddle: KartSaddleEntity) : Kart {

    override var alive = true

    override val saddleId: Int = saddle.id
    internal val internalSaddleOrNull: KartSaddleEntity?
        get() =
            if (!alive) null
            else Minecraft.getInstance().level?.getEntity(saddleId) as? KartSaddleEntity
    internal val internalEntityOrNull: KartMainEntity?
        get() =
            if (!alive) null
            else internalSaddleOrNull?.vehicle as? KartMainEntity
    internal val internalModelOrNull: KartModelRoot?
        get() {
            if (!alive) return null
            return internalEntityOrNull?.passengers?.find {
                it.customName?.string == "mcrider-modelsaddle" && it is KartModelRoot
            } as KartModelRoot?
        }

    override val saddle: KartSaddleEntity
        get() = internalSaddleOrNull ?: throw StaleKartException()
    override val entity: KartMainEntity
        get() = internalEntityOrNull ?: throw StaleKartException()
    override val model: KartModelRoot
        get() = internalModelOrNull ?: throw StaleKartException()

    // 현재의 엔티티 위치를 그대로 반환
    override val position: Vec3
        get() = internalEntityOrNull?.position() ?: throw StaleKartException()

    override var currentPosition: Vec3 = saddle.position()
        get() = if (alive) field else throw StaleKartException()
        private set
    override var prevPosition: Vec3 = saddle.position()
        get() = if (alive) field else throw StaleKartException()
        private set
    override var velocity: Vec3 = Vec3.ZERO
        get() = if (alive) field else throw StaleKartException()
        private set

    override var engine: KartEngine? = null
        get() = if (alive) field else throw StaleKartException()
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
        internalEntityOrNull?.let { currentPosition = it.position() }
        velocity = currentPosition.subtract(prevPosition) // 어차피 1틱 간격이므로 스케일 필요 없음
    }

    override fun hashCode(): Int {
        return this@KartImpl.saddle.id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (!alive) {
            other === null
        } else this === other
    }
}