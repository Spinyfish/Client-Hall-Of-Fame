package haptic.event.impl;

import haptic.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class EventPlayerPosUpdate extends Event {
	
	private final EntityPlayer player;

	public EventPlayerPosUpdate(EntityPlayer p) {
		super(false);
		this.player = p;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}

}