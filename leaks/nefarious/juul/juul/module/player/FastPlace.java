package juul.module.player;

import juul.event.Event;
import juul.event.EventUpdate;
import juul.inject.wrappers.MinecraftWrapper;
import juul.module.Module;
import juul.module.settings.RangeSetting;
import juul.util.value.Random;

public class FastPlace extends Module {

	public RangeSetting speed;

	public FastPlace() {
		super("FastPlace", "place blocks fast af", 0, Category.PLAYER);
		addSetting(speed = new RangeSetting("Speed", "delay between placements", 0, 1, 0, 6, 1));
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			double dbDelaySpeed = Random.dbRandom(speed.start, speed.end);
			mc.setRightClickDelayTimer(Math.min(mc.getRightClickDelayTimer(), (int) dbDelaySpeed));
		}
	}
	
}
