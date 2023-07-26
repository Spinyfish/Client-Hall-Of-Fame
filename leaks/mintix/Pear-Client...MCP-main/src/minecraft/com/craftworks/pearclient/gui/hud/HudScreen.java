package com.craftworks.pearclient.gui.hud;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.gui.panel.ClickGUI;
import com.craftworks.pearclient.hud.mods.HudMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class HudScreen extends GuiScreen {
	private HudMod dragging;
	private int dragX, dragY;
	
	@Override
	public void initGui() {
		super.initGui();
		mc.entityRenderer.loadShader(new ResourceLocation("pearclient/shaders/blur.json"));
		this.buttonList.add(new GuiButton(0, this.width / 2 - 50, this.height / 2 - 10, 100, 20, "ClickGUI"));
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		mc.entityRenderer.stopUseShader();
	}
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		ScaledResolution sr = new ScaledResolution(mc);
		
		for(HudMod m : PearClient.instance.modManager.getRenderModuleList()) {
			if (m.isToggled()) {
				m.onRenderDummy();
				Gui.drawRect(m.getX() - 2, m.getY() - 2, m.getX() + m.getWidth() + 2, m.getY() + m.getHeight() + 2, new Color(255,255,255,40).getRGB());
				if(m.isHovered(mouseX, mouseY)) {
					this.drawBoxAroundModule(m);
				}
				if(m == dragging) {
					m.setX(dragX + mouseX);
		            m.setY(dragY + mouseY);
				}
			}
		}
	}
	
	private void drawBoxAroundModule(HudMod m) {
		if(m.getWidth() != 0 || m.getHeight() != 0) {
			GL11.glPushMatrix();
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_LINE_SMOOTH);
	        GL11.glDisable(GL11.GL_DEPTH_TEST);
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glBlendFunc(770, 771);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glLineWidth(1);
	        GL11.glColor4f(255, 255, 255, 255);
	        
	        // top line
	        GL11.glBegin(2);
	        GL11.glVertex2d(m.getX() - 1.5, m.getY() - 1.5);
	        GL11.glVertex2d(m.getX() + m.getWidth() + 1.5, m.getY() - 1.5);
	        GL11.glEnd();
	        
	        // bottom line
	        GL11.glBegin(2);
	        GL11.glVertex2d(m.getX() - 1.5, m.getY() + m.getHeight() + 1.5);
	        GL11.glVertex2d(m.getX() + m.getWidth() + 1.5, m.getY() + m.getHeight() + 1.5);
	        GL11.glEnd();
	        
	        // left line
	        GL11.glBegin(2);
	        GL11.glVertex2d(m.getX() - 1.5, m.getY() - 1.5);
	        GL11.glVertex2d(m.getX() - 1.5, m.getY() + m.getHeight() + 1.5);
	        GL11.glEnd();
	        
	        // right line
	        GL11.glBegin(2);
	        GL11.glVertex2d(m.getX() + m.getWidth() + 1.5, m.getY() - 1.5);
	        GL11.glVertex2d(m.getX() + m.getWidth() + 1.5, m.getY() + m.getHeight() + 1.5);
	        GL11.glEnd();
	        
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glDisable(GL11.GL_LINE_SMOOTH);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glPopMatrix();
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		ArrayList<HudMod> hovered = new ArrayList<>();
		for(HudMod m : PearClient.instance.modManager.getRenderModuleList()) {
			if(!m.isToggled()) continue;
			if (m.isHovered(mouseX, mouseY) && mouseButton == 0) {
				hovered.add(m);
				dragging = m;
	            dragX = m.getX() - mouseX;
	            dragY = m.getY() - mouseY;
				}
			}
		}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0) {
			mc.displayGuiScreen(new ClickGUI());
		}
		super.actionPerformed(button);
	}

	
	@Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
		dragging = null;
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public static boolean isHover(int X, int Y, int W, int H, int mX, int mY) {
		return mX >= X && mX <= X + W && mY >= Y && mY <= Y + H;
	}
}