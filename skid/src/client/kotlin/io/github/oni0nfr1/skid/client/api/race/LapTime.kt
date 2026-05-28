package io.github.oni0nfr1.skid.client.api.race

/**
 * 랩타임을 나타내는 스냅샷 객체입니다.
 */
interface LapTime {
    val minutes: Int
    val seconds: Int
    val milliseconds: Int

    /**
     * 랩타임의 전체 정보를
     * MM:SS.mmm 형태의 문자열로 반환합니다.
     */
    fun format(): String

    companion object {
        /**
         * 랩타임 파서 객체입니다.
         *
         * parse()를 호출하여 문자열을 LapTime으로 파싱할 수 있습니다.
         */
        @JvmStatic
        val parser: LaptimeParser = TODO()
    }

    /**
     * 랩타임 문자열을 [LapTime]으로 파싱해 주는 파서입니다.
     */
    interface LaptimeParser {
        /**
         * @return MM:SS.mmm 형태의 문자열을 [LapTime]으로 파싱한 결과. 실패할 경우 `null`
         */
        fun parse(time: String): LapTime?
    }
}