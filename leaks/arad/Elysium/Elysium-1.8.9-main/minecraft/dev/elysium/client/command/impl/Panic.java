package dev.elysium.client.command.impl;

import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;
import org.lwjgl.opengl.Display;
import net.minecraft.client.Minecraft;

public class Panic extends Command {
	
	public static boolean panic = false;
	
	public Panic()
	{
		super("Panic", "Unload all modules", "panic", "selfdestruct");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		panic = true;
		Debug.debug = false;

		for(Mod m : Elysium.getInstance().getModManager().getMods())
			m.toggled = false;

		Display.setTitle("Minecraft 1.8.9");
		Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
		int i = 0;
		for(String msg : Minecraft.getMinecraft().ingameGUI.getChatGUI().getSentMessages()) {
			if(msg.contains("Elysium"))
				Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(i);
			i++;
		}

		Elysium.getInstance().getModManager().clear();
	}
}
