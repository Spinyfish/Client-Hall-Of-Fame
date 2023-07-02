package me.eldodebug.soar.gui.clickgui.category;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import net.minecraft.client.Minecraft;

public class Category {

	public Minecraft mc = Minecraft.getMinecraft();
	private String name;
	public SimpleAnimation introAnimation = new SimpleAnimation(0.0F);
	public SimpleAnimation fontAnimation[] = {
		new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F)
	};
	
	public Category(String name) {
		this.name = name;
	}
	
	public void initGui() {}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}
	
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {}
	
	public void updateScreen() {}

	public void keyTyped(char typedChar, int keyCode) {}

	public String getName() {
		return name;
	}
	
	public float getX() {
		return Soar.instance.guiManager.getClickGUI().getX();
	}
	
	public float getY() {
		return Soar.instance.guiManager.getClickGUI().getY();
	}
	
	public float getWidth() {
		return Soar.instance.guiManager.getClickGUI().getWidth();
	}
	
	public float getHeight() {
		return Soar.instance.guiManager.getClickGUI().getHeight();
	}
}
