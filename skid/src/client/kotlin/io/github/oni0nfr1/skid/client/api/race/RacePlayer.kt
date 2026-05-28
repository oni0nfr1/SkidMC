package io.github.oni0nfr1.skid.client.api.race

import io.github.oni0nfr1.skid.client.api.kart.KartRef
import net.minecraft.world.entity.player.Player

/**
 * 카트 멀티방에 있는 플레이어 한 명을 나타내는 스냅샷 객체입니다.
 */
interface RacePlayer {
    /**
     * 플레이어의 이름
     */
    val name: String

    /**
     * @return 해당하는 마인크래프트 객체, 서버 퇴장이나 서버 동기화 종료 등의 이유로 찾을 수 없는 경우 `null`
     */
    fun getPlayer(): Player?
}

/**
 * 카트 레이스의 참가자 한 명을 나타내는 스냅샷 객체입니다.
 */
interface RaceParticipant : RacePlayer {
    /**
     * 참가자의 팀
     *
     * 팀전이 아닐 경우 null입니다.
     */
    val team: KartRace.Team?

    /**
     * @return 해당하는 플레이어가 탑승한 카트 참조, 카트를 찾을 수 없을 경우 `null`
     */
    fun findKart(): KartRef?
}

/**
 * 카트 레이스의 관전자 한 명을 나타내는 스냅샷 객체입니다.
 */
interface RaceSpectator : RacePlayer {

}