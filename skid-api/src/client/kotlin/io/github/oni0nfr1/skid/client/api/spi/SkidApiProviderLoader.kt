package io.github.oni0nfr1.skid.client.api.spi

import net.fabricmc.loader.api.FabricLoader

internal const val SKID_API_PROVIDER_ENTRYPOINT = "skid-api-provider"

internal object SkidApiProviderLoader {
    val provider: SkidApiProvider by lazy(::loadProvider)

    private fun loadProvider(): SkidApiProvider {
        val providers = FabricLoader.getInstance().getEntrypoints(
            SKID_API_PROVIDER_ENTRYPOINT,
            SkidApiProvider::class.java,
        )

        return when (providers.size) {
            1 -> providers.single()
            0 -> error(
                "SkidMC API provider is not installed: " +
                    "missing '$SKID_API_PROVIDER_ENTRYPOINT' entrypoint",
            )
            else -> error(
                "Multiple SkidMC API providers are installed for " +
                    "'$SKID_API_PROVIDER_ENTRYPOINT': ${providers.size}",
            )
        }
    }
}
