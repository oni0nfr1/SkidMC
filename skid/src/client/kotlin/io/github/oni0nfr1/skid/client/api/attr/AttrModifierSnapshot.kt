package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket.AttributeSnapshot
import net.minecraft.resources.ResourceLocation
import java.util.TreeMap

/**
 * [HashMap]을 이용하여 어트리뷰트의 modifier들의 키/값 쌍을 저장합니다.
 *
 * 기존의 Minecraft의 [AttributeSnapshot]은 modifiers가 키/값을 한 번에 담은 Modifier들의 Collection으로,
 * 원하는 키에 대응하는 값을 빠르게 찾아내는 데 적합하지 않습니다.
 *
 * 따라서 SkidMC에서는 modifier들의 ID를 키, 해당하는 실숫값을 값으로 하는 트리맵 기반의 클래스를 제공합니다.
 *
 * **주의: 이 객체는 modifier 값들만 저장하며, base 값은 담지 않습니다.**
 */
class AttrModifierSnapshot(snapshot: AttributeSnapshot): Map<ResourceLocation, Double> {
    private val modifiers = TreeMap<ResourceLocation, Double>()

    init {
        snapshot.modifiers.forEach {
            modifiers[it.id] = it.amount
        }
    }

    override val size get() = modifiers.size
    override val keys get() = modifiers.keys
    override val values get() = modifiers.values
    override val entries get() = modifiers.entries

    override fun isEmpty() = modifiers.isEmpty()

    override fun containsKey(key: ResourceLocation): Boolean = modifiers.containsKey(key)

    override fun containsValue(value: Double): Boolean = modifiers.containsValue(value)

    /**
     * 주어진 [ResourceLocation]에 대한 modifier 값을 가져옵니다.
     * @param key modifier의 ID 값
     */
    override fun get(key: ResourceLocation): Double? = modifiers[key]

    /**
     * 해당하는 네임스페이스와 키에 대한 modifier 값을 가져옵니다.
     * @param namespace modifier ID의 네임스페이스
     * @param path modifier ID의 이름
     */
    fun get(namespace: String, path: String): Double? {
        val key = ResourceLocation.fromNamespaceAndPath(namespace, path)
        return modifiers[key]
    }

    /**
     * minecraft 네임스페이스 중에서 주어진 키에 대한 modifier 값을 가져옵니다.
     * @param path modifier ID의 이름
     */
    fun get(path: String): Double? {
        val key = ResourceLocation.withDefaultNamespace(path)
        return modifiers[key]
    }
}
