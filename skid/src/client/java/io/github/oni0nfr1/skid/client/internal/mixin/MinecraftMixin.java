package io.github.oni0nfr1.skid.client.internal.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.oni0nfr1.skid.client.internal.events.KartMountMixinHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
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
    @Shadow @Nullable public ClientLevel level;

    @Inject(method = "setLevel", at = @At("HEAD"), require = 1)
    private void beforeSetLevel(ClientLevel newLevel, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        if (this.level != null && this.level != newLevel) {
            KartMountMixinHandler.teardownTrackedKarts();
        }
    }

    @Inject(method = "clearClientLevel", at = @At("HEAD"), require = 1)
    private void beforeClearClientLevel(Screen screen, CallbackInfo ci) {
        KartMountMixinHandler.teardownTrackedKarts();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"), require = 1)
    private void beforeDisconnect(Screen screen, boolean transferState, CallbackInfo ci) {
        KartMountMixinHandler.teardownTrackedKarts();
    }

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
