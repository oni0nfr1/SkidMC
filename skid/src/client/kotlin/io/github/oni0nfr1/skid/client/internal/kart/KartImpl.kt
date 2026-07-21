package io.github.oni0nfr1.skid.client.internal.kart

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartMain
import io.github.oni0nfr1.skid.client.api.kart.KartModelRoot
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.utils.KartType
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

internal class KartImpl<ENGINE : KartEngine>(
    saddle: KartSaddle,
    override val type: KartType<ENGINE>,
) : Kart<ENGINE> {

    override var alive = true

    val saddleId: Int = saddle.id
    internal val internalSaddleOrNull: KartSaddle?
        get() =
            if (!alive) null
            else Minecraft.getInstance().level?.getEntity(saddleId) as? KartSaddle
    internal val internalEntityOrNull: KartMain?
        get() =
            if (!alive) null
            else internalSaddleOrNull?.vehicle as? KartMain
    internal val internalModelOrNull: KartModelRoot?
        get() {
            if (!alive) return null
            return internalEntityOrNull?.passengers?.find {
                it.customName?.string == "mcrider-modelsaddle" && it is KartModelRoot
            } as KartModelRoot?
        }

    override val saddle: KartSaddle
        get() = internalSaddleOrNull ?: unavailable("saddle")
    override val entity: KartMain
        get() = internalEntityOrNull ?: unavailable("main entity")
    override val model: KartModelRoot
        get() = internalModelOrNull ?: unavailable("model")

    // 현재의 엔티티 위치를 그대로 반환
    override val position: Vec3
        get() = internalEntityOrNull?.position() ?: unavailable("position")

    internal var currentPosition: Vec3 = saddle.position()
        get() = if (alive) field else unavailable("current position")
        private set
    internal var prevPosition: Vec3 = saddle.position()
        get() = if (alive) field else unavailable("previous position")
        private set
    override var velocity: Vec3 = Vec3.ZERO
        get() = if (alive) field else unavailable("velocity")
        private set

    private lateinit var internalEngine: ENGINE
    override val engine: ENGINE
        get() = if (alive && ::internalEngine.isInitialized) internalEngine else unavailable("engine")

    override var rider: Player? = null
        private set

    fun initializeEngine(engine: ENGINE) {
        check(!::internalEngine.isInitialized) { "kart engine is already initialized" }
        internalEngine = engine
    }

    fun mountPlayer(player: Player) { rider = player }

    fun dismountPlayer() { rider = null }

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

    private fun unavailable(property: String): Nothing {
        throw IllegalStateException("Kart $property is not available")
    }
}
