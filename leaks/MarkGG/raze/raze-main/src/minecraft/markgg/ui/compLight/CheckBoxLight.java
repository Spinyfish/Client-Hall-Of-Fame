package markgg.ui.compLight;

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

public class CheckBoxLight extends CompLight {
	
	public CheckBoxLight(double x, double y, GUIMethodLight parent1, Module module, Setting setting) {
		this.x = x;
		this.y = y;
		this.parent1 = parent1;
		this.module = module;
		this.setting = setting;
	}
	
	public static int getRainbow(float seconds, float saturation, float brightness) {
		float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY) {
		super.drawScreen(mouseX, mouseY);
		
		int primaryColor = 0xFF5555FF;
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
			primaryColor = 0xFF5555FF;
		}
		
		Gui.drawRect(parent1.posX + x - 70, parent1.posY + y, parent1.posX + x + 10 - 70, parent1.posY + y + 10,((BooleanSetting) setting).isEnabled()? primaryColor : new Color(30,30,30).getRGB());
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(setting.name, (int)(parent1.posX + x - 55), (int)(parent1.posY + y + 1), -1);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (isInside(mouseX, mouseY, parent1.posX + x - 70, parent1.posY + y, parent1.posX + x + 10 - 70, parent1.posY + y + 10) && mouseButton == 0) {
			if (setting instanceof BooleanSetting) {
				BooleanSetting bool = (BooleanSetting)setting;
				bool.toggle();
			}
		}
	}
}
