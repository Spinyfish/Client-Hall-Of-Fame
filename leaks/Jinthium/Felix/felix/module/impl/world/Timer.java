package felix.module.impl.world;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.NumberValue;

public class Timer extends Module {
	
	private NumberValue<Float> timerValue = new NumberValue<>("Timer Speed", 1.0f, 0.1f, 10.0f);
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
		mc.timer.timerSpeed = 1.0f;
	}
	
	public Timer() {
		super("Timer",0, ModuleCategory.WORLD);
		addValues(timerValue);
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		mc.timer.timerSpeed = timerValue.getValue();
	}
}
