package felix.module.impl.player;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;

public class FastPlace extends Module {
	
	public FastPlace() {
		super("FastPlace", 0, ModuleCategory.PLAYER);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
		mc.rightClickDelayTimer = 4;
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		mc.rightClickDelayTimer = 0;
	}
}
