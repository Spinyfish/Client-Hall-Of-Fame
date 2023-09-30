package de.verschwiegener.atero.ui.audio;

import java.awt.Color;

import de.verschwiegener.atero.Management;

public class ProviderButton {
    
    String name;
    int y;
    
    public ProviderButton(String name, int y) {
	this.name = name;
	this.y = y;
    }
    
    public void drawButton(AudioPanel panel, int x, int y) {
	if(panel.getCurrentProvider() != null && panel.getCurrentProvider().toString() == name) {
	    Management.instance.font.drawString(name, x + ((panel.getWidth() / 5) / 2 - Management.instance.font.getStringWidth2(name)), (y + this.y), Management.instance.colorBlue.getRGB());
	}else {
	    Management.instance.font.drawString(name, x + ((panel.getWidth() / 5) / 2 - Management.instance.font.getStringWidth2(name)), (y + this.y), Color.WHITE.getRGB());
	}
    }

}
