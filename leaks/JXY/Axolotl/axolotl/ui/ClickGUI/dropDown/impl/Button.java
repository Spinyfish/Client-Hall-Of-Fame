package axolotl.ui.ClickGUI.dropDown.impl;

import java.io.IOException;
import java.util.ArrayList;

import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.BooleanSetting;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.NumberSetting;
import axolotl.cheats.settings.Setting;
import axolotl.ui.ClickGUI.dropDown.ClickGui;
import axolotl.ui.ClickGUI.dropDown.impl.set.Checkbox;
import axolotl.ui.ClickGUI.dropDown.impl.set.Mode;
import axolotl.ui.ClickGUI.dropDown.impl.set.SetComp;
import axolotl.ui.ClickGUI.dropDown.impl.set.Slider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import java.awt.Color;
import java.util.List;

public class Button {
	public Panel panel;
	public axolotl.cheats.modules.Module mod;
	
	public ArrayList<SetComp> settings = new ArrayList<>();

	public Color toggleColor = new Color(0x32FFFA);
	public double y;
	public boolean extended = false;
	public double height = 15;
	public double setHeight = 0;
	private double sussy = 0;
	private boolean binding = false;
	
	public Button(double y, Module mod, Panel panel) {
		this.y = y;
		this.panel = panel;
		this.mod = mod;
		createSettings(mod.settings.getSettings());
	}

	public void createSettings(List<Setting> settings) {
		for (Setting s : settings) {

			switch(s.sname) {
				case "NumberSetting":
					this.settings.add(new Slider((NumberSetting) s, this));
					break;
				case "BooleanSetting":
					this.settings.add(new Checkbox((BooleanSetting) s, this));
					break;
				case "ModeSetting":
					this.settings.add(new Mode((ModeSetting) s, this));
					createSettings(((ModeSetting)s).getSettingCluster(((ModeSetting)s).getMode()).getSettings());
					break;
				default:
					break;
			}

		}
	}

	public void removeSettings(List<Setting> settings){

		for (SetComp s : this.settings) {
			for(Setting s2 : settings) {
				if(s2.id == s.setting.id) {
					this.settings.remove(s);
					settings.remove(s); // Speed it up!
				}
			}
		}

	}
	
	public double drawScreen(int mouseX, int mouseY, float partialTicks, double plusplus) {
		Minecraft mc = Minecraft.getMinecraft();

		//Gui.drawRect(panel.x, panel.y + y + 16, panel.x + panel.width, panel.y + y, panel.category.color.darker().darker().getRGB());

		if(mod.name.equalsIgnoreCase("ClickGui") && !mod.toggled) {
			mc.currentScreen = null;
			return -1;
		}

		Gui.drawRect(panel.x + 1,panel.y + y + plusplus, panel.x + panel.width - 1, panel.y + y + height + plusplus, mod.isToggled() ? !isHovered(mouseX, mouseY) ? toggleColor.getRGB() : toggleColor.darker().getRGB() : isHovered(mouseX, mouseY) ?
				ClickGui.getSecondaryColor().getRGB() : ClickGui.getThirdColor().getRGB());
		mc.customFont.drawStringWithShadow(binding ? "Binding..." : mod.name, panel.x + 5, panel.y + y + ((height - mc.customFont.getHeight()) / 2) + plusplus, -1);
		setHeight = 0;
		sussy = plusplus;
		if(extended) {
			int count = 0;
			for(SetComp sc : settings) {
				setHeight += sc.drawScreen(mouseX, mouseY, panel.x, panel.y + plusplus + y + height + (setHeight));
				count++;
			}
		}
		//Gui.drawRect(panel.x, panel.y + panel.height, panel.x + 1, panel.y + panel.height + y - 2 + setHeight + plusplus, panel.category.color.darker().darker().getRGB());
		//Gui.drawRect(panel.x + panel.width - 1, panel.y + panel.height, panel.x + panel.width, panel.y + panel.height + y - 2 + setHeight + plusplus, panel.category.color.darker().darker().getRGB());
		//Gui.drawRect(panel.x, panel.y + panel.height + y -2 + setHeight + plusplus - 1, panel.x + 1 + panel.width - 1, panel.y + panel.height + y - 2 + setHeight + plusplus, panel.category.color.darker().darker().getRGB());
		
		return setHeight;
	}

	public void keyTyped(char typedChat, int keyCode) {
		if(binding) {
			this.mod.keybindSetting.setCode(keyCode);
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
