package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.kart.Kart
import io.github.oni0nfr1.skid.client.api.kart.KartManager
import io.github.oni0nfr1.skid.client.api.kart.subject
import io.github.oni0nfr1.skid.client.internal.tachometer.TachometerManager
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import io.github.oni0nfr1.skid.client.internal.utils.MCClient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * # NOTE
 * SkidMC의 액션바 처리 파이프라인은 **서버**로부터 액션바 패킷이 도착했을 때만 작동합니다.
 *
 * 따라서 타 클라이언트 모드에서 액션바 출력 메서드를 호출하더라도 SkidMC에서는 그것을 처리하지 않습니다.
 */
object KartTachometerEvents {

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 속도값이 파싱될 경우 호출됩니다.
     *
     * [Kart.velocity]와 달리 HUD 표시 등의 시각적 목적으로 사용하는 것을 권장합니다.
     *
     * 렌더 스레드에서 호출됩니다.
     * @see Kart.velocity
     */
    @Suppress("UNUSED")
    @JvmField
    val SPEED = createEvent { listeners ->
        KartSpeedCallback { speed ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onSpeedUpdate(speed)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 부스터 개수 값이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val NITRO = createEvent { listeners ->
        KartNitroCallback { nitro ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onNitroUpdate(nitro)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 게이지 값이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val GAUGE = createEvent { listeners ->
        KartGaugeCallback { gauge ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onGaugeUpdate(gauge)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 기어류 엔진 RPM 값이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val RPM = createEvent { listeners ->
        KartRpmCallback { rpm ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onRpmUpdate(rpm)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 기어류 엔진 기어 단수가 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val GEAR = createEvent { listeners ->
        KartGearCallback { rpm ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onGearUpdate(rpm)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 F1 엔진의 ERS 충전량이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val ERS = createEvent { listeners ->
        KartErsCallback { ers ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onErsUpdate(ers)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    /**
     * 클라이언트가 마크라이더 타코미터 텍스트를 수신하고, 그곳에서 마리오카트 엔진의 터보 게이지 충전량이 파싱될 경우 호출됩니다.
     *
     * 렌더 스레드에서 호출됩니다.
     */
    @Suppress("UNUSED")
    @JvmField
    val MK_GAUGE = createEvent { listeners ->
        MarioKartGaugeCallback { gauge ->
            var result = Result.SHOW
            for (listener in listeners) {
                val listenerResult = listener.onGaugeUpdate(gauge)
                result = if (listenerResult == Result.BLOCK) Result.BLOCK else result
            }
            result
        }
    }

    fun interface KartSpeedCallback {
        fun onSpeedUpdate(speed: Double): Result
    }

    fun interface KartNitroCallback {
        fun onNitroUpdate(nitro: Int): Result
    }

    fun interface KartGaugeCallback {
        fun onGaugeUpdate(gauge: Double): Result
    }

    fun interface MarioKartGaugeCallback {
        fun onGaugeUpdate(gauge: Double): Result
    }

    fun interface KartRpmCallback {
        fun onRpmUpdate(rpm: Double): Result
    }

    fun interface KartGearCallback {
        fun onGearUpdate(gear: Int): Result
    }

    fun interface KartErsCallback {
        fun onErsUpdate(ers: Int): Result
    }

    /**
     * 액션바 이벤트의 처리 결과 반환 타입입니다.
     * - [SHOW]: 액션바 메시지를 정상적으로 처리합니다.
     * - [BLOCK]: 액션바 메시지가 화면에서 가려집니다. (이후 이벤트 리스너들은 정상적으로 작동합니다.)
     */
    enum class Result {
        SHOW, BLOCK;

        companion object {
            fun finalize(vararg results: Result): Result {
                results.forEach {
                    if (it == BLOCK) return BLOCK
                }
                return SHOW
            }
        }
    }

    internal object MixinHandler {
        private val client: Minecraft by MCClient

        /**
         * [ClientboundSetActionBarTextPacket]을 수신하여 [Gui.setOverlayMessage]를 호출하기 직전 호출됩니다.
         *
         * 렌더 스레드에서 호출됩니다.
         *
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.onSetActionBarText
         */
        @JvmStatic
        fun onSetActionbarPacket(packet: ClientboundSetActionBarTextPacket, ci: CallbackInfo) {
            val text = packet.text
            val subject = client.player?.subject as? Player ?: run {
                TachometerManager.clear()
                return
            }
            val kart = KartManager.getKart(subject)?.handle ?: run {
                TachometerManager.clear()
                return
            }
            val engine = kart.engine ?: run {
                TachometerManager.clear()
                return
            }

            val result = TachometerManager.handleActionbar(kart, engine, text)
            if (result == Result.BLOCK) ci.cancel()
        }
    }
}
