package io.github.oni0nfr1.skid.client.api.attr

import net.minecraft.resources.ResourceLocation
import java.util.Collections
import java.util.LinkedHashMap

/**
 * 카트 정보 어트리뷰트의 modifier ID와 값을 불변 Map 형태로 제공합니다.
 *
 * 생성 시 전달된 [values]를 복사하므로 이후 원본 Map을 변경해도 이 스냅샷에는 영향을
 * 주지 않습니다. 이 스냅샷은 modifier 값만 저장하며 어트리뷰트 기본값은 포함하지 않습니다.
 *
 * @param values modifier ID와 값
 */
class AttrModifierSnapshot(values: Map<ResourceLocation, Double>) :
    Map<ResourceLocation, Double> by immutableCopy(values) {

    private companion object {
        fun immutableCopy(
            values: Map<ResourceLocation, Double>,
        ): Map<ResourceLocation, Double> {
            return Collections.unmodifiableMap(LinkedHashMap(values))
        }
    }

    /**
     * [namespace]와 [path]로 구성된 modifier ID의 값을 반환합니다.
     *
     * @param namespace modifier ID의 네임스페이스
     * @param path modifier ID의 경로
     * @return 해당 ID의 값, 존재하지 않으면 `null`
     */
    operator fun get(namespace: String, path: String): Double? {
        val key = ResourceLocation.fromNamespaceAndPath(namespace, path)
        return this[key]
    }

    /**
     * `minecraft` 네임스페이스에 속한 [path]의 modifier 값을 반환합니다.
     *
     * @param path modifier ID의 경로
     * @return 해당 ID의 값, 존재하지 않으면 `null`
     */
    operator fun get(path: String): Double? {
        val key = ResourceLocation.withDefaultNamespace(path)
        return this[key]
    }
}
