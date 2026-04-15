package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.attr.AttrModifierSnapshot
import io.github.oni0nfr1.skid.client.api.attr.KnownAttrModId
import io.github.oni0nfr1.skid.client.internal.utils.createEvent
import net.fabricmc.fabric.api.event.Event
import net.minecraft.core.Holder
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

/**
 * 마크라이더에는 서버에서 클라이언트로 플레이어의 주행 세부 정보를 플레이어의 Attribute로 전송하는 API를 제공합니다.
 * 이 이벤트들에서는 해당 패킷들의 수신 내용을 후킹할 수 있습니다.
 *
 * 모든 어트리뷰트 이벤트는 렌더 스레드에서 호출됩니다.
 */
object RiderAttrEvents {

    /**
     * 어트리뷰트가 갱신될 때 호출됩니다. 어트리뷰트 전체 값을 한 번에 얻고자 할 때 사용하세요.
     *
     * 다른 일반 어트리뷰트 이벤트들보다 먼저 호출됩니다.
     */
    @JvmField
    val RIDER_META_ATTR = createEvent { listeners ->
        AttrPacketCallback { player, base, modifiers ->
            for (listener in listeners) {
                listener.onPacket(player, base, modifiers)
            }
        }
    }

    // 엔진 구분
    @JvmField val KART_ENGINE = attrEvent(KnownAttrModId.KART_ENGINE)
    @JvmField val KART_ENGINE_REAL = attrEvent(KnownAttrModId.KART_ENGINE_REAL)

    // 랩 수 관련
    @JvmField val MAX_LAP = attrEvent(KnownAttrModId.MAX_LAP)
    @JvmField val CURRENT_LAP = attrEvent(KnownAttrModId.CURRENT_LAP)

    // 순간부스터 상태
    @JvmField val FORCE_INSTANT_BOOST = attrEvent(KnownAttrModId.FORCE_INSTANT_BOOST)
    @JvmField val ACTIVE_INSTANT_BOOST = attrEvent(KnownAttrModId.ACTIVE_INSTANT_BOOST)

    // 정규 카트 상태
    @JvmField val IS_DRIFTING = attrEvent(KnownAttrModId.IS_DRIFTING)
    @JvmField val BOOST_STATE = attrEvent(KnownAttrModId.BOOST_STATE)
    @JvmField val KART_MAX_BOOST_COUNT = attrEvent(KnownAttrModId.KART_MAX_BOOST_COUNT)
    @JvmField val DRAFT_STATE = attrEvent(KnownAttrModId.DRAFT_STATE)

    // 특수 설정
    @JvmField val KART_PERFORMANCE_LIMIT_LEVEL = attrEvent(KnownAttrModId.KART_PERFORMANCE_LIMIT_LEVEL)
    @JvmField val KART_TIRE = attrEvent(KnownAttrModId.KART_TIRE)

    // deprecated
    @Deprecated("최신 마크라이더 데이터팩에서 사용되지 않는 값입니다.", replaceWith = ReplaceWith("BOOST_STATE"))
    @JvmField val DUALBOOST_STATE = attrEvent(KnownAttrModId.DUALBOOST_STATE)
    @Deprecated("최신 마크라이더 데이터팩에서 사용되지 않는 값입니다.", replaceWith = ReplaceWith("BOOST_STATE"))
    @JvmField val IS_BOOSTING = attrEvent(KnownAttrModId.IS_BOOSTING)



    fun interface RiderAttrCallback {
        fun onAttrChange(entity: Player, value: Double)
    }

    fun interface AttrPacketCallback {
        fun onPacket(player: Player, base: Double, modifiers: AttrModifierSnapshot)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun attrEvent(key: ResourceLocation): Event<RiderAttrCallback> {
        val event = createEvent { listeners ->
            RiderAttrCallback { entity, value ->
                for (listener in listeners) {
                    listener.onAttrChange(entity, value)
                }
            }
        }
        MixinHandler.attrEventRegistry[key] = event
        return event
    }

    internal object MixinHandler {
        val RIDER_META_ATTR_KEY: Holder<Attribute> = Attributes.EXPLOSION_KNOCKBACK_RESISTANCE
        val attrEventRegistry = mutableMapOf<ResourceLocation, Event<RiderAttrCallback>>()

        /**
         * [ClientboundUpdateAttributesPacket]을 수신하여 그 안의 [ClientboundUpdateAttributesPacket.AttributeSnapshot]을 읽을 때,
         * 해당 스냅샷이 대상 엔티티에 대해 유효한지가 확인된 상황에서 엔티티에 적용 직전에 호출됩니다.
         *
         * @see io.github.oni0nfr1.skid.client.mixin.ClientPacketListenerMixin.onHandleUpdateAttributes
         */
        @JvmStatic
        fun onUpdateAttrPacket(entity: Entity, snapshot: ClientboundUpdateAttributesPacket.AttributeSnapshot) {
            if (entity !is Player) return
            if (snapshot.attribute != RIDER_META_ATTR_KEY) return
            val attrInstance =
                entity.attributes.getInstance(RIDER_META_ATTR_KEY)!! // Mixin 위치상으로 이미 AttributeInstance가 null이 아님이 확인된 후임.

            val base = snapshot.base
            val modifiers = AttrModifierSnapshot(snapshot)
            RIDER_META_ATTR.invoker().onPacket(entity, base, modifiers)

            modifiers.forEach { (key, value) ->
                // 현재는 Mixin 위치상으로 바닐라 modifier 적용 직전이므로 엔티티에는 아직 modifier가 적용되지 않음.
                // 즉 엔티티의 attrInstance에 있는 값들은 이전의 값들임.
                val prevValue = attrInstance.getModifier(key)?.amount
                if (value != prevValue) attrEventRegistry[key]?.invoker()?.onAttrChange(entity, value)
            }
        }
    }
}