package me.eldodebug.soar.management.quickplay.impl;

import java.util.ArrayList;

import me.eldodebug.soar.management.quickplay.QuickPlay;
import me.eldodebug.soar.management.quickplay.QuickPlayCommand;

public class MurderMysteryQuickPlay extends QuickPlay{

	public MurderMysteryQuickPlay() {
		super("Murder", "soar/mods/quickplay/MurderMystery.png");
	}

	@Override
	public void addCommands() {
		ArrayList<QuickPlayCommand> commands = new ArrayList<QuickPlayCommand>();
		
		commands.add(new QuickPlayCommand("Lobby", "/l mm"));
		commands.add(new QuickPlayCommand("Classic", "/play murder_classic"));
		commands.add(new QuickPlayCommand("Double Up", "/play murder_double_up"));
		commands.add(new QuickPlayCommand("Assasins", "/play murder_assassins"));
		commands.add(new QuickPlayCommand("Infection", "/play murder_infection"));
		
		this.setCommands(commands);
	}
}
