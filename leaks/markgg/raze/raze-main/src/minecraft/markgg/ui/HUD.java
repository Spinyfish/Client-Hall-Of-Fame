package markgg.ui;

import java.awt.Color;
import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.events.listeners.EventRenderGUI;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.ui.notifs.NotificationManager;
import markgg.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class HUD {

	public Minecraft mc = Minecraft.getMinecraft();

	public static int getRainbow(float seconds, float saturation, float brightness) {
		float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}

	public void draw() {
		ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		FontRenderer fr = mc.fontRendererObj;

		int primaryColor = 0xFFE44964;
		int whiteColor = -1;

		if(Client.isModuleToggled("Colors")) {
			if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Rainbow") {
				primaryColor = getRainbow(4, 0.8f, 1);
				whiteColor = getRainbow(4, 0.8f, 1);
			}else if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Custom") {
				int red1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(1)).getValue();
				int green1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(2)).getValue();
				int blue1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(3)).getValue();
				primaryColor = new Color(red1,green1,blue1).getRGB();
				whiteColor = -1;
			}
		}else {
			primaryColor = 0xFFE44964;
			whiteColor = -1;
		}

		int scaledHeight = sr.getScaledHeight();
		int xPos = 2;
		int yPos = scaledHeight - (mc.fontRendererObj.FONT_HEIGHT + 7);
		float squareMotion = (float)(MathUtil.square(mc.thePlayer.motionX) + MathUtil.square(mc.thePlayer.motionZ));
		float bps = (float)MathUtil.round(Math.sqrt(squareMotion) * 20.0D * mc.timer.timerSpeed, 2.0D);
		float xCord = (float)MathUtil.round(mc.thePlayer.posX, 0.0D);
		float yCord = (float)MathUtil.round(mc.thePlayer.posY, 0.0D);
		float zCord = (float)MathUtil.round(mc.thePlayer.posZ, 0.0D);

		Client.modules.sort(Comparator.comparingInt(m -> fr.getStringWidth(((Module)m).name)).reversed());

		if (Client.isModuleToggled("HUD")) {
			double offset1 = (fr.FONT_HEIGHT + 6);

			GlStateManager.pushMatrix();
			GlStateManager.translate(4.0F, 4.0F, 0.0F);
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			if(((BooleanSetting)Client.getModuleByName("HUD").settings.get(1)).isEnabled()) {
				fr.drawStringWithShadow("BPS: " + EnumChatFormatting.WHITE + bps, xPos, yPos - 10, primaryColor);
			}
			if(((BooleanSetting)Client.getModuleByName("HUD").settings.get(2)).isEnabled()) {
				fr.drawStringWithShadow("FPS: " + EnumChatFormatting.WHITE + mc.getDebugFPS(), xPos, yPos - 20, primaryColor);
			}
			if(((BooleanSetting)Client.getModuleByName("HUD").settings.get(5)).isEnabled()) {
				fr.drawStringWithShadow("XYZ: " + EnumChatFormatting.WHITE + xCord + " | " + yCord + " | " + zCord, xPos, yPos, primaryColor);
			}
			if(((BooleanSetting)Client.getModuleByName("HUD").settings.get(4)).isEnabled()) {
				switch(((ModeSetting)Client.getModuleByName("HUD").settings.get(0)).getMode()) {
				case "Classic":
					GlStateManager.translate(-4.0F, -4.0F, 0.0F);
					Gui.drawRect(116, 2, 6, 16, 0);
					Gui.drawRect(116, 1, 6, 2, 0);
					GlStateManager.scale(0.7F, 0.7F, 0.7F);
					fr.drawStringWithShadow(Client.version, 8, 32, primaryColor);
					GlStateManager.scale(3F, 3F, 3F);
					fr.drawStringWithShadow("R", 2, 2, primaryColor);
					fr.drawStringWithShadow("aze", 8, 2, -1);
					break;
				case "Sense":
					GlStateManager.translate(1F, 1F, 1F);
					Gui.drawRect(-3, 12, 100 + fr.getStringWidth(" " + mc.getSession().getUsername()) - 37, -3, -1879048192);
					Gui.drawRect(-3, -3, 100 + fr.getStringWidth(" " + mc.getSession().getUsername()) - 37, -2, primaryColor);
					fr.drawStringWithShadow(EnumChatFormatting.WHITE + "raze" + EnumChatFormatting.RESET + "sense" + " ⎜ " + mc.getSession().getUsername(), 0, 0, primaryColor);
					break;
				}
			}

			GlStateManager.popMatrix();
			if(((BooleanSetting)Client.getModuleByName("HUD").settings.get(7)).isEnabled()) {
				NotificationManager.render();
			}
			if(((BooleanSetting)Client.getModuleByName("HUD").settings.get(6)).isEnabled()) {
				renderKeyStrokes();
			}

			int count = 0;
			if(((BooleanSetting)Client.getModuleByName("HUD").settings.get(3)).isEnabled()) {
				for (Module m : Client.modules) {
					if (!m.toggled || m.name.equals("TabGUI") || m.name.equals("Colors") || m.name.equals("Cape"))
						continue; 

					String nameMode = m.name;
					double offset = (count * (fr.FONT_HEIGHT + 6));
					Gui.drawRect((sr.getScaledWidth() - fr.getStringWidth(nameMode) - 8), offset, sr.getScaledWidth(), (6 + fr.FONT_HEIGHT) + offset, -1879048192);
					fr.drawStringWithShadow(nameMode, (sr.getScaledWidth() - fr.getStringWidth(nameMode) - 4), 4.0D + offset, whiteColor);
					Gui.drawRect((sr.getScaledWidth() - fr.getStringWidth(nameMode) - 9), offset, (sr.getScaledWidth() - fr.getStringWidth(nameMode) - 8), (6 + fr.FONT_HEIGHT) + offset, primaryColor);
					count++;
				}
			}
		}


		Client.onEvent(new EventRenderGUI());
	}

	private void renderKeyStrokes() {
		ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		FontRenderer fr = this.mc.fontRendererObj;

		int whiteColor = -1;
		if(Client.isModuleToggled("Colors")) {
			if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Rainbow") {
				whiteColor = getRainbow(4, 0.8f, 1);
			}else if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Custom") {
				int red1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(1)).getValue();
				int green1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(2)).getValue();
				int blue1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(3)).getValue();
				whiteColor = new Color(red1,green1,blue1).getRGB();
			}
		}else {
			whiteColor = -1;
		}

		int wAlpha = (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()) ? 125 : 50);
		int aAlpha = (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()) ? 125 : 50);
		int sAlpha = (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()) ? 125 : 50);
		int dAlpha = (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()) ? 125 : 50);

		Gui.drawRect(sr.getScaledWidth() - 29 - 29, sr.getScaledHeight() - 4 - 25 - 29, sr.getScaledWidth() - 4 - 29, sr.getScaledHeight() - 4 - 29, new Color(0, 0, 0, wAlpha).getRGB());
		Gui.drawRect(sr.getScaledWidth() - 29 - 29 - 29, sr.getScaledHeight() - 4 - 25, sr.getScaledWidth() - 4 - 29 - 29, sr.getScaledHeight() - 4, new Color(0, 0, 0, aAlpha).getRGB());
		Gui.drawRect(sr.getScaledWidth() - 29 - 29, sr.getScaledHeight() - 4 - 25, sr.getScaledWidth() - 4 - 29, sr.getScaledHeight() - 4, new Color(0, 0, 0, sAlpha).getRGB());
		Gui.drawRect(sr.getScaledWidth() - 29, sr.getScaledHeight() - 4 - 25, sr.getScaledWidth() - 4, sr.getScaledHeight() - 4, new Color(0, 0, 0, dAlpha).getRGB());

		fr.drawString("W", sr.getScaledWidth() - 48, sr.getScaledHeight() - 49, whiteColor);
		fr.drawString("A", sr.getScaledWidth() - 77, sr.getScaledHeight() - 20, whiteColor);
		fr.drawString("S", (int) (sr.getScaledWidth() - 48.5), sr.getScaledHeight() - 20, whiteColor);
		fr.drawString("D", sr.getScaledWidth() - 19, sr.getScaledHeight() - 20, whiteColor);
	}
}