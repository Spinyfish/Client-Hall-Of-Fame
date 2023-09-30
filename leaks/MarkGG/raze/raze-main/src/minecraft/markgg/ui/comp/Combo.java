package markgg.ui.comp;

import java.awt.Color;

import markgg.Client;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.settings.Setting;
import markgg.ui.GUIMethod;
import markgg.ui.GUIMethodLight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Combo extends Comp {

	public Combo(double x, double y, GUIMethod parent, Module module, Setting setting) {
		this.x = x;
		this.y = y;
		this.parent = parent;
		this.module = module;
		this.setting = setting;
	}
	
	public Combo(double x, double y, GUIMethodLight parent1, Module module, Setting setting) {
		this.x = x;
		this.y = y;
		this.parent1 = parent1;
		this.module = module;
		this.setting = setting;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (isInside(mouseX, mouseY, parent.posX + x - 70, parent.posY + y, parent.posX + x, parent.posY + y + 10) && mouseButton == 0) {
			if (setting instanceof ModeSetting) {
				ModeSetting mode = (ModeSetting)setting;
				mode.cycle();
			} 
		}
	}
	
	public static int getRainbow(float seconds, float saturation, float brightness) {
		float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY) {
		super.drawScreen(mouseX, mouseY);
		
		int primaryColor = 0xFFE44964;
		if(Client.isModuleToggled("Colors")) {
			if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Rainbow") {
				primaryColor = getRainbow(4, 0.8f, 1);
			}else if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Custom") {
				int red1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(1)).getValue();
				int green1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(2)).getValue();
				int blue1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(3)).getValue();
				primaryColor = new Color(red1,green1,blue1).getRGB();
			}
		}else {
			primaryColor = 0xFFE44964;
		}
		
		Gui.drawRect(parent.posX + x - 70, parent.posY + y, parent.posX + x + 20, parent.posY + y + 10, primaryColor);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(setting.name + ": " + ((ModeSetting)setting).getMode(), (int)(parent.posX + x - 69), (int)(parent.posY + y + 1), -1);
	}


}
