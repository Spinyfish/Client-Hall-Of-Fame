package com.craftworks.pearclient.gui.ingame;

import java.io.IOException;

import net.arikia.dev.drpc.callbacks.DisconnectedCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;

public class GuiMultiplayerIngame extends GuiMultiplayer {
	
	public GuiMultiplayerIngame() {
		super(null);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		
		if(button.id == 1 || button.id == 4) {
			disconnect();
		}
		
		super.actionPerformed(button);
	}
	
	@Override
	public void connectToSelected() {
		disconnect();
		super.connectToSelected();
	}

	private void disconnect() {
		if(mc.theWorld != null) {
			
			mc.theWorld.sendQuittingDisconnectingPacket();
			
			mc.loadWorld(null);
			
			mc.displayGuiScreen(null);
			
			parentScreen = null;
		}
	}

}
