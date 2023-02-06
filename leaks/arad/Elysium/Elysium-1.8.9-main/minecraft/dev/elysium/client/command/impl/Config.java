package dev.elysium.client.command.impl;

import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;
import dev.elysium.client.extensions.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Config extends Command {
	
	public Config()
	{
		super("Config", "Save or load configs!", "c load <name> | c delete <name> | c save <name> | c list", "c");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("save")) {
				ConfigManager.save(args[1]);
			}
			if(args[0].equalsIgnoreCase("load")) {
				ConfigManager.load(args[1]);
			}
			if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
				ConfigManager.delete(args[1]);
			}
		} else if(args.length == 1 && args[0].equalsIgnoreCase("list")){
			Elysium.getInstance().addChatMessageConfig("Configs [" + ConfigManager.configs().length + "]:");
			for(String s : ConfigManager.configs()) {
				Elysium.getInstance().addChatMessageConfig(s.substring(0, s.length() - 8));
			}
			Elysium.getInstance().addChatMessageConfig("- - -");
		} else {
			Elysium.getInstance().addChatMessageConfig("Usage: " + "c load <name> | c save <name> | c list");
		}
	}
}
