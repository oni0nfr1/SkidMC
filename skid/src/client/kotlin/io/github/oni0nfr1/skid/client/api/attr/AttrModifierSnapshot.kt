package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket.AttributeSnapshot
import net.minecraft.resources.ResourceLocation
import java.util.TreeMap

/**
 * 라이더 메타데이터 어트리뷰트의 modifier ID와 값을 조회하기 쉬운 형태로 제공합니다.
 *
 * 기존의 Minecraft의 [AttributeSnapshot]은 modifiers가 키/값을 한 번에 담은 Modifier들의 Collection으로,
 * 원하는 키에 대응하는 값을 빠르게 찾아내는 데 적합하지 않습니다.
 *
 * 따라서 SkidMC에서는 modifier들의 ID를 키, 해당하는 실숫값을 값으로 하는 트리맵 기반의 클래스를 제공합니다.
 *
 * 이 스냅샷은 modifier 값만 저장하며 [AttributeSnapshot.base]는 포함하지 않습니다.
 *
 * @param snapshot 변환할 Minecraft 어트리뷰트 스냅샷
 */
class AttrModifierSnapshot(snapshot: AttributeSnapshot)
    : Map<ResourceLocation, Double> by createDelegate(snapshot)
{
    private companion object {
        fun createDelegate(snapshot: AttributeSnapshot): TreeMap<ResourceLocation, Double> {
            return TreeMap<ResourceLocation, Double>().apply {
                snapshot.modifiers.forEach { this[it.id] = it.amount }
            }
        }
    }

    /**
     * [key]에 해당하는 modifier 값을 반환합니다.
     *
     * @param key 조회할 modifier ID
     * @return 해당 ID의 값, 존재하지 않으면 `null`
     */
    override fun get(key: ResourceLocation): Double? = this[key]

    /**
     * [namespace]와 [path]로 구성된 modifier ID의 값을 반환합니다.
     *
     * @param namespace modifier ID의 네임스페이스
     * @param path modifier ID의 경로
     * @return 해당 ID의 값, 존재하지 않으면 `null`
     */
    fun get(namespace: String, path: String): Double? {
        val key = ResourceLocation.fromNamespaceAndPath(namespace, path)
        return this[key]
    }

    /**
     * `minecraft` 네임스페이스에 속한 [path]의 modifier 값을 반환합니다.
     *
     * @param path modifier ID의 경로
     * @return 해당 ID의 값, 존재하지 않으면 `null`
     */
    fun get(path: String): Double? {
        val key = ResourceLocation.withDefaultNamespace(path)
        return this[key]
    }
}
