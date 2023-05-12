package markgg.modules.render;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;

public class AlwaysDay extends Module {

	public AlwaysDay() {
		super("AlwaysDay", "Always day time", 0, Module.Category.RENDER);
	}

	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			mc.theWorld.setWorldTime(1000L);
		}
	}
}
