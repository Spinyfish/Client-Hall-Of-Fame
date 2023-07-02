package me.eldodebug.soar.mixin.mixins.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {

	@Shadow
    protected Minecraft mc;
    
	@Shadow
    public abstract void keyTyped(char typedChar, int keyCode);
    
	@Overwrite
    public void handleKeyboardInput() throws IOException {
        char c = Keyboard.getEventCharacter();
        
        if ((Keyboard.getEventKey() == 0 && c >= ' ') || Keyboard.getEventKeyState()) {
            this.keyTyped(c, Keyboard.getEventKey());
        }
        
        this.mc.dispatchKeypresses();
    }
}