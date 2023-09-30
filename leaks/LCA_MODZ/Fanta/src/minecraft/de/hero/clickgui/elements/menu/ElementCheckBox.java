package de.hero.clickgui.elements.menu;

import java.awt.Color;


import net.minecraft.client.gui.Gui;
import de.fanta.setting.Setting;
import de.hero.clickgui.elements.Element;
import de.hero.clickgui.elements.ModuleButton;
import de.hero.clickgui.util.ColorUtil;
import de.hero.clickgui.util.FontUtil;


/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class ElementCheckBox extends Element {
	/*
	 * Konstrukor
	 */
	public ElementCheckBox(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		super.setup();
	}

	/*
	 * Rendern des Elements 
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
	
		Gui.drawRect2(x, y, x + width, y + height,  new Color(18,18,18,180).getRGB());
		

		FontUtil.drawString(setstrg, x + width - FontUtil.getStringWidth(setstrg) - 5, y + FontUtil.getFontHeight() / 2 -2, new Color(150,150,150).getRGB());
		Gui.drawRect2(x + 1, y + 2, x + 12, y + 13, ((de.fanta.setting.settings.CheckBox) set.getSetting()).state ?  Color.green.getRGB() : new Color(60,60,60).getRGB());
//		Gui.drawRect2(x, y - 1, x + width, y,new Color(20,220,50).getRGB());
	
	}

	/*
	 * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
	 * sollen alle anderen Versuche der Interaktion abgebrochen werden?
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isCheckHovered(mouseX, mouseY)) {
			((de.fanta.setting.settings.CheckBox) set.getSetting()).setState(!((de.fanta.setting.settings.CheckBox) set.getSetting()).state);
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/*
	 * Einfacher HoverCheck, bentigt damit die Value gendert werden kann
	 */
	public boolean isCheckHovered(int mouseX, int mouseY) {
		return mouseX >= x + 1 && mouseX <= x + 12 && mouseY >= y + 2 && mouseY <= y + 13;
	}
}
