package io.github.oni0nfr1.skid.client.internal.mixin;

import io.github.oni0nfr1.skid.client.internal.events.KartMountMixinHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow @Nullable public Entity cameraEntity;

    @Inject(method = "setCameraEntity", at = @At(value = "HEAD"))
    private void onSetCameraEntity(Entity newCamera, CallbackInfo ci) {
        @Nullable LocalPlayer player = this.player;
        @Nullable Entity prevCamera = this.cameraEntity;
        if (player == null || newCamera == null || prevCamera == null) return;
        KartMountMixinHandler.onSpectateTargetChange(player, prevCamera, newCamera);
    }
}
