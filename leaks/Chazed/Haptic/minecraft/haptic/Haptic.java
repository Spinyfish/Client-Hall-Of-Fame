package haptic;

import java.awt.Color;

import org.lwjgl.opengl.Display;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.command.CommandManager;
import haptic.config.SaveLoad;
import haptic.font.FontUtil;
import haptic.module.ModuleManager;
import haptic.processor.impl.PacketsProcessor;
import haptic.processor.impl.servers.HypixelProcessor;
import haptic.processor.impl.servers.RedeskyProcessor;
import haptic.util.base.BaseUtil;
import net.minecraft.util.ChatComponentText;

public class Haptic implements BaseUtil {
	
	private static final Haptic instance = new Haptic();
	
	private static final String name = "Haptic";
	private static final String version = "beta 0.1";
	
	private static final String formattedName = "H" + ChatFormatting.WHITE + "aptic";;
	
	private static ModuleManager moduleManager;
	private static CommandManager commandManager;
	
	private static SaveLoad config;
	
	private static PacketsProcessor packetsProcessor;
	private static HypixelProcessor hypixelProcessor;
	private static RedeskyProcessor redeskyProcessor;
	
	public static void start() {
		System.out.println(getConsolePrefix() + "Loading Client");
		
		moduleManager = new ModuleManager();
		System.out.println(getConsolePrefix() + "Loaded Modules");
		
		config = new SaveLoad("default", true);
		config.load(true);
		System.out.println("Loaded Default Config");
		
		commandManager = new CommandManager();
		System.out.println(getConsolePrefix() + "Loaded Command Manager");
		
		packetsProcessor = new PacketsProcessor();
		hypixelProcessor = new HypixelProcessor();
		redeskyProcessor = new RedeskyProcessor();
		System.out.println(getConsolePrefix() + "Loaded Processors");
		
		try {
			FontUtil.bootstrap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Loaded Custom FontRenderer Util");
		
		Display.setTitle(getFullName() + " | Minecraft 1.8.9");
	}
	
	public static void shutdown() {
		config.save();
	}
	
	public static Haptic getInstance() {
		return instance;
	}
	
	public static String getName() {
		return name;
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getFormattedName() {
		return formattedName;
	}
	
	public static String getFullName() {
		return name + " " + version;
	}
	
	public static String getFullFormattedName() {
		return formattedName + " " + version;
	}
	
	public static ModuleManager getModuleManager() {
		return moduleManager;
	}
	
	public static CommandManager getCommandManager() {
		return commandManager;
	}
	
	public static PacketsProcessor getPacketsProcessor() {
		return packetsProcessor;
	}
	
	public static HypixelProcessor getHypixelProcessor() {
		return hypixelProcessor;
	}
	
	public static RedeskyProcessor getRedeskyProcessor() {
		return redeskyProcessor;
	}
	
	public static void addChatMessage(String message) {
		message = ChatFormatting.YELLOW + "Haptic" + ChatFormatting.WHITE + " : " + message;
		
		mc.thePlayer.addChatMessage(new ChatComponentText(message));
	}
	
	public static String getConsolePrefix() {
		return "[" + getFullName() + "] : ";
	}
	
	public static int getClientColor() {
		return new Color(247, 76, 7).getRGB();
	}
	
}