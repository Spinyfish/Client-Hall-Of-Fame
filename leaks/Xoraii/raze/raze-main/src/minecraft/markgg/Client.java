package markgg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.Display;

import markgg.alts.AltManager;
import markgg.command.CommandManager;
import markgg.config.ConfigSystem;
import markgg.config.KeybindSystem;
import markgg.discord.DiscordRP;
import markgg.events.Event;
import markgg.events.listeners.*;
import markgg.modules.Module;
import markgg.modules.combat.*;
import markgg.modules.ghost.*;
import markgg.modules.misc.*;
import markgg.modules.movement.*;
import markgg.modules.player.*;
import markgg.modules.render.*;
import markgg.ui.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class Client {
	public static String name = "Raze", version = "v1.5b";
	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();
	public static HUD hud = new HUD();
	public static AltManager altManager;
	public static CommandManager cmdManager = new CommandManager();
	public static ConfigSystem cfgSystem = new ConfigSystem(new File("Raze/configs/default.json"));
	public static KeybindSystem kbSystem = new KeybindSystem(new File("Raze/keybinds.json"));
	
	public static DiscordRP disRPC = new DiscordRP();

	public static void startup() {
		cfgSystem = new ConfigSystem(new File("Raze/configs/default.json"));
		kbSystem = new KeybindSystem(new File("Raze/keybinds.json"));
		altManager = new AltManager();
		Client.disRPC.start();
		Minecraft mc = Minecraft.getMinecraft();
		
		String icon16x16 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAADVSURBVDhPY6AUMEJpMHjqkyYCZWKAr3/YP6vtmPwTyoUDJigNBv///nuNC3Myfm+BKkMBKAbAwP//DB+wYPGHHok2tzxy2aHKwIAFSqMA2R1zBKFMOHjsmbKAiYH5MPf/7ylA7lyIKFoYPPFM+Q+iGf8zgBShgH+MjN6MDP8kGf8zzpHeMQe/AdgAUKJHdvucUigXDrAaACQWggWQwP///zgYGZke///PuEVux+yDUGEcYbB9TgKUCQegMADaVsLE8P8GkAs3AGsskAJGDaCCARQCBgYASiFjh7ORcJoAAAAASUVORK5CYII=";
		String icon32x32 = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAACsSURBVFhHYxgFIx4wQmmc4KlHSjmUSTSQ3jGnE8okCAg64Ilnyn8okyjw/z/DB9kdcwShXIKACUoPGCApBEC+gzLxgZ//GP6GQNkM8jvmH4EysQKSHPD3/19bQgY+9Ei0YWZkPgzlMshsn4PXjgGPgqGVBogBQy4XDK0o+Pf/X/Z/hv+XoFysgJGBUY+RgakVymUgFB2j2XDUAaMOGHXAqANGHTAKRsEAAwYGAD4YQCi//tmdAAAAAElFTkSuQmCC";
		Minecraft.getMinecraft().setWindowIcon(icon16x16, icon32x32);
		Display.setTitle(name + " " + version);
		
		File directory = new File("Raze");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File directory1 = new File("Raze/configs");
		if (!directory1.exists()) {
			directory1.mkdirs();
		}

		mc.gameSettings.ofFastRender = false;
		mc.gameSettings.ofSmoothFps = false;
		mc.gameSettings.ofFastMath = false;
			
		//combat
		modules.add(new BowAimbot());
		modules.add(new KillAura());
		modules.add(new Velocity());
		//ghost
		modules.add(new AimAssist());
		modules.add(new AutoClicker());
		modules.add(new NoHitDelay());
		modules.add(new WTap());
		//misc
		modules.add(new AntiExploit());
		modules.add(new Cape());
		modules.add(new Disabler());
		modules.add(new Colors());
		modules.add(new Spammer());
		//movement
		modules.add(new CustomSpeed());
		modules.add(new Fly());
		modules.add(new LongJump());
		modules.add(new NoFall());
		modules.add(new NoSlowDown());
		modules.add(new Safewalk());
		modules.add(new Scaffold());
		modules.add(new Sneak());
		modules.add(new Speed());
		modules.add(new Sprint());
		modules.add(new Step());
		//player
		modules.add(new ChestSteal());
		modules.add(new FastPlace());
		modules.add(new Fucker());
		modules.add(new InvManager());
		modules.add(new InvMove());
		modules.add(new Regen());
		modules.add(new VClip());
		//render
		modules.add(new AlwaysDay());
		modules.add(new ChestESP());
		modules.add(new ClickGUI());
		modules.add(new ESP());
		modules.add(new Fullbright());
		modules.add(new HUD2());
		modules.add(new NoWeather());
		modules.add(new Rotations());
		modules.add(new TabGUI());
		modules.add(new TargetHUD());

		File configFile = new File("Raze/configs/default.json");
		if (!configFile.exists()) {
			cfgSystem.saveConfig("default.json");
		}else {
			cfgSystem.loadConfig("default.json");
		}

		File keybindFile = new File("Raze/keybinds.json");
		if (!keybindFile.exists()) {
			kbSystem.saveKeybinds("keybinds.json");
		}else {
			kbSystem.loadKeybinds("keybinds.json");
		}

		String cfgName = "Raze/config.properties";
		File cfgFile = new File(cfgName);
		try {
			if (!cfgFile.exists()) {
				cfgFile.createNewFile();
			}
			Properties properties = new Properties();
			OutputStream outputStream = new FileOutputStream(cfgName);
			properties.setProperty("category", "Combat");
			properties.store(outputStream, "Raze ClickGUI");
			outputStream.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	public static void onEvent(Event e) {
		if(e instanceof EventChat) {
			cmdManager.handlechat((EventChat)e);
		}

		for(Module m : modules) {
			if(!m.toggled)
				continue;

			m.onEvent(e);
		}
	}

	public static List<Module> getModulesByCategory(Module.Category c) {
		List<Module> modules = new ArrayList<>();

		for (Module m : Client.modules) {
			if (m.category == c)
				modules.add(m); 
		}

		return modules;
	}

	public static List<Module> getAllModules() {
		List<Module> modules = new ArrayList<>();

		for (Module m : Client.modules) {
			modules.add(m); 
		}
		return modules;
	}

	public static Module getModuleByName(String name) {
		for (Module m : modules) {
			if (m.name.equals(name))
				return m; 
		} 
		return null;
	}

	public static void keyPress(int key) {
		Client.onEvent(new EventKey(key));
		for(Module m : modules) {
			if(m.getKey() == key) {
				m.toggle();
			}
		}
	}

	public static boolean isModuleToggled(String Module) {
		for (Module m : modules) {
			if (m.name == Module && 
					m.toggled)
				return true; 
		} 
		return false;
	}

	public static void addChatMessage(String message) {
		message = "§c[§c§lRaze§c] §f" + message;
		(Minecraft.getMinecraft()).thePlayer.addChatMessage((IChatComponent)new ChatComponentText(message));
	}
	
	public static void addChatMessage() {
		(Minecraft.getMinecraft()).thePlayer.addChatMessage((IChatComponent)new ChatComponentText(""));
	}
	
	public static void print(String message) {
		System.out.println(message);
	}
	
	public static DiscordRP getDiscordRP() {
		return disRPC;
	}
}
