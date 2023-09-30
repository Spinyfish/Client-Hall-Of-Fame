package de.verschwiegener.atero.ui.multiplayer;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

public class GUIProxy2 extends GuiScreen {
    
    GuiMultiplayer parent;
    
    public GUIProxy2(GuiMultiplayer parent) {
	this.parent = parent;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	parent.drawScreen(mouseX, mouseY, partialTicks);
	
    }

}
