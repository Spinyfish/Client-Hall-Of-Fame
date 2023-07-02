package me.eldodebug.soar.management.quickplay.impl;

import java.util.ArrayList;

import me.eldodebug.soar.management.quickplay.QuickPlay;
import me.eldodebug.soar.management.quickplay.QuickPlayCommand;

public class ArcadeQuickPlay extends QuickPlay{

	public ArcadeQuickPlay() {
		super("Arcade", "soar/mods/quickplay/Arcade.png");
	}

	@Override
	public void addCommands() {
		ArrayList<QuickPlayCommand> commands = new ArrayList<QuickPlayCommand>();
		
		commands.add(new QuickPlayCommand("Lobby", "/l a"));
		commands.add(new QuickPlayCommand("Hole In The Wall", "/play arcade_hole_in_the_wall"));
		commands.add(new QuickPlayCommand("Football", "/play arcade_soccer"));
		commands.add(new QuickPlayCommand("Bounty Hunters", "/play arcade_bounty_hunters"));
		commands.add(new QuickPlayCommand("Pixel Painters", "/play arcade_pixel_painters"));
		commands.add(new QuickPlayCommand("Dragon Walls", "/play arcade_dragon_wars"));
		commands.add(new QuickPlayCommand("Ender Spleef", "/play arcade_ender_spleef"));
		
		commands.add(new QuickPlayCommand("Galaxy Wars", "/play arcade_starwars"));
		commands.add(new QuickPlayCommand("Throw Out", "/play arcade_throw_out"));
		commands.add(new QuickPlayCommand("Capture The Wool", "/play arcade_pvp_ctw"));
		commands.add(new QuickPlayCommand("Party Games", "/play arcade_party_games_1"));
		commands.add(new QuickPlayCommand("Farm Hunt", "/play arcade_farm_hunt"));
		commands.add(new QuickPlayCommand("Zombies Dead End", "/play arcade_zombies_dead_end"));
		commands.add(new QuickPlayCommand("Zombies Bad Blood", "/play arcade_zombies_bad_blood"));
		
		commands.add(new QuickPlayCommand("Zombies Alien Arcadium", "/play arcade_zombies_alien_arcadium"));
		commands.add(new QuickPlayCommand("Hide & Seek Prop Hunt", "/play arcade_hide_and_seek_prop_hunt"));
		commands.add(new QuickPlayCommand("Hide & Seek Party Pooper", "/play arcade_hide_and_seek_party_pooper"));
		commands.add(new QuickPlayCommand("Hypixel Says", "/play arcade_simon_says"));
		commands.add(new QuickPlayCommand("Mini Walls", "/play arcade_mini_walls"));
		commands.add(new QuickPlayCommand("Blocking Dead", "/play arcade_day_one"));
		
		this.setCommands(commands);
	}
}
