package com.craftworks.pearclient.mixins.client.gui;

import com.craftworks.pearclient.gui.mainmenu.MainMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    @Inject(method = "initGui", at = @At("HEAD"))
    public void injectMainMenu(CallbackInfo callbackInfo) {
        Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
    }
}