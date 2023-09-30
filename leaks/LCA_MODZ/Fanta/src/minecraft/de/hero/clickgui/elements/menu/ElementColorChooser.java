package de.hero.clickgui.elements.menu;

import java.awt.Color;

import de.fanta.setting.Setting;
import de.fanta.setting.settings.ColorValue;
import de.hero.clickgui.classes.GuiColorChooser;
import de.hero.clickgui.elements.Element;
import de.hero.clickgui.elements.ModuleButton;
import de.hero.clickgui.util.FontUtil;
import net.minecraft.client.gui.Gui;

public class ElementColorChooser extends Element{
	
	private GuiColorChooser colorChooser;
	
	public ElementColorChooser(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		this.colorChooser = new GuiColorChooser((int)x, (int)y+10);
		super.setup();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.colorChooser.x = this.x;
		this.colorChooser.y = this.y+10;
		height = 10 + colorChooser.getHeight();
		
		this.colorChooser.draw(mouseX, mouseY);
		
		this.colorChooser.setWidth((int)width);
		((ColorValue)set.getSetting()).color = this.colorChooser.color;
		Gui.drawRect2(x, y, x+width, y+10, new Color(38,38,38,150).getRGB());
		FontUtil.drawCenteredString(set.getName()+":", x+(width/2), y+2, Color.white.getRGB());
		
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	/*
	 * Einfacher HoverCheck, bentigt damit die Combobox geffnet und geschlossen werden kann
	 */
	public boolean isButtonHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 15;
	}
	
}
