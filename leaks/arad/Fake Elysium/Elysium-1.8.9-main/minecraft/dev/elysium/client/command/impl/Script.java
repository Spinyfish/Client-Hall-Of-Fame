package dev.elysium.client.command.impl;

import org.lwjgl.input.Keyboard;

import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;
import dev.elysium.client.scripting.ScriptManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Script extends Command {
	
	public Script()
	{
		super("Script", "script api", "script");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		if(args.length != 1) {
			Elysium.getInstance().addChatMessage("Usage : script (unload/reload)");
			return;
		}
		if(args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("u")) {
			ScriptManager.unloadScripts();
			Elysium.getInstance().addChatMessage("Unloaded scripts");
		}
		if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
			ScriptManager.unloadScripts();
			ScriptManager.loadScripts();
			
			Elysium.getInstance().addChatMessage("Reloaded scripts");
		}
	}
	
}
