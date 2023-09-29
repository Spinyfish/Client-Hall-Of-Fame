package de.fanta.clickgui.astolfo.objects;

import java.awt.Color;

import de.fanta.module.Module.Type;
import de.fanta.setting.Setting;
import de.hero.clickgui.classes.GuiColorChooser;
import net.minecraft.client.Minecraft;

public class GuiColorChanger extends GuiChanger<Integer>{

public GuiColorChooser chooser;
	
	public GuiColorChanger(int x, int y, Setting setting, Type cat) {
		super(x, y, setting, cat);
		this.chooser = new GuiColorChooser(x, y+15, new Color((int) this.setting.getSetting().getValue()).getRGB());
		this.height = 15;
	}
	
	@Override
	public void draw(float mouseX, float mouseY) {
		super.draw(mouseX, mouseY);
		drawRect(x, y, x+width, y+height, BACKGROUND_COLOR.getRGB());
		drawString(Minecraft.getMinecraft().fontRendererObj, setting.getName(), x+5, y+5, Color.white.getRGB());
		drawRect(x+width-25, y+5, x+width-5, y+13, chooser.color);
		chooser.x = x;
		chooser.y = y+15;
		this.height = 15+chooser.getHeight();
		chooser.draw((int)mouseX, (int)mouseY);
		try {
			int i = chooser.color;
			setting.getSetting().setValue((Integer)i);
		} catch (Exception e) {
		}
	}


}
