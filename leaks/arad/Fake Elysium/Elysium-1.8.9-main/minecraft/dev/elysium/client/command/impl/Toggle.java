package dev.elysium.client.command.impl;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;

public class Toggle extends Command {
	
	public Toggle()
	{
		super("Toggle", "Toggle a module", "toggle <name>", "t");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		if(args.length > 0)
		{
			String moduleName = "";
			int i = 0;
			for(String arg : args) {
				moduleName += arg;
				
				if(i != args.length - 1)
					moduleName += " ";
				
				i++;
			}
			
			boolean foundModule = false;
			
			for(Mod mods : Elysium.getInstance().getModManager().getMods())
			{
				if(mods.name.equalsIgnoreCase(moduleName))
				{
					mods.toggle();
					Elysium.getInstance().addChatMessage(mods.toggled ? "Enabled " + mods.name : "Disabled " + mods.name);
					
					foundModule = true;
					break;
				}
			}
			if(!foundModule)
			{
				Elysium.getInstance().addChatMessage("Could not find module " + args[0]);
			}
		}
	}
	
}

