package com.craftworks.pearclient.mixins.client;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.event.impl.EventClientTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.Sys;
import com.craftworks.pearclient.animation.DTime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "runTick", at = @At("RETURN"))
    public void runTick(CallbackInfo ci) {
        new EventClientTick().call();
    }

    long lastFrame = Sys.getTime();

    @Inject(method = "runGameLoop", at = @At("RETURN"))
    public void runGameLoop(CallbackInfo ci){
        long currentTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        int dTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        DTime.setDTime(dTime);
    }

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
