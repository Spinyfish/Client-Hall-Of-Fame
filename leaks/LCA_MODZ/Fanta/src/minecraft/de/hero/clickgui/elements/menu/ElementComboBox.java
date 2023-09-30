package de.hero.clickgui.elements.menu;

import java.awt.Color;


import net.minecraft.client.Minecraft;
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
public class ElementComboBox extends Element {
	/*
	 * Konstrukor
	 */
	public ElementComboBox(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		super.setup();
	}

	/*
	 * Rendern des Elements
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		/*
		 * Die Box und Umrandung rendern
		 */
		Gui.drawRect2(x, y, x + width, y + 15, new Color(18,18,18,180).getRGB());
		
		Gui.drawRect2(x, y + 15, x + width, y + height, new Color(18,18,18,200).getRGB());

		FontUtil.drawTotalCenteredString(setstrg, x + width / 2, y + 15/2, new Color(150,150,150).getRGB());
	

		//Gui.drawRect2(x, y + 14, x + width, y + 15, 0x77000000);
		if (comboextended) {
			double ay = y + 15;
			for (String sld : ((de.fanta.setting.settings.DropdownBox) set.getSetting()).getOptions()) {
				String elementtitle = sld.substring(0, 1).toUpperCase() + sld.substring(1, sld.length());
				/*
				 * Ist das Element ausgewhlt, wenn ja dann markiere
				 * das Element in der ComboBox
				 */
				if (sld.equalsIgnoreCase(((de.fanta.setting.settings.DropdownBox) set.getSetting()).curOption)) {
					Gui.drawRect2(x, ay, x + width, ay + FontUtil.getFontHeight() + 2, Color.green.getRGB());
				}
				
				FontUtil.drawCenteredString(elementtitle, x + width / 2, ay, 0xffffffff);
			
				/*
				 * Wie bei mouseClicked 'is hovered', wenn ja dann markiere
				 * das Element in der ComboBox
				 */
				if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY < ay + FontUtil.getFontHeight() + 2) {
					Gui.drawRect2(x + width - 1.2, ay, x + width, ay + FontUtil.getFontHeight() + 2, new Color(20,220,50).getRGB());
				}
				ay += FontUtil.getFontHeight() + 2;
			}
		}
//		Gui.drawRect2(x, y - 1, x + width, y,new Color(20,220,50).getRGB());
	}

	/*
	 * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
	 * sollen alle anderen Versuche der Interaktion abgebrochen werden?
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if (isButtonHovered(mouseX, mouseY)) {
				comboextended = !comboextended;
				return true;
			}
			
			/*
			 * Also wenn die Box ausgefahren ist, dann wird fr jede mgliche Options
			 * berprft, ob die Maus auf diese zeigt, wenn ja dann global jeder weitere 
			 * call an mouseClicked gestoppt und die Values werden aktualisiert
			 */
			if (!comboextended)return false;
			double ay = y + 15;
			for (String slcd : ((de.fanta.setting.settings.DropdownBox) set.getSetting()).getOptions()) {
				if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY <= ay + FontUtil.getFontHeight() + 2) {
					
					//if(clickgui != null && clickgui.setmgr != null)
					((de.fanta.setting.settings.DropdownBox) set.getSetting()).curOption =slcd;
					return true;
				}
				ay += FontUtil.getFontHeight() + 2;
			}
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/*
	 * Einfacher HoverCheck, bentigt damit die Combobox geffnet und geschlossen werden kann
	 */
	public boolean isButtonHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 15;
	}
}
