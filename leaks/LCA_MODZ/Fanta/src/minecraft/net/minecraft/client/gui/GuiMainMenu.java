package net.minecraft.client.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.google.common.collect.Lists;

import de.fanta.Client;
import de.fanta.design.AltManager;
import de.fanta.gui.flux.GuiButtonFanta;
import de.fanta.gui.flux.GuiButtonFlux;
import de.fanta.utils.AnimationUtil;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.UnicodeFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
	public static final String MORE_INFO_TEXT = "Please click " + EnumChatFormatting.UNDERLINE + "here"
			+ EnumChatFormatting.RESET + " for more information.";
	private static final AtomicInteger field_175373_f = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private static final Random RANDOM = new Random();
	private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
	private static final ResourceLocation minecraftTitleTextures = new ResourceLocation(
			"textures/gui/title/minecraft.png");
	/**
	 * An array of all the paths to the panorama pictures.
	 */
	private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {
			new ResourceLocation("textures/gui/title/background/panorama_0.png"),
			new ResourceLocation("textures/gui/title/background/panorama_1.png"),
			new ResourceLocation("textures/gui/title/background/panorama_2.png"),
			new ResourceLocation("textures/gui/title/background/panorama_3.png"),
			new ResourceLocation("textures/gui/title/background/panorama_4.png"),
			new ResourceLocation("textures/gui/title/background/panorama_5.png") };
	/**
	 * The Object object utilized as a thread lock when performing non thread-safe
	 * operations
	 */
	private final Object threadLock = new Object();
	/**
	 * Counts the number of screen updates.
	 */
	private final float updateCounter;
	private final boolean field_175375_v = true;
	/**
	 * The splash message.
	 */
	private String splashText;
	private GuiButton buttonResetDemo;
	
	/**
	 * Timer used to rotate the panorama, increases every tick.
	 */
	private int panoramaTimer;
	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private DynamicTexture viewportTexture;
	/**
	 * OpenGL graphics card warning.
	 */
	private String openGLWarning1;
	/**
	 * OpenGL graphics card warning.
	 */
	private String openGLWarning2;
	/**
	 * Link to the Mojang Support about minimum requirements
	 */
	private String openGLWarningLink;
	private int openGLWarning2Width;
	private int openGLWarning1Width;
	private int openGLWarningX1;
	private int openGLWarningY1;
	private int openGLWarningX2;
	private int openGLWarningY2;
	private ResourceLocation backgroundTexture;
	private double current;

	public GuiMainMenu() {
		this.openGLWarning2 = MORE_INFO_TEXT;
		this.splashText = "missingno";
		BufferedReader bufferedreader = null;

		try {
			List<String> list = Lists.newArrayList();
			bufferedreader = new BufferedReader(new InputStreamReader(
					Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(),
					Charsets.UTF_8));
			String s;

			while ((s = bufferedreader.readLine()) != null) {
				s = s.trim();

				if (!s.isEmpty()) {
					list.add(s);
				}
			}
		
			if (!list.isEmpty()) {
				while (true) {
					this.splashText = list.get(RANDOM.nextInt(list.size()));

					if (this.splashText.hashCode() != 125780783) {
						break;
					}
				}
			}
		} catch (IOException exception) {
		} finally {
			if (bufferedreader != null) {
				try {
					bufferedreader.close();
				} catch (IOException var11) {
				}
			}
		}

		this.updateCounter = RANDOM.nextFloat();
		this.openGLWarning1 = "";

		if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
			this.openGLWarning1 = I18n.format("title.oldgl1");
			this.openGLWarning2 = I18n.format("title.oldgl2");
			this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		++this.panoramaTimer;
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		current = 0.5;
		int i = 24;
		int j = this.height / 4 + 48;

		this.buttonList.add(new GuiButtonFanta(26, this.width / 2 - 100, i + j + 25, I18n.format("AltManager")));
		this.buttonList.add(new GuiButtonFanta(0, this.width / 2 - 100, j + 62 + 12, 98, 20, I18n.format("menu.options")));
		this.buttonList.add(new GuiButtonFanta(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
		this.buttonList.add(new GuiButtonFanta(2, this.width / 2 - 100, i + j, I18n.format("menu.multiplayer")));
		this.buttonList.add(new GuiButtonFanta(4, this.width / 2 + 2, j + 62 + 12, 98, 20, I18n.format("menu.quit")));

		synchronized (this.threadLock) {
			this.openGLWarning1Width = this.fontRendererObj.getStringWidth(this.openGLWarning1);
			this.openGLWarning2Width = this.fontRendererObj.getStringWidth(this.openGLWarning2);
			int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
			this.openGLWarningX1 = (this.width - k) / 2;
			this.openGLWarningY1 = this.buttonList.get(0).yPosition - 24;
			this.openGLWarningX2 = this.openGLWarningX1 + k;
			this.openGLWarningY2 = this.openGLWarningY1 + 24;
		}


		if(!Client.allowed)
			try {
				Display.releaseContext();
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		this.mc.setConnectedToRealms(false);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 26) {
			mc.displayGuiScreen(new AltManager(this));
		}
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (button.id == 4) {
			this.mc.shutdown();
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	final UnicodeFontRenderer nameFont = UnicodeFontRenderer.getFontOnPC("Moonbeam", 100);
	final UnicodeFontRenderer versionFont = UnicodeFontRenderer.getFontOnPC("Sitka Text", 100);

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int i = 274;
		int j = this.width / 2 - i / 2;
		int k = 30;
		String mcInfo = "Minecraft 1.8.8 Modded by "+ "LCA_MODZ,Command JO,Exeos";
		String copyright = "Fanta "+ "V" + Client.INSTANCE.version+ " Beta Build";
		
		this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Client.getBackgrundAPI3().renderShader();
		Client.INSTANCE.fluxTabGuiFont.drawStringWithShadow( copyright,
				this.width - Client.INSTANCE.fluxTabGuiFont.getStringWidth(copyright) -4, this.height - 15, -1);
		Client.INSTANCE.fluxTabGuiFont.drawStringWithShadow( mcInfo, 0, this.height - 15, -1);
			
		current = AnimationUtil.animate(current, 1, 0.02);
		
		GL11.glPushMatrix();
		GL11.glScaled(current, current, current);
		
		RenderUtil.drawRoundedRect2(width / 2 - 120, this.height / 4 + 40, 240, 110, 11, new Color(0,0,0,120));
		
		Client.INSTANCE.fluxTabGuiFont2.drawStringWithShadow(Client.INSTANCE.getName(),this.width / 2 - (Client.INSTANCE.unicodeBasicFontRenderer2.getStringWidth(Client.INSTANCE.getName()) / 2), this.height / 4, 0xFFFFFF);


		if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
			drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2,
					this.openGLWarningY2 - 1, 1428160512);
			this.drawString(this.fontRendererObj, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
			this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2,
					this.buttonList.get(0).yPosition - 12, -1);
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		synchronized (this.threadLock) {
			if (this.openGLWarning1.length() > 0 && mouseX >= this.openGLWarningX1 && mouseX <= this.openGLWarningX2
					&& mouseY >= this.openGLWarningY1 && mouseY <= this.openGLWarningY2) {
				GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
				guiconfirmopenlink.disableSecurityWarning();
				this.mc.displayGuiScreen(guiconfirmopenlink);
			}
		}
	}

	public void confirmClicked(boolean result, int id) {
		if (result && id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			isaveformat.flushCache();
			isaveformat.deleteWorldDirectory("Demo_World");
			this.mc.displayGuiScreen(this);
		} else if (id == 13) {
			if (result) {
				try {
					Class<?> oclass = Class.forName("java.awt.Desktop");
					Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
					oclass.getMethod("browse", new Class[] { URI.class }).invoke(object,
							new URI(this.openGLWarningLink));
				} catch (Throwable throwable) {
					logger.error("Couldn't open link", throwable);
				}
			}

			this.mc.displayGuiScreen(this);
		}
	}
}
