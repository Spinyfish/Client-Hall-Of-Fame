package me.eldodebug.soar.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiLanguage.class)
public class MixinGuiLanguage extends GuiScreen{

    @Override
    public void onGuiClosed() {
        mc.ingameGUI.getChatGUI().refreshChat();
    }
}
