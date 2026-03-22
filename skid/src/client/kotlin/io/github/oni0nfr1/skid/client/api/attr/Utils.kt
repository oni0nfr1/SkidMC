@file:JvmName("RiderAttrUtils")
package io.github.oni0nfr1.skid.client.api.attr

import io.github.oni0nfr1.skid.client.api.engine.KartEngine
import io.github.oni0nfr1.skid.client.api.events.RiderAttrEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

/**
 * @return id 값에 해당하는 마크라이더 어트리뷰트 수신값을 반환합니다.
 *
 * 네임스페이스는 minecraft로 고정됩니다.
 */
fun Player.getRiderMeta(id: String): Double? {
    val key = ResourceLocation.withDefaultNamespace(id)
    return this.getRiderMeta(key)
}

/**
 * @return 키에 해당하는 마크라이더 어트리뷰트 프로토콜 수신값을 반환합니다.
 */
fun Player.getRiderMeta(key: ResourceLocation): Double? {
    return try {
        this.attributes.getModifierValue(RiderAttrEvents.MixinHandler.RIDER_META_ATTR_KEY, key)
    } catch (e: Exception) {
        null
    }
}

/**
 * @return 플레이어가 타고 있는 엔진 종류를 반환합니다.
 *
 * 유효하지 않은 값이 읽힐 경우 null을 반환합니다.
 */
val Player.realKartEngine: KartEngine.Type?
    get() {
        val modifier = this.getRiderMeta(KnownAttrModId.KART_ENGINE_REAL) ?: return null
        return KartEngine.Type.getByRawModifier(modifier)
    }

/**
 * @return 플레이어가 선택한 엔진 종류를 반환합니다.
 *
 * 유효하지 않은 값이 읽힐 경우 null을 반환합니다.
 */
val Player.selectedKartEngine: KartEngine.Type?
    get() {
        val modifier = this.getRiderMeta(KnownAttrModId.KART_ENGINE) ?: return null
        return KartEngine.Type.getByRawModifier(modifier)
    }

/**
 * @return 현재 있는 트랙의 최대 랩 수를 반환합니다.
 *
 * 인게임 상태가 아니거나 잘못된 값(0바퀴)가 읽힐 경우 null을 반환합니다.
 */
@Suppress("UnusedReceiverParameter")
val ClientLevel.maxLap: Int?
    get() {
        val client = Minecraft.getInstance()
        val player = client.player ?: return null
        val readValue =  player.getRiderMeta(KnownAttrModId.MAX_LAP)?.toInt() ?: return null
        return if (readValue > 0) readValue else null
    }