package de.verschwiegener.atero.util.components;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.proxy.ProxyType;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;

public class CustomSelectionBox extends Gui{
    
    String[] modes;
    int x, y, width, height;
    Font font;
    boolean visible;
    Color backroundColor;
    Color foregroundColor;
    int position;
    
    public CustomSelectionBox(String[] modes, int x, int y, int width, int height, Font font) {
	this.modes = modes;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.font = font;
	visible = true;
	backroundColor = Management.instance.colorBlack;
    }
    
    public void drawCombobox() {
	if(visible) {
	    RenderUtil.fillRect(x, y, width, height, backroundColor);
	    font.drawString(modes[position], x + (width / 2 - (font.getStringWidth(modes[position]) / 2)), y, foregroundColor);
	    int xOffset = width / 5;
	    RenderUtil.drawLine(x + xOffset, y, x + xOffset, y + height, 4, backroundColor);
	    RenderUtil.drawLine(x + (width - xOffset), y, x + (width - xOffset), y + height, 4, backroundColor);
	    
	    
	}
    }
    
    public void setVisible(boolean visible) {
	this.visible = visible;
    }
    public void setBackroundColor(Color backroundColor) {
	this.backroundColor = backroundColor;
    }
    public void setForegroundColor(Color foregroundColor) {
	this.foregroundColor = foregroundColor;
    }
    
    private void moveType(int amount) {
	position += amount;
	if (position < 0) {
	    position = modes.length - 1;
	} else if (position > modes.length - 1) {
	    position = 0;
	}
    }

}
