package igbt.astolfy.ui.ClickGUI.Compact.impl;

import java.awt.Color;
import java.io.IOException;

import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.ui.ClickGUI.Compact.CompactGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class MButton {
	
	public double x = 0;
	public double y = 0;
	
	public double width = 12;
	public double height = 20;
	public ModuleBase mod;
	public CompactGUI parent;
	public double count;
	
	public MButton(int count,CompactGUI compactGUI, ModuleBase mod, double width, double height) {
		this.parent = compactGUI;
		this.mod = mod;
		this.height = height;
		this.width = width;
		this.count = count;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean b) {
		width = 265;
		height = 30;
		x = parent.x + 75;
		y = parent.y + 5  + parent.scroll +  (int) (parent.headerWidth + (count * (height + 5)));
		Minecraft mc = Minecraft.getMinecraft();
		boolean hovered = isHovered(mouseX, mouseY);
		if(b) {
			Gui.drawRect(x, y, x + width, y + height, hovered ? new Color(41,41,41).getRGB() : new Color(31,31,31).getRGB());
			mc.customFont.drawString(mod.getName(), x + (width / 2) - (mc.customFont.getStringWidth(mod.getName()) / 2), y + ((height - mc.customFont.getHeight()) / 2), -1);
		}
	}
	

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(isHovered(mouseX, mouseY)) {
			mod.toggle();
			//parent.selectedCategory = this.get;
		}
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		double x = parent.x + 75;
		double y = parent.y + 5  + parent.scroll +  (int) (parent.headerWidth + (count * (height + 5)));
		if(x < mouseX && x + width > mouseX
				&& y < mouseY && y + height > mouseY)
			return true;
		
		return false;
	}
}
