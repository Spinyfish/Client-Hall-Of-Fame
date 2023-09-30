package de.hero.clickgui.elements.menu;

import java.awt.Color;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
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
public class ElementSlider extends Element {
	public boolean dragging;

	/*
	 * Konstrukor
	 */
	public ElementSlider(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		dragging = false;
		super.setup();
	}

	/*
	 * Rendern des Elements 
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		String displayval = "" + Math.round(((de.fanta.setting.settings.Slider) set.getSetting()).curValue * 100D)/ 100D;
		boolean hoveredORdragged = isSliderHovered(mouseX, mouseY) || dragging;
		
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 250 : 200).getRGB();
		int color2 = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 255 : 230).getRGB();
		
		//selected = iset.getValDouble() / iset.getMax();
		double percentBar = (Double)(((de.fanta.setting.settings.Slider) set.getSetting()).curValue - (Double)((de.fanta.setting.settings.Slider) set.getSetting()).getMinValue())/((Double)((de.fanta.setting.settings.Slider) set.getSetting()).getMaxValue() - (Double)((de.fanta.setting.settings.Slider) set.getSetting()).getMinValue());
		
		/*
		 * Die Box und Umrandung rendern
		 */
		Gui.drawRect2(x, y, x + width, y + height,  new Color(18,18,18,180).getRGB());

		/*
		 * Den Text rendern
		 */
		FontUtil.drawString(setstrg, x + 1, y + 2, 0xffffffff);
		FontUtil.drawString(displayval, x + width - FontUtil.getStringWidth(displayval) - 2, y + 2, 0xffffffff);

		/*
		 * Den Slider rendern
		 */
		Gui.drawRect2(x, y + 12, x + width, y + 13.5, 0xff101010);
		Gui.drawRect2(x, y + 12, x + (percentBar * width), y + 13.5, Color.green.getRGB());
		
		if(percentBar > 0 && percentBar < 1)
		Gui.drawRect2(x + (percentBar * width)-1, y + 12, x + Math.min((percentBar * width), width), y + 13.5, Color.black.getRGB());
		

		/*
		 * Neue Value berechnen, wenn dragging
		 */
		if (this.dragging) {
			double diff = (Double)((de.fanta.setting.settings.Slider) set.getSetting()).getMaxValue() - (Double)((de.fanta.setting.settings.Slider) set.getSetting()).getMinValue();
			double val = (Double)((de.fanta.setting.settings.Slider) set.getSetting()).getMinValue() + (MathHelper.clamp_double((mouseX - x) / width, 0, 1)) * diff;
			((de.fanta.setting.settings.Slider) set.getSetting()).curValue = val;
			//((de.dayx.setting.settings.Slider) set.getSetting()).(val); //Die Value im Setting updaten
		}
//		Gui.drawRect2(x, y - 1, x + width, y,new Color(20,220,50).getRGB());

	}

	/*
	 * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
	 * sollen alle anderen Versuche der Interaktion abgebrochen werden?
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isSliderHovered(mouseX, mouseY)) {
			this.dragging = true;
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/*
	 * Wenn die Maus losgelassen wird soll aufgehrt werden die Slidervalue zu verndern
	 */
	public void mouseReleased(int mouseX, int mouseY, int state) {
		this.dragging = false;
	}

	/*
	 * Einfacher HoverCheck, bentigt damit dragging auf true gesetzt werden kann
	 */
	public boolean isSliderHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y + 11 && mouseY <= y + 14;
	}
}