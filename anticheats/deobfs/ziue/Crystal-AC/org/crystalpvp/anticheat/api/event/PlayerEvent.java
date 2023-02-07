package org.crystalpvp.anticheat.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Getter
public class PlayerEvent extends BaseEvent {
	private Player player;

	public PlayerEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUniqueId() {
		return player.getUniqueId();
	}
}
