package de.fanta.clickgui.astolfo.objects;

import java.awt.Color;
import java.util.ArrayList;

import de.fanta.module.Module.Type;
import de.fanta.setting.Setting;
import net.minecraft.client.gui.Gui;

public abstract class GuiChanger<T> extends Gui {

	public int x, y;
	public int width = 125;
	public int height = 35;
	public Setting setting;
	public boolean extended = false;
	public T value;
	public Color HOVER_COLOR = Color.blue, BACKGROUND_COLOR = new Color(25, 25, 25);
	public Type cat;
	
	public GuiChanger(int x, int y, Setting setting, Type cat) {
		this.x = x;
		this.y = y;
		this.setting = setting;
		this.height = 17;
		this.cat = cat;
	}

	public void draw(float mouseX, float mouseY) {
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
	}

	public void mouseDragged(int mouseX, int mouseY, int mouseButton) {
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

}
