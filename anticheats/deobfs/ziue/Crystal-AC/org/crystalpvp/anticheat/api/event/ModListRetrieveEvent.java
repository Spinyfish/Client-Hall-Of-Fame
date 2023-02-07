package org.crystalpvp.anticheat.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Getter
public class ModListRetrieveEvent extends PlayerEvent {

	private Map<String, String> mods;

	public ModListRetrieveEvent(Player player, Map<String, String> mods) {
		super(player);
		this.mods = mods;
	}
}
