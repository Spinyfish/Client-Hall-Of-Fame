package me.wavelength.baseclient.utils;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class HudMOD {
	
	public Minecraft mc = Minecraft.getMinecraft();
	public FontRenderer fr = mc.fontRendererObj;
	
	public String name;
	public boolean enabled;
	public DraggableComponent drag;
	
	public int x, y;
	




	public HudMOD(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		
		drag = new DraggableComponent(x, y, x + getWidth(), y + getHeight(), new Color(0, 0, 0, 0).getRGB());
	}

	public int getWidth() {
		return 50;
	}
	
	public int getHeight() {
		return 50;
	}
	
	public void draw() {
		
	}
	
	public void renderDummy(int mouseX, int mouseY) {
		drag.draw(mouseX, mouseY);
	}
	
	public int getX() {
		return drag.getyPosition();
	}
	
	public int getY() {
		return drag.getxPosition();
	}
	
}
