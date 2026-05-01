package io.github.oni0nfr1.skid.client.internal.events

import io.github.oni0nfr1.skid.client.api.attr.AttrModifierSnapshot
import io.github.oni0nfr1.skid.client.api.events.RiderAttrEvents
import io.github.oni0nfr1.skid.client.api.events.RiderAttrEvents.RIDER_META_ATTR
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import kotlin.collections.component1
import kotlin.collections.component2

object RiderAttrMixinHandler {

    /**
     * [ClientboundUpdateAttributesPacket]을 수신하여 그 안의 [ClientboundUpdateAttributesPacket.AttributeSnapshot]을 읽을 때,
     * 해당 스냅샷이 대상 엔티티에 대해 유효한지가 확인된 상황에서 엔티티에 적용 직전에 호출됩니다.
     *
     * @see io.github.oni0nfr1.skid.client.internal.mixin.ClientPacketListenerMixin.onHandleUpdateAttributes
     */
    @JvmStatic
    fun onUpdateAttrPacket(entity: Entity, snapshot: ClientboundUpdateAttributesPacket.AttributeSnapshot) {
        if (entity !is Player) return
        if (snapshot.attribute != RiderAttrEvents.RIDER_META_ATTR_KEY) return
        val attrInstance = entity.attributes.getInstance(RiderAttrEvents.RIDER_META_ATTR_KEY)!! // Mixin 위치상으로 이미 AttributeInstance가 null이 아님이 확인된 후임.

        val base = snapshot.base
        val modifiers = AttrModifierSnapshot(snapshot)
        RIDER_META_ATTR.invoker().onPacket(entity, base, modifiers)

        modifiers.forEach { (key, value) ->
            // 현재는 Mixin 위치상으로 바닐라 modifier 적용 직전이므로 엔티티에는 아직 modifier가 적용되지 않음.
            // 즉 엔티티의 attrInstance에 있는 값들은 이전의 값들임.
            val prevValue = attrInstance.getModifier(key)?.amount
            if (value != prevValue) RiderAttrEvents.attrEventRegistry[key]?.invoker()?.onAttrChange(entity, value)
        }
    }
}