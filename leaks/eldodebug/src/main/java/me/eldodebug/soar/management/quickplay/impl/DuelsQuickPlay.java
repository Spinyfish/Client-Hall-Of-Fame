package me.eldodebug.soar.management.quickplay.impl;

import java.util.ArrayList;

import me.eldodebug.soar.management.quickplay.QuickPlay;
import me.eldodebug.soar.management.quickplay.QuickPlayCommand;

public class DuelsQuickPlay extends QuickPlay{

	public DuelsQuickPlay() {
		super("Duels", "soar/mods/quickplay/Duels.png");
	}

	@Override
	public void addCommands() {
		ArrayList<QuickPlayCommand> commands = new ArrayList<QuickPlayCommand>();
		
		commands.add(new QuickPlayCommand("Lobby", "/l 1v1"));
		commands.add(new QuickPlayCommand("Classic", "/play duels_classic_duel"));
		commands.add(new QuickPlayCommand("Solo SkyWars", "/play duels_sw_duel"));
		commands.add(new QuickPlayCommand("Doubles SkyWars", "/play duels_sw_doubles"));
		commands.add(new QuickPlayCommand("Solo Bow", "/play duels_bow_duel"));
		commands.add(new QuickPlayCommand("Solo UHC", "/play duels_uhc_duel"));
		commands.add(new QuickPlayCommand("Double UHC", "/play duels_uhc_doubles"));
		
		commands.add(new QuickPlayCommand("Teams UHC", "/play duels_uhc_four"));
		commands.add(new QuickPlayCommand("Deathmatch UHC", "/play duels_uhc_meetup"));
		commands.add(new QuickPlayCommand("Solo NoDebuff", "/play duels_potion_duel"));
		commands.add(new QuickPlayCommand("Solo Combo", "/play duels_combo_duel"));
		commands.add(new QuickPlayCommand("Solo Potion", "/play duels_potion_duel"));
		commands.add(new QuickPlayCommand("Solo OP", "/play duels_op_duel"));
		commands.add(new QuickPlayCommand("Doubles OP", "/play duels_op_doubles"));
		
		commands.add(new QuickPlayCommand("Solo Mega Walls", "/play duels_mw_duel"));
		commands.add(new QuickPlayCommand("Doubles Mega Walls", "/play duels_mw_doubles"));
		commands.add(new QuickPlayCommand("Sumo", "/play duels_sumo_duel"));
		commands.add(new QuickPlayCommand("Solo Blitz", "/play duels_blitz_duel"));
		commands.add(new QuickPlayCommand("Solo Bow Spleef", "/play duels_bowspleef_duel"));
		commands.add(new QuickPlayCommand("Bridge 1v1", "/play duels_bridge_duel"));
		commands.add(new QuickPlayCommand("Bridge 2v2", "/play duels_bridge_doubles\""));
		
		commands.add(new QuickPlayCommand("Bridge 4v4", "/play duels_bridge_four"));
		commands.add(new QuickPlayCommand("Bridge 2v2v2v2", "/play duels_bridge_2v2v2v2"));
		commands.add(new QuickPlayCommand("Bridge 3v3v3v3", "/play duels_bridge_3v3v3v3"));
		
		this.setCommands(commands);
	}
}
