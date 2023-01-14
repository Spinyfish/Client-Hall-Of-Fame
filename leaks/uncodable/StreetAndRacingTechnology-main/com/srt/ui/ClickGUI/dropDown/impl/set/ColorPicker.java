package com.srt.ui.ClickGUI.dropDown.impl.set;

import java.awt.Color;

import com.srt.settings.settings.ColorSetting;
import com.srt.ui.ClickGUI.dropDown.ClickGui;
import com.srt.ui.ClickGUI.dropDown.impl.Button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ColorPicker extends SetComp {

	private ColorSetting set;
	private boolean draggingBox;
	private boolean draggingHue;
	
	private boolean hovered;
	public static double height = 14;
	private double x;
	private double y;
	private double size;
	private int yOffset;
	private int xOffset;
	
	public ColorPicker(ColorSetting s, Button parent) {
		super(s, parent, height);
		this.set = s;
	}

	@Override
	public double drawScreen(int mouseX, int mouseY, double x, double y) {
		this.hovered = this.isHovered(mouseX, mouseY);
		this.x = x;
		this.y = y;

		this.height = 13;
		double distance = 0.01F;
		size = this.parent.getWidth() - 1;
		
		xOffset = 7;
		yOffset = 14;
		Gui.drawRect(x, y, x + this.parent.getWidth(), y + height + 1, ClickGui.getSecondaryColor().getRGB());
		Gui.drawRect(this.x + 1, this.y, this.x - 1 + this.parent.getWidth(), this.y + this.height, ClickGui.getSecondaryColor().brighter().getRGB());
		
		//Gui.drawRect(x + xOffset, y + 3, x + xOffset + Client.customFont.getStringWidth(this.set.getName()), y + 12, Color.HSBtoRGB(1, 0F, 1F - this.set.getBrightness()));


		//Gui.drawRect(this.x + this.xOffset, this.y + this.yOffset - 1, this.x + this.xOffset + size + 1, this.y + this.yOffset, -1);
		//Gui.drawRect(this.x + this.xOffset, this.y + this.yOffset + this.size + 1, this.x + this.xOffset + size + 1, this.y + this.yOffset + this.size + 2, -1);
		//Gui.drawRect(this.x + this.xOffset - 1, this.y + this.yOffset - 1, this.x + this.xOffset, this.y + this.yOffset + this.size + 2, -1);
		//Gui.drawRect(this.x + this.xOffset + this.size + 1, this.y + this.yOffset - 1, this.x + this.xOffset + this.size + 2, this.y + this.yOffset + this.size + 2, -1);
		for (double brightness = 0; brightness < 1; brightness += distance) {
			for (double saturation = 0; saturation < 1; saturation += distance) {
				double startX = x + (saturation * size) + xOffset;
				double endX = x + (saturation * size) + xOffset + 1;
				double startY = y + ((1 - brightness) * size) + yOffset;
				double endY = y + ((1 - brightness) * size) + 1 + yOffset;
				//Gui.drawRect(startX, startY, endX, endY, Color.HSBtoRGB(this.set.getHue(), 1, 1));
				
				if (this.draggingBox) {
					if (mouseX >= (saturation == 0 ? -99999999 : startX) && mouseX <= (saturation > 0.9999999 ? 99999999 : endX) && mouseY >= (brightness >=  0.999999 ? -9999999 : startY) && mouseY <= (brightness == 0 ? 9999999 : endY)) {
						//this.set.setSaturation((float) saturation);
						//this.set.setBrightness((float) brightness);
					}
				}
			}
		}
		for (double hue = 0; hue < 1; hue += distance) {
			double startX = x + ((1.0 - hue) * size);
			double endX = x + ((1.0 - hue) * size) + this.parent.getWidth() - 99;
			double startY = y;
			double endY = y + this.height;
			Gui.drawRect(startX, startY, endX, endY, Color.HSBtoRGB((float)hue, 0.9F, 1F));
			
			if (this.draggingHue) {
				if (mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY) {
					this.set.setHue((float) hue);
				}
			}
		}
		float lhue = set.getHue();
		Gui.drawRect(this.x, this.y, this.x+1, this.y + height,new Color(20,20,20).getRGB());
		Gui.drawRect(this.x+parent.getWidth(), this.y, this.x+parent.getWidth()-1, this.y + height,new Color(20,20,20).getRGB());
		Gui.drawRect(this.x + -lhue * (this.parent.getWidth() - 1) + this.parent.getWidth() - 1.0,this.y + (double)1,this.x +-lhue * (this.parent.getWidth() - 1)  - 0.0 + this.parent.getWidth(),this.y + this.height - 1,new Color(20,20,20).getRGB());
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.set.getName(), (float)(x + 4), (float)(y + 2), -1);
		return this.height;
	}
	
	@Override
	public void mouseClicked(int x, int y, int button) {
		if (button == 0 && this.hovered) {
			this.draggingHue = true;
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0) {
			draggingBox = false;
			draggingHue = false;
		}
	}
	
	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + this.parent.getWidth() && mouseY > y && mouseY < y + height;
	}
}
