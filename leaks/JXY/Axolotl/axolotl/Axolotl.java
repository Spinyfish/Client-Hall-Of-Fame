package axolotl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Proxy;

import axolotl.cheats.commands.CommandManager;
import axolotl.cheats.config.ConfigManager;
import axolotl.cheats.settings.CommandSettings;
import axolotl.util.BlockUtils;
import net.minecraft.client.main.Main;
import org.lwjgl.opengl.Display;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import axolotl.cheats.modules.ModuleManager;
import axolotl.ui.HUD;
import axolotl.ui.MainMenu;
import axolotl.ui.TabGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Session;
import store.intent.api.account.GetUserInfo;
import store.intent.api.account.IntentAccount;
import viamcp.ViaMCP;

public class Axolotl {

	public static Axolotl INSTANCE;
	public CommandManager cmdManager;
	public HUD hud;
	public TabGUI tabGUI;
	public String var1 = "A";
	private String var2 = "1.4.1";
	public String name = "QQ", version = "CL_00001", full_name = name + version;
	public GuiScreen menu = new MainMenu();
	public int width = menu.width, height = menu.height;
	public boolean loadingViaMCPMappings = true;
	public ModuleManager moduleManager;
	public ConfigManager configManager;

	// Ignore these, makes it harder to find the main class :)
	public int obfuscationVar1;
	public int obfuscationVar2;
	public int obfuscationVar3;
	public int obfuscationVar4;
	public int obfuscationVar5;
	public int obfuscationVar6;
	public int obfuscationVar7;
	public int obfuscationVar8;
	public int obfuscationVar9;
	public IntentAccount userInfo;
	public boolean clientOn = true;
	public static void startUp() throws InterruptedException {
		new Axolotl();
	}
	
	public Axolotl() throws InterruptedException {

		INSTANCE = this;
		configManager = new ConfigManager();

		BlockUtils.startUp();

		try {
			userInfo = new GetUserInfo().getIntentAccount(Main.api_key);
		} catch(NullPointerException e) {
			/*System.exit(-27);
			return;*/
			IntentAccount acc = new IntentAccount();
			acc.username = "IntentAccount";
			acc.email = "myemail@gmail.com";
			acc.intent_uid = 69;
			acc.client_uid = "UwU";
			acc.discord_tag = "haha doxxed#6942";
			acc.discord_id = "937818764559405086";
			acc.twoFactor = false;
			acc.api_key = "doxxedEZ";
			acc.loggedIn = true;
			userInfo = acc;
		}

		cmdManager = new CommandManager(new CommandSettings("."));
		moduleManager = new ModuleManager();
		moduleManager.registerCheats();

		hud = new HUD();
		tabGUI = hud.tabGUI;

		name = var1 + "xo" + hud.var1 + tabGUI.var1;
		version = "b" + var2/* + " " + cmdManager.var1*/;
		full_name = name + " " + version;

		// ViaMCP

		try
		{
		  ViaMCP.getInstance().start();
		}
		catch (Exception e)
		{
		  e.printStackTrace();
		}

		configManager.loadConfig(System.getProperty("user.dir") + "\\" + name + "\\currentConfig.json");

		File file = new File(System.getProperty("user.dir") + "\\" + name + "\\al" + "t.t" + "x" + "t");
		
		if(file.exists()) {
			StringBuilder f = new StringBuilder();
			try {
				  
				  BufferedReader br = new BufferedReader(new FileReader(file));
				  
				  String st;
				  while ((st = br.readLine()) != null) {
					   f.append(st);
				  }
				  br.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			
			String d = f.toString();
			
			final String args[] = d.split(":");
			if(args[0].contains("@") && args[0].contains(".")) {
				final YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
				authentication.setUsername(args[0]);
				authentication.setPassword(args[1]);
				try {
					authentication.logIn();
					
					Minecraft.getMinecraft().session = new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "mojang");	
				} catch (AuthenticationException e) {
					e.printStackTrace();
				}
			} else {
				Minecraft.getMinecraft().session = new Session(d, "", "", "mojang");
			}
		}
		
		Display.setTitle(full_name + " - " + Minecraft.getMinecraft().session.getUsername() + " - 1.8 - UID: " + userInfo.client_uid);
		
	}

	public void sendMessage(Object b) {
		b = "\2475[\247d" + name + "\2475] \247d" + b;

		Minecraft mc = Minecraft.getMinecraft();

		if(mc != null && mc.thePlayer != null && mc.theWorld != null)
			mc.thePlayer.addChatMessage(new ChatComponentText(b.toString()));
	}
	
	
}