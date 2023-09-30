package axolotl.cheats.modules.impl.player;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;

public class Timer extends Module {

	public NumberSetting timer = new NumberSetting("Timer", 1, 0.1, 5, 0.1);

	public Timer() {
		super("Timer", Category.PLAYER, true);
		this.addSettings(timer);
		this.setSpecialSetting(timer);
	}
	
	public void onEvent(Event e) {
		
		if(!(e instanceof EventUpdate) || e.eventType != EventType.PRE)return;

		mc.timer.timerSpeed = (float)timer.getNumberValue();

	}
	
}
