package dev.elysium.client.command.impl;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;

public class Show extends Command {
	
	public Show()
	{
		super("Show", "Toggles to display in arraylist", "Show <name>", "s","hide");
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
					mods.show = !mods.show;
					//Client.prefs.putBoolean(module.name.toLowerCase(), module.toggled);
					Elysium.getInstance().addChatMessage(mods.show ? "Showing " + mods.name : "Hiding " + mods.name);
					
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

