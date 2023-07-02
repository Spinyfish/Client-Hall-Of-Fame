package me.eldodebug.soar.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import me.eldodebug.soar.gui.GuiFixConnecting;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;

@Mixin(GuiMultiplayer.class)
public class MixinGuiMultiplayer extends GuiScreen{

	@Overwrite
    private void connectToServer(ServerData server){
        this.mc.displayGuiScreen(new GuiFixConnecting(this, this.mc, server));
    }
}
