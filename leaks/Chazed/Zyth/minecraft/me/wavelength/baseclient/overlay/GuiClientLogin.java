package me.wavelength.baseclient.overlay;

import java.awt.datatransfer.Clipboard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
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

public class GuiClientLogin extends GuiScreen implements GuiYesNoCallback {
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

	public GuiClientLogin() {
		
		
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
		BaseClient.getDiscordRP().update("Idle", "Authenticating...");
		
		
		this.viewportTexture = new DynamicTexture(256, 256);
		this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
		Calendar var1 = Calendar.getInstance();
		var1.setTime(new Date());

		int var3 = this.height / 4 + 48;

		this.addSingleplayerMultiplayerButtons(var3, 24);



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
				new GuiButton(1, this.width / 2 - 100, height/2 - 10, I18n.format("Authenticate yourself my niqqa", new Object[0])));

	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 * 
	 * 
	 */

	
    public static String getHWID() {
        try {
            StringBuilder sb = new StringBuilder();
            String computerName = System.getenv("COMPUTERNAME");
            String processIdentifier = System.getenv("PROCESS_IDENTIFIER");
            String main =  processIdentifier + computerName;
            byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(bytes);
            for (byte b : md5) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x300), 0, 2);
            }
            char[] wow = sb.toString().toCharArray();
            for (char c : wow) {
                try {
                    sb.insert(computerName.length(), c ^ 555 & 114 & 514 ^ 233);
                }catch(Exception e){
                    // oh shit
                }
            }
            String lastNumber = sb.substring(sb.toString().length() - 1);
            try {
                int num = Integer.parseInt(lastNumber);
                sb.append(HWID(num));
            }catch (Exception e){
                //System.out.println("唉");
                return sb.toString();
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }

	private static Object HWID(int num) {
		return null;
	}
	
	private static String ID = null;

	protected void actionPerformed(GuiButton button) throws IOException {


		if (button.id == 1) {
			ID = getHWID();

			
			try {
				Thread.sleep(250L);
				System.out.println("Checking HWID...");
			} catch (InterruptedException e) {
				// Check HWID
				e.printStackTrace();
			}

				
				try {
					Thread.sleep(250L);
					System.out.println("Checking USERNAME...");
				} catch (InterruptedException e) {
					// Check Discord name

					e.printStackTrace();
				}
				
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					// wait
					e.printStackTrace();
				}
			    if(DiscordRP.name.contains("Aiden_jk#1312") && ID.equals("383f3639312232162112162112162162162202161372161412161382161412162212161382162182162102162212161412162112163a363f3a3f3b3733383834null")) {
	    			System.out.println("Your username has been authenticated.");
					this.mc.displayGuiScreen(new GuiMainMenu());
			    }
			    	
			    else if(DiscordRP.name.contains("Chazed#7376") && ID.equals("31303a3032353f314121614321621821621021621721613721613621613721622021614121622221621721621921613821621921621821673b3c3b3239313d3f")) {
	    			System.out.println("Your username has been authenticated.");
					this.mc.displayGuiScreen(new GuiMainMenu());
			    }
			    
			    else if(DiscordRP.name.contains("lrxh#1247") && ID.equals("3e303b32303c3a32102162182162222162202162182162192161412162172162112161382161362162192162172161372162192161422168323f303137353139456g4fdgh")) {
	    			System.out.println("Your username has been authenticated.");
					this.mc.displayGuiScreen(new GuiMainMenu());
			    }
			    
			    else if(DiscordRP.name.contains("SuperSkidder#0000") && ID.equals("3b333f3d3839363218216136216221216142216141216136216210216221216217216221216210216211216143216141216216216137216236393c3f3e363c31null")) {
	    			System.out.println("Your username has been authenticated.");
					this.mc.displayGuiScreen(new GuiMainMenu());
			    }
			    
			    else if(DiscordRP.name.contains("Dabsio#3087") && ID.equals("3b303031422162192162232162222161422162162162102162212162162162232162212162202162112162192162192161372168373634333639333e3534303e")) {
	    			System.out.println("Your username has been authenticated.");
					this.mc.displayGuiScreen(new GuiMainMenu());
			    }
			    
			    else if(DiscordRP.name.contains("Aibao#3610") && ID.equals("31393d136216211216143216219216216216217216211216210216137216219216222216223216221216143216210216218216363435303b39383233303d383c")) {
	    			System.out.println("Your username has been authenticated.");
					this.mc.displayGuiScreen(new GuiMainMenu());

			    }
			    
			    else if(DiscordRP.name.contains("Hello_SW#1628") && ID.equals("35353f3f35313e3217216220216216216136216219216222216222216210216136216142216218216222216141216141216222216222216c393535303c333732null")) {
	    			System.out.println("Your username has been authenticated.");
					this.mc.displayGuiScreen(new GuiMainMenu());
			    }else {
		    			mc.shutdown();
		    			System.out.println("Your licence is not authenticated.");
			    }

		}
	}






	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mc.getTextureManager().bindTexture(new ResourceLocation("Bpl/Main.png"));
		Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);
		
		
		GlStateManager.disableAlpha();
		GlStateManager.enableAlpha();
		//this.mc.getTextureManager().bindTexture(new ResourceLocation("Bpl/Main.png"));
		//Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);
		super.drawScreen(mouseX, mouseY, partialTicks);



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
