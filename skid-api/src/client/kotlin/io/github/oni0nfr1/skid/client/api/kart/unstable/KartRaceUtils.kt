@file:JvmName("KartRaceUtils")

package io.github.oni0nfr1.skid.client.api.kart.unstable

import io.github.oni0nfr1.skid.client.api.attr.getKartInfo
import io.github.oni0nfr1.skid.client.api.attr.unstable.KnownAttrModId
import io.github.oni0nfr1.skid.client.api.kart.Kart

/**
 * 현재 카트에 기록된 랩 번호입니다.
 *
 * 아직 값이 수신되지 않았거나 현재 카트에 랩 정보가 없으면 `0`을 반환합니다. 이 임시
 * API는 1.1.0에서 레이스 상태 API가 추가되면 그 계약으로 이동할 예정입니다.
 */
val Kart<*>.currentLap: Int
    get() = saddle.getKartInfo(KnownAttrModId.CTX_CURRENT_LAP)?.toInt() ?: 0

/**
 * 현재 카트에 기록된 트랙의 최대 랩 수입니다.
 *
 * 아직 값이 수신되지 않았거나 값이 0 이하이면 `null`을 반환합니다. 이 임시 API는
 * 1.1.0에서 레이스 상태 API가 추가되면 그 계약으로 이동할 예정입니다.
 */
val Kart<*>.maxLap: Int?
    get() {
        val value = saddle.getKartInfo(KnownAttrModId.CTX_MAX_LAP)?.toInt() ?: return null
        return value.takeIf { it > 0 }
    }
