package igbt.astolfy.ui.ClickGUI.Compact.impl;

import igbt.astolfy.module.ModuleBase.Category;
import igbt.astolfy.ui.ClickGUI.Compact.CompactGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import java.awt.*;
import java.io.IOException;

public class CButton {
	
	
	double width = 12;
	double height = 20;
	Category category;
	CompactGUI parent;
	double count;
	
	public CButton(int count,CompactGUI compactGUI, Category category, double width, double height) {
		this.parent = compactGUI;
		this.category = category;
		this.height = height;
		this.width = width;
		this.count = count;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		width = 65;
		double x = parent.x + 2;
		double y = parent.y + 5 +  (int) (parent.headerWidth + (count * 25));
		Minecraft mc = Minecraft.getMinecraft();
		boolean hovered = isHovered(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, hovered ? new Color(41,41,41).getRGB() : new Color(31,31,31).getRGB());
		mc.customFont.drawString(category.name, x + (width / 2) - (mc.customFont.getStringWidth(category.name) / 2), y + (height /2 /2), -1);
	}
	

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(isHovered(mouseX, mouseY)) {
			parent.selectedCategory = this.category;
			parent.scroll = 0;
		}
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		double x = parent.x + 2;
		double y = parent.y + (int) (parent.headerWidth + (count * 25) + 5);
		if(x <= mouseX && x + width >= mouseX
				&& y <= mouseY && y + height >= mouseY)
			return true;
		
		return false;
	}
}
