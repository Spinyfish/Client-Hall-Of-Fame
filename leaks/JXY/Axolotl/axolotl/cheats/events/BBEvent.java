package axolotl.cheats.events;

import net.minecraft.util.AxisAlignedBB;

public class BBEvent extends Event {

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean getCancelled() {
		return cancelled;
	}

	public AxisAlignedBB bb;

	public BBEvent(EventType eventType, AxisAlignedBB bb) {
		this.bb = bb;
		this.eventType = eventType;
	}
	
}
