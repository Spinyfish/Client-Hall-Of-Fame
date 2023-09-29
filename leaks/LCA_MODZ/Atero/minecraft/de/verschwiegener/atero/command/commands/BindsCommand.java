package de.verschwiegener.atero.command.commands;

import java.util.ArrayList;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.chat.ChatUtil;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import de.verschwiegener.atero.command.Command;

public class BindsCommand extends Command{

	public BindsCommand() {
		super("Binds", "Lists all Binds", new String[] {});
	}
	
	@Override
	public void onCommand(String[] args) {
		ChatUtil.sendMessage("§6------§fBinds §6------");
		for(Module m : Management.instance.modulemgr.getModules()) {
			if(m.getKey() != 0) {
				ChatUtil.addBindsMessage(m.getName());
			}
		}
	}

}
