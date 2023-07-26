package com.craftworks.pearclient.gui.panel;

import java.awt.Panel;
import java.io.IOException;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.gui.panel.impl.ModulePanel;
import com.craftworks.pearclient.nofication.NotificationManager;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class ClickGUI extends GuiScreen {

	public ModulePanel modulePanel;

    public ClickGUI() {
    	modulePanel = new ModulePanel(this.width - 440, this.height - 250 , this.width, this.height);
    }

    @Override
    public void initGui() {
        super.initGui();
        modulePanel = new ModulePanel(this.width - 440, this.height - 250 , this.width, this.height);
        mc.entityRenderer.loadShader(new ResourceLocation("pearclient/shaders/blur.json"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    	modulePanel.draw(mouseX, mouseY);
    	NotificationManager.render();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        mc.entityRenderer.stopUseShader();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        modulePanel.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
	
}
