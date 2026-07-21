@file:JvmName("KartInfoUtils")
package io.github.oni0nfr1.skid.client.api.attr

import io.github.oni0nfr1.skid.client.api.attr.unstable.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.kart.ridingKart
import io.github.oni0nfr1.skid.client.api.utils.KartType
import io.github.oni0nfr1.skid.client.api.utils.access
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation

/**
 * `minecraft` 네임스페이스의 [id]에 해당하는 카트 정보 값을 반환합니다.
 *
 * @param id 조회할 modifier ID의 경로
 * @return 수신된 modifier 값, 해당 값이 없거나 조회에 실패하면 `null`
 */
fun KartSaddle.getKartInfo(id: String): Double? {
    val key = ResourceLocation.withDefaultNamespace(id)
    return getKartInfo(key)
}

/**
 * [key]에 해당하는 카트 정보 값을 반환합니다.
 *
 * @param key 조회할 modifier ID
 * @return 수신된 modifier 값, 해당 값이 없거나 조회에 실패하면 `null`
 */
fun KartSaddle.getKartInfo(key: ResourceLocation): Double? {
    val attribute = attributes.getInstance(KartAttributes.KART_INFO_ATTR_KEY) ?: return null
    return attribute.getModifier(key)?.amount
}

/**
 * 카트의 실제 엔진 종류를 반환합니다.
 *
 * @return 실제 엔진 종류, 값이 없거나 알려지지 않은 코드이면 `null`
 */
val KartSaddle.realKartEngine: KartType<*>?
    get() {
        val modifier = getKartInfo(KnownAttrModId.ID_ENGINE_REAL) ?: return null
        return KartType.fromAttrEngineCode(modifier.toInt())
    }

/**
 * 카트에 설정된 엔진 종류를 반환합니다.
 *
 * @return 설정된 엔진 종류, 값이 없거나 알려지지 않은 코드이면 `null`
 */
val KartSaddle.selectedKartEngine: KartType<*>?
    get() {
        val modifier = getKartInfo(KnownAttrModId.ID_ENGINE) ?: return null
        return KartType.fromAttrEngineCode(modifier.toInt())
    }

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
            saddle.getKartInfo(KnownAttrModId.CTX_MAX_LAP)
        }?.toInt() ?: return null
        return if (readValue > 0) readValue else null
    }
