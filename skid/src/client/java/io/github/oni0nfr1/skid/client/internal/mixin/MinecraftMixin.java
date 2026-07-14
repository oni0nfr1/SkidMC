package io.github.oni0nfr1.skid.client.internal.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.oni0nfr1.skid.client.internal.events.KartMountMixinHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow @Nullable public Entity cameraEntity;

    @WrapMethod(method = "setCameraEntity")
    private void onSetCameraEntity(Entity newCamera, Operation<Void> original) {
        @Nullable LocalPlayer player = this.player;
        @Nullable Entity prevCamera = this.cameraEntity;
        original.call(newCamera);

        @Nullable Entity currentCamera = this.cameraEntity;
        if (player == null || prevCamera == null || currentCamera == null) return;
        KartMountMixinHandler.afterSpectateTargetChange(player, prevCamera, currentCamera);
    }
}
