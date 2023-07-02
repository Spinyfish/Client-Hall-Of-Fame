package me.eldodebug.soar.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import me.eldodebug.soar.management.events.impl.EventLoadWorld;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiDownloadTerrain.class)
public class MixinGuiDownloadTerrain extends GuiScreen{

	@Overwrite
	public void initGui() {
		this.buttonList.clear();
		EventLoadWorld event = new EventLoadWorld();
		event.call();
	}
}
