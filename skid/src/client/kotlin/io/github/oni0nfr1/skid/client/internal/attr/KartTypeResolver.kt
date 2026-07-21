package io.github.oni0nfr1.skid.client.internal.attr

import io.github.oni0nfr1.skid.client.SkidClient
import io.github.oni0nfr1.skid.client.api.attr.getKartInfo
import io.github.oni0nfr1.skid.client.api.attr.unstable.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.kart.KartSaddle
import io.github.oni0nfr1.skid.client.api.utils.KartType

internal object KartTypeResolver {

    /**
     * 카트의 실제 엔진 어트리뷰트를 새 API의 카트 타입으로 변환합니다.
     *
     * 아직 어트리뷰트가 없으면 `null`을 반환합니다. 어트리뷰트는 있지만 SkidMC가 알지
     * 못하는 코드이면 경고를 남기고 `null`을 반환합니다.
     */
    fun resolve(saddle: KartSaddle): KartType<*>? {
        val rawCode = saddle.getKartInfo(KnownAttrModId.ID_ENGINE_REAL) ?: return null
        val attrEngineCode = rawCode.toInt()
        val type = KartType.fromAttrEngineCode(attrEngineCode)

        if (type == null) {
            SkidClient.LOGGER.warn(
                "unknown kart attribute engine code: {} (raw: {})",
                attrEngineCode,
                rawCode,
            )
        }

        return type
    }
}
