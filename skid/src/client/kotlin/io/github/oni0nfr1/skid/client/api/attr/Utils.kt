@file:JvmName("RiderAttrUtils")
package io.github.oni0nfr1.skid.client.api.attr

import io.github.oni0nfr1.skid.client.api.events.KartAttrEvents
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.api.utils.access
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

/**
 * `minecraft` 네임스페이스의 [id]에 해당하는 카트 메타데이터 값을 반환합니다.
 *
 * @param id 조회할 modifier ID의 경로
 * @return 수신된 modifier 값, 해당 값이 없거나 조회에 실패하면 `null`
 */
fun KartSaddle.getKartMeta(id: String): Double? {
    val key = ResourceLocation.withDefaultNamespace(id)
    return getKartMeta(key)
}

/**
 * [key]에 해당하는 카트 메타데이터 값을 반환합니다.
 *
 * @param key 조회할 modifier ID
 * @return 수신된 modifier 값, 해당 값이 없거나 조회에 실패하면 `null`
 */
fun KartSaddle.getKartMeta(key: ResourceLocation): Double? {
    return try {
        attributes.getModifierValue(KartAttrEvents.KART_META_ATTR_KEY, key)
    } catch (e: Exception) {
        null
    }
}

/**
 * 현재 탑승한 카트에서 `minecraft` 네임스페이스의 [id]에 해당하는 메타데이터 값을 반환합니다.
 *
 * @param id 조회할 modifier ID의 경로
 * @return 현재 카트의 modifier 값, 카트에 탑승하지 않았거나 값이 없으면 `null`
 */
@Deprecated("카트 메타데이터의 주체가 플레이어에서 카트 엔티티로 변경되었습니다. getKartMeta를 사용하세요.")
fun Player.getRiderMeta(id: String): Double? {
    val key = ResourceLocation.withDefaultNamespace(id)
    return ridingKart?.access { saddle.getKartMeta(key) }
}

/**
 * 현재 탑승한 카트에서 [key]에 해당하는 메타데이터 값을 반환합니다.
 *
 * @param key 조회할 modifier ID
 * @return 현재 카트의 modifier 값, 카트에 탑승하지 않았거나 값이 없으면 `null`
 */
@Deprecated("카트 메타데이터의 주체가 플레이어에서 카트 엔티티로 변경되었습니다. getKartMeta를 사용하세요.")
fun Player.getRiderMeta(key: ResourceLocation): Double? {
    return ridingKart?.access { saddle.getKartMeta(key) }
}

/**
 * 카트의 실제 엔진 종류를 반환합니다.
 *
 * @return 실제 엔진 종류, 값이 없거나 알려지지 않은 코드이면 `null`
 */
val KartSaddle.realKartEngine: KartType<*>?
    get() {
        val modifier = getKartMeta(KnownAttrModId.ID_ENGINE_REAL) ?: return null
        return KartType.fromAttrEngineCode(modifier.toInt())
    }

/**
 * 카트에 설정된 엔진 종류를 반환합니다.
 *
 * @return 설정된 엔진 종류, 값이 없거나 알려지지 않은 코드이면 `null`
 */
val KartSaddle.selectedKartEngine: KartType<*>?
    get() {
        val modifier = getKartMeta(KnownAttrModId.ID_ENGINE) ?: return null
        return KartType.fromAttrEngineCode(modifier.toInt())
    }

/**
 * 플레이어가 현재 탑승한 카트의 실제 엔진 종류를 반환합니다.
 *
 * @return 실제 엔진 종류, 값이 없거나 알려지지 않은 코드이면 `null`
 */
val Player.realKartEngine: KartType<*>?
    get() = ridingKart?.access { saddle.realKartEngine }

/**
 * 플레이어가 선택한 엔진 종류를 반환합니다.
 *
 * @return 선택한 엔진 종류, 값이 없거나 알려지지 않은 코드이면 `null`
 */
val Player.selectedKartEngine: KartType<*>?
    get() = ridingKart?.access { saddle.selectedKartEngine }

/**
 * 현재 트랙의 최대 랩 수를 반환합니다.
 *
 * 이 프로퍼티의 receiver는 API 탐색 편의를 위한 것으로, 실제 조회에는 현재 클라이언트 플레이어가 사용됩니다.
 *
 * @return 양수인 최대 랩 수, 플레이어가 없거나 값이 0 이하이면 `null`
 */
@Suppress("UnusedReceiverParameter")
val ClientLevel.maxLap: Int?
    get() {
        val client = Minecraft.getInstance()
        val player = client.player ?: return null
        val readValue = player.ridingKart?.access {
            saddle.getKartMeta(KnownAttrModId.CTX_MAX_LAP)
        }?.toInt() ?: return null
        return if (readValue > 0) readValue else null
    }
