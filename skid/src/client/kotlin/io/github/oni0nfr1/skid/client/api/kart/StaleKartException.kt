package io.github.oni0nfr1.skid.client.api.kart

/** 제거되어 더 이상 유효하지 않은 [Kart]의 상태에 접근했을 때 발생합니다. */
class StaleKartException : Exception("tried to access kart which is not alive")
