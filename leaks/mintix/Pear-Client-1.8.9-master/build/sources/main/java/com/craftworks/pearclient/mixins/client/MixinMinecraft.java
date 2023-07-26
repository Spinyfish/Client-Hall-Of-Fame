package com.craftworks.pearclient.mixins.client;

import com.craftworks.pearclient.PearClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "startGame", at = @At("RETURN"))
    private void injectPearClientStartup(CallbackInfo callbackInfo) {
        PearClient.instance.onStartup();
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    private void injectPearClientShutdown(CallbackInfo callbackInfo) {
        PearClient.instance.onShutdown();
    }

    @ModifyArg(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
    private String changeDisplayTitle(String title) {
        return PearClient.instance.name;
    }
}
