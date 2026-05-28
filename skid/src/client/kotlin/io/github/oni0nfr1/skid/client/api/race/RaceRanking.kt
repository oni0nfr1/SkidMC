package io.github.oni0nfr1.skid.client.api.race

/**
 * 레이스의 랭킹 상태를 나타내는 불변 스냅샷 객체입니다.
 */
interface RaceRanking {
    /**
     * 이 객체에 해당하는 레이스가 팀전인 경우 true, 아니면 false를 반환합니다.
     */
    val isTeamRace: Boolean

    /**
     * 랭킹을 리스트로 반환합니다.
     * 참가자들이 순위 순서대로 정렬되어 있습니다.
     */
    val rankTable: List<RaceParticipant>

    /**
     * @param player 순위를 찾고자 하는 경기 참가자
     * @return 참가자의 순위. 해당 레이스의 참가자가 아닐 경우 `null` 반환
     */
    fun playerRank(player: RaceParticipant): Int?

    /**
     * @param team 점수를 얻고자 하는 팀
     * @return 팀의 현재 점수. 현재 레이스가 팀전이 아닐 경우 `null`
     */
    fun teamScore(team: KartRace.Team): Int?
}