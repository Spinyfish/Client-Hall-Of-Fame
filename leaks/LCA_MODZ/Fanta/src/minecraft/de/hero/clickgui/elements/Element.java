package de.hero.clickgui.elements;

import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.hero.clickgui.ClickGUI;
import de.hero.clickgui.elements.menu.ElementComboBox;
import de.hero.clickgui.util.FontUtil;


/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class Element {
	public ClickGUI clickgui;
	public ModuleButton parent;
	public Setting set;
	public double offset;
	public double x;
	public double y;
	public double width;
	public double height;
	
	public String setstrg;
	
	public boolean comboextended;
	
	public void setup(){
		clickgui = parent.parent.clickgui;
		height = 15;
	}
	
	public void update(){
		/*
		 * Richtig positionieren! Offset wird von ClickGUI aus bestimmt, sodass
		 * nichts ineinander gerendert wird
		 */
		x = parent.x;
		y = parent.y  + parent.height + offset + 1;
		width = parent.width;
		//height = 15;
		
		/*
		 * Title der Box bestimmen und falls ntig die Breite der CheckBox
		 * erweitern
		 */
		String sname = set.getName();
		if(set.getSetting() instanceof de.fanta.setting.settings.CheckBox){
			setstrg = sname.substring(0, 1).toUpperCase() + sname.substring(1, sname.length());
			double textx = x + width - FontUtil.getStringWidth(setstrg);
			if (textx < x + 13) {
				width += (x + 13) - textx + 1;
			}
		}else if(set.getSetting() instanceof de.fanta.setting.settings.DropdownBox){
			height = comboextended ?((de.fanta.setting.settings.DropdownBox) set.getSetting()).getOptions().length* (FontUtil.getFontHeight() + 2) + 15 : 15;
			
			setstrg = sname.substring(0, 1).toUpperCase() + sname.substring(1, sname.length());
			int longest = FontUtil.getStringWidth(setstrg);
			for (String s : ((DropdownBox) set.getSetting()).getOptions()) {
				int temp = FontUtil.getStringWidth(s);
				if (temp > longest) {
					longest = temp;
				}
			}
			double textx = x + width - longest;
			if (textx < x) {
				width += x - textx + 1;
			}
		}else if(set.getSetting() instanceof de.fanta.setting.settings.Slider){
			setstrg = sname.substring(0, 1).toUpperCase() + sname.substring(1, sname.length());
			String displayval = "" + Math.round((Double)((de.fanta.setting.settings.Slider) set.getSetting()).curValue * 100D)/ 100D;
			String displaymax = "" + Math.round((Double)((de.fanta.setting.settings.Slider) set.getSetting()).getMaxValue() * 100D)/ 100D;
			double textx = x + width - FontUtil.getStringWidth(setstrg) - FontUtil.getStringWidth(displaymax) - 4;
			if (textx < x) {
				width += x - textx + 1;
			}
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
	
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return isHovered(mouseX, mouseY);
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {}
	
	public boolean isHovered(int mouseX, int mouseY) 
	{
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
