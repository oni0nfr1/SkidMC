package io.github.oni0nfr1.skidTest.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    // for some testing but empty for now
}
