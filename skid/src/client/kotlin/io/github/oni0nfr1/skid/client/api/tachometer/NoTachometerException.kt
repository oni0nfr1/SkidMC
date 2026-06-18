package io.github.oni0nfr1.skid.client.api.tachometer

/** 제거되어 더 이상 유효하지 않은 [KartTachometer] 접근을 나타내는 예외입니다. */
class NoTachometerException : Exception("tried to access tachometer which is not alive")
