package io.github.oni0nfr1.skid.client.api.engine

/**
 * 카트 엔진의 용도를 구분합니다.
 */
enum class EngineKind {
    /** 카트라이더: 마인크래프트의 공식 밸런스 체계에 따라 제공되는 엔진입니다. */
    NORMAL,

    /** 공식 밸런스 체계를 고려하지 않고 별도로 제작된 비공식 엔진입니다. */
    DUMMY,
}
