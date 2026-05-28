package io.github.oni0nfr1.skid.client.api.race

interface KartRace {
    // 게임 모드
    // XXEnabled는 기본이 true이며, XXMode는 기본이 false입니다.
    /**
     * 팀전 여부
     */
    val teamMode: Boolean

    /**
     * 고스트 모드 여부
     */
    val ghostMode: Boolean

    /**
     * 무한 부스터 모드 여부
     */
    val infiniteNitroMode: Boolean

    /**
     * 톡톡이 가속 모드 여부
     */
    val dragAccelMode: Boolean

    /**
     * 갓겜모드 여부
     */
    val chaoticCollisionMode: Boolean

    /**
     * 벽 충돌 페널티 활성화 여부
     */
    val wallCrashPenaltyMode: Boolean


    /**
     * 견인 가속 활성화 여부
     */
    val towAccelEnabled: Boolean

    /**
     * 드래프트 활성화 여부
     */
    val draftEnabled: Boolean

    /**
     * 레이스(멀티 방)에 존재하는 플레이어들의 리스트
     *
     * 리스트의 순서는 구현에 의존합니다.
     */
    val racePlayers: List<RacePlayer>

    /**
     * 레이스의 참여자들의 리스트
     *
     * 어떠한 기준으로의 정렬을 보장하지 않습니다.
     */
    val participants: List<RaceParticipant>

    /**
     * 레이스의 관전자들의 리스트
     *
     * 어떠한 기준으로의 정렬을 보장하지 않습니다.
     */
    val spectators: List<RaceSpectator>

    /**
     * @return 타임어택을 할 때와 기록이 달라질 만한 원인이 없을 경우 `true`, 아니면 `false`
     */
    fun isValidTimeAttack(): Boolean

    enum class Team {
        RED,
        BLUE,
    }
}