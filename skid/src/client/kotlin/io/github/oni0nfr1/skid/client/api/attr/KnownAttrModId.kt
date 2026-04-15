package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.resources.ResourceLocation

object KnownAttrModId {
    // 엔진 구분
    @JvmField val KART_ENGINE: ResourceLocation = ResourceLocation.withDefaultNamespace("kart-engine")
    @JvmField val KART_ENGINE_REAL: ResourceLocation = ResourceLocation.withDefaultNamespace("kart-engine-real")

    // 랩 수 관련
    @JvmField val MAX_LAP: ResourceLocation = ResourceLocation.withDefaultNamespace("max-lap")
    @JvmField val CURRENT_LAP: ResourceLocation = ResourceLocation.withDefaultNamespace("current-lap")

    // 순간부스터 상태
    @JvmField val FORCE_INSTANT_BOOST: ResourceLocation = ResourceLocation.withDefaultNamespace("force-instant-boot")
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
