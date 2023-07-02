package me.eldodebug.soar.management.quickplay.impl;

import java.util.ArrayList;

import me.eldodebug.soar.management.quickplay.QuickPlay;
import me.eldodebug.soar.management.quickplay.QuickPlayCommand;

public class TNTQuickPlay extends QuickPlay{

	public TNTQuickPlay() {
		super("TNT", "soar/mods/quickplay/TNT.png");
	}

	@Override
	public void addCommands() {
		ArrayList<QuickPlayCommand> commands = new ArrayList<QuickPlayCommand>();
		
		commands.add(new QuickPlayCommand("Lobby", "/l tnt"));
		commands.add(new QuickPlayCommand("TNT Run", "/play tnt_tntrun"));
		commands.add(new QuickPlayCommand("PVP Run", "/play tnt_pvprun"));
		commands.add(new QuickPlayCommand("Bow Spleef", "/play tnt_bowspleef"));
		commands.add(new QuickPlayCommand("TNT Tag", "/play tnt_tntag"));
		commands.add(new QuickPlayCommand("TNT Wizards", "/play tnt_capture"));
		
		this.setCommands(commands);
	}
}
