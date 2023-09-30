package me.r.touchgrass.injection.mixins.client;

import com.darkmagician6.eventapi.EventManager;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.events.EventKey;
import me.r.touchgrass.events.EventMouseClick;
import me.r.touchgrass.events.EventTick;
import me.r.touchgrass.file.files.*;
import me.r.touchgrass.file.files.ClickGuiConfig;
import me.r.touchgrass.file.files.ModuleConfig;
import me.r.touchgrass.file.files.SettingsConfig;
import me.r.touchgrass.file.files.deprecated.*;
import me.r.touchgrass.file.files.deprecated.*;
import me.r.touchgrass.injection.interfaces.IMixinMinecraft;
import me.r.touchgrass.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
@SideOnly(Side.CLIENT)
public class MixinMinecraft implements IMixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;

    @Shadow
    @Mutable
    public Session session;

    private long lastFrame;

    public MixinMinecraft() {
        this.lastFrame = this.getTime();
    }

    public long getTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void minecraftConstructor(GameConfiguration gameConfig, CallbackInfo ci) {
        new touchgrass();
    }

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
    private void startGame(CallbackInfo ci) {
        touchgrass.getClient().startClient();
        ModuleConfig moduleConfig = new ModuleConfig();
        SettingsConfig settingsConfig = new SettingsConfig();
        ClickGuiConfig clickGuiConfig = new ClickGuiConfig();
        if(touchgrass.getClient().hasNewFiles) {
            moduleConfig.loadConfig();
            settingsConfig.loadConfig();
            clickGuiConfig.loadConfig();
        } else {
            KeybindFile.loadKeybinds();
            VisibleFile.loadState();
            ModuleFile.loadModules();
            ClickGuiFile.loadClickGui();
            SettingsButtonFile.loadState();
            SettingsComboBoxFile.loadState();
            SettingsSliderFile.loadState();
            TextFile.loadState();
        }
    }

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoopDeltaTime(CallbackInfo ci) {
        long currentTime = this.getTime();
        int deltaTime = (int)(currentTime - this.lastFrame);
        this.lastFrame = currentTime;
        Utils.deltaTime = deltaTime;
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && currentScreen == null && !touchgrass.getClient().panic) {
            EventManager.call(new EventKey(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
        }
    }

    @Inject(method = "clickMouse", at = @At("HEAD"))
    public void clickMouse(CallbackInfo ci) {
        if(!touchgrass.getClient().panic) {
            EventMouseClick e = new EventMouseClick();
            EventManager.register(e);
        }
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        if(!touchgrass.getClient().panic) {
            touchgrass.getClient().stopClient();
        }
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;joinPlayerCounter:I", shift = At.Shift.BEFORE))
    private void onTick(final CallbackInfo callbackInfo) {
        if(!touchgrass.getClient().panic) {
            EventTick e = new EventTick();
            EventManager.call(e);
        }
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }


}
