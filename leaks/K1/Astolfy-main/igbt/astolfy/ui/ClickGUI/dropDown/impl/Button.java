package igbt.astolfy.ui.ClickGUI.dropDown.impl;

import java.io.IOException;
import java.util.ArrayList;

import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.visuals.Hud;
import igbt.astolfy.settings.Setting;
import igbt.astolfy.settings.settings.BooleanSetting;
import igbt.astolfy.settings.settings.ColorSetting;
import igbt.astolfy.settings.settings.ModeSetting;
import igbt.astolfy.settings.settings.NumberSetting;
import igbt.astolfy.ui.ClickGUI.dropDown.ClickGui;
import igbt.astolfy.ui.ClickGUI.dropDown.impl.set.Checkbox;
import igbt.astolfy.ui.ClickGUI.dropDown.impl.set.ColorPicker;
import igbt.astolfy.ui.ClickGUI.dropDown.impl.set.Mode;
import igbt.astolfy.ui.ClickGUI.dropDown.impl.set.SetComp;
import igbt.astolfy.ui.ClickGUI.dropDown.impl.set.Slider;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Button {
	public Panel panel;
	public ModuleBase mod;
	
	public ArrayList<SetComp> settings = new ArrayList<>();
	
	public double y;
	public boolean extended = false;
	public double height = 15;
	public double setHeight = 0;
	private double sussy = 0;
	private boolean binding = false;
	
	public Button(double y,ModuleBase mod, Panel panel) {
		this.y = y;
		this.panel = panel;
		this.mod = mod;
		for (Setting s : mod.getSettings()) {
			if (s instanceof NumberSetting) {
				this.settings.add(new Slider((NumberSetting) s, this));
			}
			if (s instanceof BooleanSetting) {
				this.settings.add(new Checkbox((BooleanSetting) s, this));
			}
			if (s instanceof ModeSetting) {
				this.settings.add(new Mode((ModeSetting) s, this));
			}
			if (s instanceof ColorSetting) {
				this.settings.add(new ColorPicker((ColorSetting) s, this));
			}
		}
	}
	
	public double drawScreen(int mouseX, int mouseY, float partialTicks, double plusplus) {
		Minecraft mc = Minecraft.getMinecraft();	
		Gui.drawRect(panel.x + 1,panel.y + y + plusplus, panel.x + panel.width - 1, panel.y + y + height + plusplus, mod.isToggled() ? !isHovered(mouseX, mouseY) ? panel.category.color.darker().darker().getRGB() : panel.category.color.darker().darker().darker().getRGB() : isHovered(mouseX, mouseY) ? 
				ClickGui.getSecondaryColor().getRGB() : ClickGui.getThirdColor().getRGB());
		mc.customFont.drawStringWithShadow(binding ? "Binding..." : mod.getName(), panel.x + 5, panel.y + y + ((height - mc.customFont.getHeight()) / 2) + plusplus, -1);

		setHeight = 0;
		sussy = plusplus;
		if(extended) {
			int count = 0;
			for(SetComp sc : settings) {
				setHeight += sc.drawScreen(mouseX, mouseY, panel.x, panel.y + plusplus + y + height + (setHeight));
				count++;
			}
		}
		return setHeight;
	}

	public void keyTyped(char typedChat, int keyCode) {
		if(binding) {
			this.mod.setKey(keyCode);
			binding = false;
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(isHovered(mouseX,mouseY)) {
			switch(mouseButton) {
				case 0:
					mod.toggle();
					break;
				case 1:
					extended = !extended;
					break;
				case 2:
					binding = !binding;
					break;
			}
		}
		for(SetComp sc : settings) {
			sc.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		for(SetComp sc : settings) {
			sc.mouseReleased(mouseX, mouseY, state);
		}
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseX > panel.x && mouseX < panel.x + panel.width)
				&& (mouseY > panel.y + y + sussy && mouseY < panel.y + y + height + sussy);
	}
	
	public double getWidth() {
		return panel.width;
	}
}
