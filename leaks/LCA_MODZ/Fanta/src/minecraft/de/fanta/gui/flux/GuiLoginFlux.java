package de.fanta.gui.flux;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.gui.font.ClientFont;
import de.fanta.gui.font.GlyphPageFontRenderer;
import de.fanta.gui.font.ClientFont.FontType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLoginFlux extends GuiScreen{

	private GuiScreen parent;
	GlyphPageFontRenderer icons, logoFont, icons2, clicks;
	GuiTextFieldFlux usernameField, passwordField;
	
	public GuiLoginFlux(GuiScreen parent) {
	}
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, width, height, Color.white.getRGB());
		mc.getTextureManager().bindTexture(new ResourceLocation("Fanta/gui/loginbackground.png"));
		drawModalRectWithCustomSizedTexture(0, 0, 150, 0, width-150, height, width, height);
		super.drawScreen(mouseX, mouseY, partialTicks);
		icons.drawString("q", width-125, 27, Color.decode("#0FA292").getRGB(), false);
		logoFont.drawString("Fanta", width-107, 24, Color.decode("#0FA292").getRGB(), false);
		icons2.drawString("q", width-60, height-60, new Color(220, 220, 220).getRGB());
		
		clicks = ClientFont.font(13, FontType.ARIAL, true);
		String clickOne = "No Account?";
		boolean hoveredOne = mouseX >= width-85-clicks.getStringWidth(clickOne) && mouseX <= width-85 && mouseY >= height-44 && mouseY <= height-44+clicks.getFontHeight();
		clicks.drawString(clickOne, width-85-clicks.getStringWidth(clickOne), height-44, hoveredOne ? Color.decode("#0C8275").getRGB() : Color.decode("#0FA292").getRGB(), false);
		String clickTwo = "Password Reset";
		boolean hoveredTwo = mouseX >= width-85-clicks.getStringWidth(clickOne) && mouseX <= width-85 && mouseY >= height-29 && mouseY <= height-29+clicks.getFontHeight();
		clicks.drawString(clickTwo, width-85-clicks.getStringWidth(clickOne), height-29, hoveredTwo ? Color.decode("#7D7D7D").getRGB() : Color.decode("#9D9D9D").getRGB(), false);
		
		if(hoveredOne && Mouse.isButtonDown(0)) {
			try {Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));} catch (Exception e) {}
		}
		if(hoveredTwo && Mouse.isButtonDown(0)) {
			try {Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));} catch (Exception e) {}
		}
		this.usernameField.drawTextBox();
		this.passwordField.drawTextBox();
		
		for(GuiButton button : this.buttonList) {
			if(button.id == 1) button.enabled = (!this.usernameField.getText().isEmpty())/* && !this.passwordField.getText().isEmpty())*/;
		}
	}

	@Override
	public void initGui() {
		icons = ClientFont.font(30, FontType.FLUX_ICONS, true);
		icons2 = ClientFont.font(150, FontType.FLUX_ICONS, true);
		logoFont = ClientFont.font(34, "tahomabold", true);
		this.buttonList.add(new GuiButtonFlux(0, width-125, height-115, 100, 15, "GET HWID"));
		this.buttonList.add(new GuiButtonFlux(1, width-125, height-95, 100, 25, "LOGIN"));
		this.usernameField = new GuiTextFieldFlux(0, fontRendererObj, width-125, height/2-50, 100, 20, "UID");
		this.passwordField = new GuiTextFieldFlux(0, fontRendererObj, width-125, height/2-20, 100, 20, "COMING SOON");
		super.initGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		/*if(button.id == 0) {
			try {
				setClipboardString(getHwid());				
			} catch (Exception e) {
			}
		}
		if(button.id == 1) {
			URL url;
			try {
				url = new URL("Kein url für euch <3");
			} catch (MalformedURLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
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
				boolean valid = Boolean.parseBoolean(HWID);
				Client.allowed = valid;
				if (this.usernameField.getText().equals(UID) && valid) {
					mc.displayGuiScreen((GuiScreen) new GuiMainMenu());
				} else {
					// Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Ungltige
					// UID/HWID", 50, 50, Color.red.getRGB());
					
					try {
						Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));	
						System.exit(0);
					} catch (Exception e) {
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				
			}
		}*/
		mc.displayGuiScreen((GuiScreen) new GuiMainMenu());
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
		this.passwordField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void updateScreen() {
		this.usernameField.updateCursorCounter();
		this.passwordField.updateCursorCounter();
		super.updateScreen();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		this.usernameField.textboxKeyTyped(typedChar, keyCode);
		this.passwordField.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}
	
	public static String getHwid() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return "Nö das bekommt ihr auch nicht";
	}
	
}
