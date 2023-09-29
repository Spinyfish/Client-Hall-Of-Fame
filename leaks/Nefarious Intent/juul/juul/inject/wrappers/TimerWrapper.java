package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;

public class TimerWrapper extends Wrapper {

	public TimerWrapper(Object instance) {
		super("net.minecraft.util.Timer", instance);
	}
	
	public float getTimerSpeed() { return (float) getField("timerSpeed"); }

	public void setTimerSpeed(float speed) { setField("timerSpeed", speed); }
	
	public float renderPartialTicks() { return (float) getField("renderPartialTicks"); }
}
