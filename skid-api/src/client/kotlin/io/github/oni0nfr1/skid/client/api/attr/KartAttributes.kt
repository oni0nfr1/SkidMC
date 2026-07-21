package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.core.Holder
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.Attributes

/** SkidMC가 카트 정보를 전송하는 데 사용하는 Minecraft 어트리뷰트를 제공합니다. */
object KartAttributes {
    /** 카트 정보 modifier를 담는 어트리뷰트입니다. */
    @JvmField
    val KART_INFO_ATTR_KEY: Holder<Attribute> = Attributes.ARMOR
}
