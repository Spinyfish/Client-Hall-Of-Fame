package me.eldodebug.soar.mixin.mixins.multiplayer;

import java.net.IDN;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.multiplayer.ServerAddress;

@Mixin(ServerAddress.class)
public class MixinServerAddress {

    @Shadow
    @Final
    private String ipAddress;
    
    @Overwrite
    public String getIP() {
        try {
            return IDN.toASCII(this.ipAddress);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
