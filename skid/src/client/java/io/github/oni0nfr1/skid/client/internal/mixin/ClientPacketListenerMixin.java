package io.github.oni0nfr1.skid.client.internal.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.oni0nfr1.skid.client.internal.events.KartMountMixinHandler;
import io.github.oni0nfr1.skid.client.internal.events.KartSummonMixinHandler;
import io.github.oni0nfr1.skid.client.internal.events.KartTachometerMixinHandler;
import io.github.oni0nfr1.skid.client.internal.events.KartAttrMixinHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(method = "handleSetEntityPassengersPacket", at = @At("TAIL"))
    private void onHandleSetEntityPassengersPacket(ClientboundSetPassengersPacket packet, CallbackInfo ci) {
        KartMountMixinHandler.onEntityMountPacket(packet, ci);
    }

    @Inject(method = "method_64896", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;removeEntity(ILnet/minecraft/world/entity/Entity$RemovalReason;)V"), require = 1)
    private void onHandleRemoveEntitiesPacket(int entityId, CallbackInfo ci) {
        KartMountMixinHandler.beforeEntityRemoveByPacket(entityId, ci);
        KartSummonMixinHandler.beforeRemoveEntityByPacket(entityId, ci);
    }

    @Inject(method = "setActionBarText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"), require = 1, cancellable = true)
    private void onSetActionBarText(ClientboundSetActionBarTextPacket packet, CallbackInfo ci) {
        KartTachometerMixinHandler.onSetActionbarPacket(packet, ci);
    }

    @Inject(method = "handleAddEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;postAddEntitySoundInstance(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER), require = 1)
    private void onHandleAddEntityPacket(CallbackInfo ci, @Local Entity entity) {
        if (entity == null) return;
        KartSummonMixinHandler.onAddEntityPacket(entity, ci);
    }

    @Inject(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;setBaseValue(D)V"))
    private void onHandleUpdateAttributes(CallbackInfo ci, @Local ClientboundUpdateAttributesPacket.AttributeSnapshot attributeSnapshot, @Local Entity entity) {
        KartAttrMixinHandler.onUpdateAttrPacket(entity, attributeSnapshot);
    }

    @Inject(method = "handleUpdateAttributes", at = @At("TAIL"))
    private void afterHandleUpdateAttributes(ClientboundUpdateAttributesPacket packet, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity entity = level.getEntity(packet.getEntityId());
        if (entity == null) return;

        KartMountMixinHandler.onFirstAttrUpdateAfterMount(entity);
        KartMountMixinHandler.onFirstAttrUpdateAfterSpectate(entity);
    }

    /*
    TODO
    믹스인 1:
        패킷 감지 후 메인 스레드에서 objective 주입 직전에 레이스 종료 조건 확인
        레이스 종료 판정이 나면 적절한 처리 후 종료 이벤트 호출

    믹스인 2:
        패킷 감지 후 메인 스레드에서 objective 주입 직후 레이스 시작 조건 확인
        레이스 시작 판정이 나면 적절한 처리 후 시작 이벤트 호출
     */
}
