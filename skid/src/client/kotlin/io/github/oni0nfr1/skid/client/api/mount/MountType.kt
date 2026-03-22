package io.github.oni0nfr1.skid.client.api.mount

import net.minecraft.world.entity.Entity

sealed interface MountType {
    class Dismounted : MountType
    class Mounted : MountType
    class Spectating(val camera: Entity) : MountType
}