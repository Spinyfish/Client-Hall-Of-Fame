package de.fanta.utils;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import de.fanta.Client;
import de.fanta.gui.font.BasicFontRenderer;

public class LoginScreen extends GuiScreen {
	public GuiTextField name;

	public GuiTextField password;

	public String status = "";

	public String statuse = "";

	private int wdithfade = 0;

	private int rfade = 0;

	private int fade = 0;

	public void initGui() {
		this.wdithfade = -180;
		this.rfade = 0;
		this.fade = 50;
		Keyboard.enableRepeatEvents(true);
		int var3 = height / 4 + 48;

		this.buttonList.add(new GuiButton(1, width / 2 - 103, var3 + 72 - 5, 98, 20, "Login"));
		this.buttonList.add(new GuiButton(2, width / 2 + 3, var3 + 72 - 5, 98, 20, "Quit"));
		this.name = new GuiTextField(3, this.fontRendererObj, width / 2 - 102, 215, 202, 20);
		this.password = new GuiTextField(3, this.fontRendererObj, width / 2 - 100, 116, 200, 20);
		this.name.setMaxStringLength(50);
		this.password.setMaxStringLength(50);
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void updateScreen() {
		this.name.updateCursorCounter();
		this.password.updateCursorCounter();
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.name.mouseClicked(mouseX, mouseY, mouseButton);
		this.password.mouseClicked(mouseX, mouseY, mouseButton);
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (Exception exception) {
		}
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 1)
			if (!this.name.getText().isEmpty()) {

				URL url;
				try {
					url = new URL("https://skidsense.000webhostapp.com/auth/lcaauth.php?hwid=" + getHwid());
				} catch (MalformedURLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
					e.printStackTrace();
					return;
				}

				Scanner scanner = new Scanner(url.openStream());
				String scanners = "";
				while (scanner.hasNext()) {
					scanners += scanner.next();
				}
				try {
					final String[] Split = scanners.split(":");
					final String UID = Split[0];
					final String HWID = Split[1];
					Boolean.parseBoolean(HWID);
					if (this.name.getText().equals(UID) && Boolean.parseBoolean(HWID)) {
						mc.displayGuiScreen((GuiScreen) new GuiMainMenu());
					} else {
						this.statuse = "";
						// Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Ungltige
						// UID/HWID", 50, 50, Color.red.getRGB());
						this.status = "Ungültige UID/HWID";
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			} else {
				this.statuse = "";
				this.status = "Bitte geben sie etwas ein!";
			}

		if (button.id == 2)
			this.mc.shutdown();
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		this.name.textboxKeyTyped(typedChar, keyCode);
		this.password.textboxKeyTyped(typedChar, keyCode);
		if (typedChar == '\t')
			if (this.name.isFocused()) {
				this.name.setFocused(false);
				this.password.setFocused(true);
			} else {
				this.name.setFocused(true);
				this.password.setFocused(false);
			}
		if (typedChar == '\r')
			try {
				actionPerformed(this.buttonList.get(0));
			} catch (Exception exception) {
			}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		BasicFontRenderer fr = (Minecraft.getMinecraft()).fontRendererObj;
		ScaledResolution si = new ScaledResolution(this.mc);
		Client.getBackgrundAPI3().renderShader();
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		Gui.drawRect(scaledresolution.getScaledWidth() / 2F + 110, scaledresolution.getScaledHeight() / 2f - 90,
				scaledresolution.getScaledWidth() / 2 - 110, scaledresolution.getScaledHeight() / 2f + 25,
				new Color(0, 0, 0, 120).getRGB());
		this.name.drawTextBox();
//    this.password.drawTextBox();
		if (this.wdithfade < width) {
			this.wdithfade += 2;
		} else {
			this.wdithfade = -180;
		}
		if (this.fade < 290)
			this.fade++;
		if (this.rfade < 110)
			this.rfade++;

		// drawString(this.fontRendererObj, "AAC Client", this.wdithfade, 1, 16777215);
		Client.INSTANCE.unicodeBasicFontRenderer2.drawCenteredString("UID-Login", width / 3 + 152, 180,
				Color.WHITE.getRGB());
		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(this.status, width / 3 + 112, 266, Color.red.getRGB());
		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(this.statuse, width / 4, 266, Color.red.getRGB());
		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(this.statuse, width / 3 + 112, 266, Color.red.getRGB());

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static String getHwid() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String hwid = "";
		String main = (String.valueOf((Object) String.valueOf((Object) System.getenv((String) "PROCESSOR_IDENTIFIER")))
				+ System.getenv((String) "COMPUTERNAME") + System.getProperty((String) "user.name")).trim();
		byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
		MessageDigest messageDigest = MessageDigest.getInstance((String) "MD5");
		byte[] md5 = messageDigest.digest(bytes);
		int i = 0;
		byte[] arrayOfByte1 = md5;
		int j = md5.length;
		for (int b = 0; b < j; b = (int) ((byte) (b + 1))) {
			byte b2 = arrayOfByte1[b];
			hwid = String.valueOf((Object) String.valueOf((Object) hwid))
					+ Integer.toHexString((int) (b2 + 255 | 0x100)).substring(0, 3);
			if (i != md5.length - 1) {
				hwid = String.valueOf((Object) String.valueOf((Object) hwid)) + "-";
			}
			++i;
		}
		return hwid;
	}
}
