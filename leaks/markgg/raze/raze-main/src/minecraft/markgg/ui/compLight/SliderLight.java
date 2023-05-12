package markgg.ui.compLight;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import markgg.Client;
import markgg.modules.Module;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.settings.Setting;
import markgg.ui.GUIMethod;
import markgg.ui.GUIMethodLight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class SliderLight extends CompLight {

	private boolean dragging = false;
	private double renderWidth;
	private double renderWidth2;
	
	public SliderLight(double x, double y, GUIMethodLight parent1, Module module, Setting setting) {
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
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (isInside(mouseX, mouseY, parent1.posX + x - 70, parent1.posY + y + 10,parent1.posX + x - 70 + renderWidth2, parent1.posY + y + 20) && mouseButton == 0) {
			dragging = true;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		dragging = false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY) {
		super.drawScreen(mouseX, mouseY);
		
		int primaryColor = 0xFF5555FF, secondaryColor = 0xFF4B4BDD;
		if(Client.isModuleToggled("Colors")) {
			if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Rainbow") {
				primaryColor = getRainbow(4, 0.8f, 1);
				secondaryColor = getRainbow(4, 0.8f, 0.8f);
			}else if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Custom") {
				int red1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(1)).getValue();
				int green1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(2)).getValue();
				int blue1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(3)).getValue();
				primaryColor = new Color(red1,green1,blue1).getRGB();
				secondaryColor = new Color(red1,green1,blue1).darker().getRGB();
			}
		}else {
			primaryColor = 0xFF5555FF;
			secondaryColor = 0xFF4B4BDD;
		}

		double min = ((NumberSetting)setting).getMinimum();
		double max = ((NumberSetting)setting).getMaximum();
		double l = 90;

		renderWidth = (l) * (((NumberSetting)setting).getValue() - min) / (max - min);
		renderWidth2 = (l) * (((NumberSetting)setting).getMaximum() - min) / (max - min);

		double diff = Math.min(l, Math.max(0, mouseX - (parent1.posX + x - 70)));
		if (dragging) {
			if(setting instanceof NumberSetting) {
				if (diff == 0) {
					((NumberSetting)setting).setValue(((NumberSetting)setting).getMinimum());
				}
				else {
					double newValue = roundToPlace(((diff / l) * (max - min) + min), 1);
					((NumberSetting)setting).setValue(newValue);
				}
			}
		}
		Gui.drawRect(parent1.posX + x - 70, parent1.posY + y + 10,parent1.posX + x - 70 + renderWidth2, parent1.posY + y + 20, secondaryColor);
		Gui.drawRect(parent1.posX + x - 70, parent1.posY + y + 10, parent1.posX + x - 70 + renderWidth, parent1.posY + y + 20, primaryColor);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(setting.name + ": " + ((NumberSetting)setting).getValue(),(int)(parent1.posX + x - 70),(int)(parent1.posY + y), -1);
	}

	private double roundToPlace(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}
