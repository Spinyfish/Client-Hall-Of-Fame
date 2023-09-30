
package markgg.ui;

import java.awt.Color;

import markgg.Client;
import markgg.alts.GuiAltManager;
import markgg.config.ConfigSystem;
import markgg.config.KeybindSystem;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.util.RandomUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

public class MainMenu extends GuiScreen{

	public MainMenu() {}

	public void playPressSound() {
		this.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
	}

	public void initGui() {
		Client.getDiscordRP().update("Idle Main Menu", "");
	}
	
	public static int getRainbow(float seconds, float saturation, float brightness) {
		float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		FontRenderer fr = mc.fontRendererObj;
		mc.getTextureManager().bindTexture(new ResourceLocation("Raze/background.jpg"));
		this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);

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
		

		this.drawGradientRect(0, height - 100, width, height, 0x00000000, 0xff000000);

		String[] buttons = { "Singleplayer", "Multiplayer", "Settings", "Alt Manager", "Quit"};

		int count = 0;
		for(String name : buttons) {
			float x = (width/buttons.length) * count + (width/buttons.length)/2f + 8 - mc.fontRendererObj.getStringWidth(name)/2f;
			float y = height - 20;

			boolean hovered = mouseX >= x  && mouseY >= y && mouseX < x + mc.fontRendererObj.getStringWidth(name) && mouseY < y + mc.fontRendererObj.FONT_HEIGHT;

			this.drawCenteredString(mc.fontRendererObj, name, (width/buttons.length) * count + (width/buttons.length)/2f + 8, height - 20, hovered ? primaryColor : -1);
			count++;
		}

		//update log part

		int greenColor = 0x00FF00, yellowColor = 0xFFFF00, redColor = 0xFF0000, grayColor = 0xD3D3D3;

		String whatIsNew = "Raze " + Client.version + " | What's new?";
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.7, 0.7, 1);
		fr.drawStringWithShadow(whatIsNew, 5, 5, -1);
		fr.drawStringWithShadow("[+] Template" + EnumChatFormatting.WHITE + " - Added Template", 5, 25, greenColor);
		
		GlStateManager.popMatrix();
		fr.drawStringWithShadow("Developed by:", (width - fr.getStringWidth("Developed by:") - 5), 5, grayColor);
		fr.drawStringWithShadow("MarkGG#8181", (width - fr.getStringWidth("MarkGG#8181") - 5), 15, -1);
		fr.drawStringWithShadow("Bloksteri36#2833", (width - fr.getStringWidth("Bloksteri36#2833") - 5), 25, -1);
		
		//end of update log
		GlStateManager.pushMatrix();
		GlStateManager.translate(width/2f, height/2f, 0);
		GlStateManager.scale(2, 2, 1);
		GlStateManager.translate(-(width/2f), -(height/2f), 0);
		int stringWidth = mc.fontRendererObj.getStringWidth("Raze");
		int centerX = Math.round(width / 2f);
		int centerY = Math.round(height / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f);

		fr.drawStringWithShadow("R" + EnumChatFormatting.WHITE + "aze", centerX - stringWidth / 2, centerY, primaryColor);

		GlStateManager.popMatrix();
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		String[] buttons = { "Singleplayer", "Multiplayer", "Settings", "Alt Manager", "Quit"};
		int count = 0;
		for(String name : buttons) {
			float x = (width/buttons.length) * count + (width/buttons.length)/2f + 8 - mc.fontRendererObj.getStringWidth(name)/2f;
			float y = height - 20;

			if(mouseX >= x  && mouseY >= y && mouseX < x + mc.fontRendererObj.getStringWidth(name) && mouseY < y + mc.fontRendererObj.FONT_HEIGHT) {
				switch(name) {
				case "Singleplayer":
					playPressSound();
					mc.displayGuiScreen(new GuiSelectWorld(this));
					break;

				case "Multiplayer":
					playPressSound();
					mc.displayGuiScreen(new GuiMultiplayer(this));
					break;

				case "Settings":
					playPressSound();
					mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
					break;

				case "Alt Manager":
					playPressSound();
					mc.displayGuiScreen(new GuiAltManager());
					break;

				case "Quit":
					KeybindSystem.saveKeybinds("keybinds.json");
			        ConfigSystem.saveConfig("default.json");
					mc.shutdown();
					break;
				}
			}

			count++;
		}
	}

	public void onGuiClosed() {

	}

}
