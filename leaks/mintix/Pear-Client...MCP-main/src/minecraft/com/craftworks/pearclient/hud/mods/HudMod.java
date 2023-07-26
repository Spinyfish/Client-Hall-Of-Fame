package com.craftworks.pearclient.hud.mods;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.event.EventManager;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.setting.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class HudMod {
	
	public List<Setting> settings = new ArrayList<>();
	
	private String name;
	private String description;

	private int x, y;
	
	private int width, height;

	private boolean toggled;
	
	public Minecraft mc = Minecraft.getMinecraft();
	public FontRenderer fr = mc.fontRendererObj;

	public HudMod(String name, String description, int x, int y) {
		this.name = name;
		this.description = description;
		this.x = x;
		this.y = y;
		this.toggled = true;
		EventManager.register(this);
	}
	
	public void addSettings(Setting... toAdd) {
		settings.addAll(Arrays.asList(toAdd));
	}
	
	public void drawShadow(float x, float y, float width, float height, float radius) {
		
		DrawUtil.drawGradientRoundedRectangle(x, y, width, height, radius, new Color(0, 0, 0, 255), new Color(0, 0, 0, 50), new Color(0, 0, 0, 255), new Color(0, 0, 0, 255));
	}

	public void onRender2D() {
		
	}
	
	public void onRenderShadow() {
		
	}
	
	public void onRenderBackground() {
		DrawUtil.drawGradientRoundedRectangle((float)(this.getX()) - 2, (float)(this.getY()) - 2, (float)(this.getWidth()) + 2, (float)(this.getHeight()) + 2, 5.0f, new Color(60, 232, 118), new Color(60, 232, 118), new Color(255,140,0), new Color(255,140,0));
	}
	
	public void setonRenderBackground(float x, float y, float w, float h, float radius, Color color1, Color color2, Color color3, Color color4) {
		DrawUtil.drawGradientRoundedRectangle(x, y, w, h, radius, color1, color2, color3, color4);
	}
	
	public void onRenderDummy() {
		
	}
	
	public void onEnable() {
		PearClient.instance.eventManager.register(this);
	}
	
	public void onDisable() {
		PearClient.instance.eventManager.unregister(this);
	}
	
	public void toggle() {
		toggled = !toggled;
		
		if(toggled) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		
		if(toggled) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public String getText() {
		return null;
	}
	
	public void setWidth(int width) {
		this.width = this.getX() + width;
	}
	
	public void setHeight(int height) {
		this.height = this.getY() + height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public boolean isToggled() {
		return toggled;
	}

	public String getDescription() {
		return description;
	}
	
public boolean isHovered(int mouseX, int mouseY) {
		
		return mouseX >= x && mouseX <= x + getWidth() && mouseY >= y && mouseY <= y + getHeight();

	}
}
