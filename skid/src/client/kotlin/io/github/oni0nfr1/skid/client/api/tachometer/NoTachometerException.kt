package io.github.oni0nfr1.skid.client.api.tachometer

class NoTachometerException : Exception("tried to access tachometer which is not alive")