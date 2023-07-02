package me.eldodebug.soar.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;

@Mixin(ServerListEntryNormal.class)
public abstract class MixinServerListEntryNormal {
	
    @Shadow 
    
    protected abstract void prepareServerIcon();
    @Shadow 
    @Final
    private ServerData server;

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ServerListEntryNormal;prepareServerIcon()V"))
    private void resolveCrash(ServerListEntryNormal serverListEntryNormal) {
        try {
            prepareServerIcon();
        } catch (Exception e) {
            server.setBase64EncodedIconData(null);
        }
    }
}
