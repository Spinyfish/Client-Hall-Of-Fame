package juul.module.movement;

import juul.event.Event;
import juul.event.EventUpdate;
import juul.inject.wrappers.TimerWrapper;
import juul.module.Module;
import juul.module.settings.NumberSetting;
import juul.util.objects.Timer;

public class Boost extends Module {

	public NumberSetting speed, duration;

	private final Timer tDuration = new Timer();

	protected float flPreTimer;

	public Boost() {
		super("Boost", "temp timer", 0, Category.MOVEMENT);
		addSetting(speed = new NumberSetting("Speed", "Timer Speed", 1.1, 0.1, 10, 0.1));
		addSetting(duration = new NumberSetting("Duration", "Timer Speed", 20, 1, 200, 1));
	}

	@Override
	public void onEnable()
	{
		TimerWrapper timer = new TimerWrapper(constants.getTimer());
		tDuration.reset();
		flPreTimer = timer.getTimerSpeed();
		super.onEnable();
	}

	@Override
	public void onDisable()
	{
		TimerWrapper timer = new TimerWrapper(constants.getTimer());
		timer.setTimerSpeed(flPreTimer);
		super.onDisable();
	}

	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			TimerWrapper timer = new TimerWrapper(constants.getTimer());
			timer.setTimerSpeed(speed.getValueFloat());

			if (tDuration.hasTimeElapsed(duration.getValueLong() * 50, true)) this.disable();
		}
	}
	
}
