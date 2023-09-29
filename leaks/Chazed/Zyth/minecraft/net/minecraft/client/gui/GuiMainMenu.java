package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.AltManager.GuiAltManager;
import me.wavelength.baseclient.extensions.DiscordRP;
import me.wavelength.baseclient.utils.ColorUtilL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
	private static final AtomicInteger field_175373_f = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private static final Random field_175374_h = new Random();

	private int changes;

	/**
	 * Counts the number of screen updates.
	 */
	private float updateCounter;

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
	private boolean field_175375_v = true;
	private final Object field_104025_t = new Object();
	private String field_92025_p;
	private String field_146972_A;
	private String field_104024_v;
	private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
	private static final ResourceLocation minecraftTitleTextures = new ResourceLocation(
			"textures/gui/title/minecraft.png");

	/**
	 * An array of all the paths to the panorama pictures.
	 */

	public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here"
			+ EnumChatFormatting.RESET + " for more information.";
	private int field_92024_r;
	private int field_92023_s;
	private int field_92022_t;
	private int field_92021_u;
	private int field_92020_v;
	private int field_92019_w;
	private ResourceLocation field_110351_G;
	private GuiButton field_175372_K;
	private static final String __OBFID = "CL_00001154";

	public GuiMainMenu() {
		this.field_146972_A = field_96138_a;
		this.splashText = "missingno";
		BufferedReader var1 = null;

		try {
			ArrayList var2 = Lists.newArrayList();
			var1 = new BufferedReader(new InputStreamReader(
					Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(),
					Charsets.UTF_8));
			String var3;

			while ((var3 = var1.readLine()) != null) {
				var3 = var3.trim();

				if (!var3.isEmpty()) {
					var2.add(var3);
				}
			}

			if (!var2.isEmpty()) {
				do {
					this.splashText = (String) var2.get(field_175374_h.nextInt(var2.size()));
				} while (this.splashText.hashCode() == 125780783);
			}
		} catch (IOException var12) {
			;
		} finally {
			if (var1 != null) {
				try {
					var1.close();
				} catch (IOException var11) {
					;
				}
			}
		}

		this.updateCounter = field_175374_h.nextFloat();
		this.field_92025_p = "";


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
	 * Fired when a key is typed (except F11 who toggle full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		
		//DISCORD
		BaseClient.getDiscordRP().update("Idle", "MainMenu");
		


		
		
		this.viewportTexture = new DynamicTexture(256, 256);
		this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
		Calendar var1 = Calendar.getInstance();
		var1.setTime(new Date());

		int var3 = this.height / 4 + 48;

		this.addSingleplayerMultiplayerButtons(var3, 24);

		this.buttonList.add(new GuiButton(0, this.width / 2 - 50, var3 + 72 + 12, 98, 20,
				I18n.format("Settings", new Object[0])));

		this.buttonList.add(new GuiButton(69, 4, 4, 98, 20,
				I18n.format("Changes", new Object[0])));

		this.buttonList.add(new GuiButton (80, 750, 4, 98, 20,
				I18n.format("Leave", new Object[0])));

		synchronized (this.field_104025_t) {
			this.field_92023_s = this.fontRendererObj.getStringWidth(this.field_92025_p);
			this.field_92024_r = this.fontRendererObj.getStringWidth(this.field_146972_A);
			int var5 = Math.max(this.field_92023_s, this.field_92024_r);
			this.field_92022_t = (this.width - var5) / 2;
			this.field_92021_u = ((GuiButton) this.buttonList.get(0)).yPosition - 24;
			this.field_92020_v = this.field_92022_t + var5;
			this.field_92019_w = this.field_92021_u + 24;
		}
	}

	/**
	 * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have
	 * bought the game.
	 */
	private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
		this.buttonList.add(
				new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("SinglePlayer", new Object[0])));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1,
				I18n.format("Multiplayer", new Object[0])));
		this.buttonList.add(new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, "Alt Manager"));
	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 */

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 80) {
			mc.shutdown();
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 14) {
			this.mc.displayGuiScreen(new GuiAltManager());
		}

		if (button.id == 69) {
			changes++;

			if(changes >= 1) {

			}

			if(changes >= 2) {
				changes = 0;
			}
			System.out.println("showing = " + changes);
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}







	}





	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.disableAlpha();
		GlStateManager.enableAlpha();
		this.mc.getTextureManager().bindTexture(new ResourceLocation("Bpl/Main.png"));
		Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(this.changes == 1) {
			this.drawString(this.fontRendererObj, "Change Log :", 4, 50, -19457);

			this.drawString(this.fontRendererObj, "======ZYTH b1 Module===", 4, 70, -1156418);
			this.drawString(this.fontRendererObj, "[+] New hypixel disabler", 4, 80, -1);
			this.drawString(this.fontRendererObj, "[+] Hypixel Speeds lolol", 4, 90, -1);
			this.drawString(this.fontRendererObj, "[+] Better Verus speeds", 4, 100, -1);
			this.drawString(this.fontRendererObj, "[+] Better verus flight", 4, 110, -1);
			this.drawString(this.fontRendererObj, "[+] Better AntiAim mod", 4, 120, -1);
			this.drawString(this.fontRendererObj, "[*] Better Vulcan fly", 4, 130, -1);
			this.drawString(this.fontRendererObj, "[+] Spartan flight lol", 4, 140, -1);
			this.drawString(this.fontRendererObj, "[+] Minemor Blink fly", 4, 150, -1);
			this.drawString(this.fontRendererObj, "[+] More Animations", 4, 160, -1);
			this.drawString(this.fontRendererObj, "[+] More Disablers", 4, 170, -1);
			this.drawString(this.fontRendererObj, "[+] hypxl InvMove", 4, 180, -1);
			this.drawString(this.fontRendererObj, "[+] TargetHUDs", 4, 190, -1);
			this.drawString(this.fontRendererObj, "[+] bypasses", 4, 200, -1);
			//this.drawString(this.fontRendererObj, "[+] Lunar client Animations", 4, 210, -1);
			//this.drawString(this.fontRendererObj, "[+] Anime ClickGUI", 4, 220, -1);
			//this.drawString(this.fontRendererObj, "[+] MainBackGround", 4, 230, -1);
			//this.drawString(this.fontRendererObj, "======HypixelMovement===", 4, 240, -1156418);
			//this.drawString(this.fontRendererObj, "[+] HypixelFullStrafe", 4, 250, -1);
			//this.drawString(this.fontRendererObj, "[+] HypixelFastBhop", 4, 260, -1);
			//this.drawString(this.fontRendererObj, "[+] HypixelLowHop", 4, 270, -1);
			//this.drawString(this.fontRendererObj, "[+] HypixelZoomLowhop", 4, 280, -1);
			//this.drawString(this.fontRendererObj, "[+] HypixelResetVL", 4, 290, -1);
			//this.drawString(this.fontRendererObj, "=======================", 4, 300, -1156418);
			



			//this.drawString(this.fontRendererObj, "[!] Placeholder", 4, 150, -1);
			//this.drawString(this.fontRendererObj, "[!] Placeholder", 4, 160, -1);

			//his.drawString(this.fontRendererObj, "=[Vulcan Anticheat]=", 4, 200, -1);
			//this.drawString(this.fontRendererObj, "[/] Improved the speed LOL", 4, 210, -1);
			//this.drawString(this.fontRendererObj, "[!] Made it tick faster.", 4, 220, -1);
			//this.drawString(this.fontRendererObj, "[+] Made an inf fly!", 4, 230, -1);
			//this.drawString(this.fontRendererObj, "[!] Only some cfgs.", 4, 240, -1);
			//this.drawString(this.fontRendererObj, "[+] Better Aura!", 4, 250, -1);
			//this.drawString(this.fontRendererObj, "[!] multi flags.", 4, 260, -1);
		}

	}
	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
		

	    
		super.mouseClicked(mouseX, mouseY, mouseButton);
		Object var4 = this.field_104025_t;
		synchronized (this.field_104025_t) {
			if (this.field_92025_p.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v
					&& mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
				GuiConfirmOpenLink var5 = new GuiConfirmOpenLink(this, this.field_104024_v, 13, true);
				var5.disableSecurityWarning();
				this.mc.displayGuiScreen(var5);
			}
		}
	}
}
