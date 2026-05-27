package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.resources.ResourceLocation

/**
 * 잘 알려진 마크라이더의 플레이어에 붙는 고유 어트리뷰트 modifier들의 ID를 저장합니다.
 *
 * 일반적인 상황에서는 [io.github.oni0nfr1.skid.client.api.engine.KartEngine]의 사용을 권장하나,
 * 직접 modifier 값을 얻어야 하는 경우에 사용할 수 있습니다.
 *
 * **주의**: 어트리뷰트 modifier는 기술적인 한계로 1틱 정도씩 밀릴 수 있기 때문에 틱 단위 정확성이 필요하면 다른 방식을 찾는 것을 권장드립니다.
 */
object KnownAttrModId {
    // 엔진 구분
    /**
     * 유저가 선택한 카트 엔진의 종류 정보를 나타내는 어트리뷰트 modifier입니다.
     * @see io.github.oni0nfr1.skid.client.api.engine.KartEngine.Type
     */
    @JvmField val KART_ENGINE: ResourceLocation = ResourceLocation.withDefaultNamespace("kart-engine")
    /**
     * 유저가 탑승 카트 엔진의 종류 정보를 나타내는 어트리뷰트 modifier입니다.
     * @see io.github.oni0nfr1.skid.client.api.engine.KartEngine.Type
     */
    @JvmField val KART_ENGINE_REAL: ResourceLocation = ResourceLocation.withDefaultNamespace("kart-engine-real")

    // 랩 수 관련
    /**
     * 유저가 레이스를 진행 중일 경우 그 트랙의 총 랩 수를 제공하는 어트리뷰트 modifier입니다.
     *
     * 레이스 중인 상태가 아닐 때의 값은 유효한 값이라고 볼 수 없습니다.
     */
    @JvmField val MAX_LAP: ResourceLocation = ResourceLocation.withDefaultNamespace("max-lap")
    /**
     * 유저가 레이스를 진행 중일 경우 유저가 돌고 있는 현재 랩 수를 제공하는 어트리뷰트 modifier입니다.
     *
     * 레이스 중인 상태가 아닐 때의 값은 유효한 값이라고 볼 수 없습니다.
     */
    @JvmField val CURRENT_LAP: ResourceLocation = ResourceLocation.withDefaultNamespace("current-lap")

    // 순간부스터 상태
    /**
     * 유저가 탄 카트에 순간 부스터 기능이 활성화되어 있는지를 제공하는 어트리뷰터 modifier입니다.
     *
     * 카트바디 종류에 의존하여 순간 부스터 기능이 덮어씌워질 때만 켜지는 값이 아니라, A2엔진 같이 일반적인 순간부스터 활성화 시에도 값이 1.0이 됩니다.
     */
    @JvmField val FORCE_INSTANT_BOOST: ResourceLocation = ResourceLocation.withDefaultNamespace("force-instant-boost")

    /**
     * 유저가 탄 카트의 순간 부스터의 발동 준비가 되었는지 여부를 제공하는 어트리뷰트 modifier입니다.
     *
     * - 발동 준비가 되지 않음: 0.0
     * - 발동 준비됨: 1.0
     * - 이번 틱에 발동이 처음 준비됨: 2.0
     *
     * **주의**: 어트리뷰트 modifier는 기술적인 한계로 1틱 정도씩 밀릴 수 있기 때문에 틱 단위 정확성이 필요하면 다른 방식을 찾는 것을 권장드립니다.
     */
    @JvmField val ACTIVE_INSTANT_BOOST: ResourceLocation = ResourceLocation.withDefaultNamespace("active-instant-boost")

    // 정규 카트 상태
    @JvmField val IS_DRIFTING: ResourceLocation = ResourceLocation.withDefaultNamespace("is-drifting")
    @JvmField val BOOST_STATE: ResourceLocation = ResourceLocation.withDefaultNamespace("boost-state")
    @JvmField val KART_MAX_BOOST_COUNT: ResourceLocation = ResourceLocation.withDefaultNamespace("kartmaxboostcount")
    @JvmField val DRAFT_STATE: ResourceLocation = ResourceLocation.withDefaultNamespace("draft-state")

    // 특수 설정
    @JvmField val KART_PERFORMANCE_LIMIT_LEVEL: ResourceLocation = ResourceLocation.withDefaultNamespace("kart-performance-limit-level")
    @JvmField val KART_TIRE: ResourceLocation = ResourceLocation.withDefaultNamespace("kart-tire")

    // deprecated
    @Deprecated("최신 마크라이더 데이터팩에서 사용되지 않는 값입니다.", replaceWith = ReplaceWith("BOOST_STATE"))
    @JvmField val DUALBOOST_STATE: ResourceLocation = ResourceLocation.withDefaultNamespace("dualboost-state")
    @Deprecated("최신 마크라이더 데이터팩에서 사용되지 않는 값입니다.", replaceWith = ReplaceWith("BOOST_STATE"))
    @JvmField val IS_BOOSTING: ResourceLocation = ResourceLocation.withDefaultNamespace("is-boosting")
}
