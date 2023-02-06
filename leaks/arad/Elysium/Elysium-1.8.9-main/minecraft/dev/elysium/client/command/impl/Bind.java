package dev.elysium.client.command.impl;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;
import org.lwjgl.input.Keyboard;

public class Bind extends Command {
	
	public Bind()
	{
		super("Bind", "Binds a module", "bind <name> | clear", "b");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		if(args.length == 2)
		{
			String moduleName = args[0];
			String keyName = args[1];
			boolean foundModule = false;
			
			for(Mod module : Elysium.getInstance().getModManager().getMods())
			{
				if(module.name.equalsIgnoreCase(moduleName))
				{
					module.keyCode.setKeyCode(Keyboard.getKeyIndex(keyName.toUpperCase()));
					Elysium.getInstance().addChatMessage(String.format("Bound %s to %s", module.name, Keyboard.getKeyName(module.getKey())));
					foundModule = true;
					break;
				}
			}
			
			if(!foundModule)
			{
				Elysium.getInstance().addChatMessage("Could not find module " + args[0]);
			}
		}
		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("clear"))
			{
				for(Mod module : Elysium.getInstance().getModManager().getMods())
				{
					module.keyCode.setKeyCode(Keyboard.KEY_NONE);
					//Client.prefs.putInt(module.name.toLowerCase() + "key", 0);
				}
				Elysium.getInstance().addChatMessage("Cleared every bind");
			}
			if(args[0].equalsIgnoreCase("list"))
			{
				for(Mod module : Elysium.getInstance().getModManager().getMods())
				{
					if(module.keyCode.getKeyCode() != Keyboard.KEY_NONE) {
						Elysium.getInstance().addChatMessage(module.name + " : " + Keyboard.getKeyName(module.keyCode.getKeyCode()));
					}
				}
			}
		}
	}
	
}