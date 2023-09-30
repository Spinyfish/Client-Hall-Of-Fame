package markgg.modules.render;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;

public class NoWeather extends Module {
	
	public NoWeather() {
		super("NoWeather", "Disables weather", 0, Module.Category.RENDER);
	}

	public void onEvent(Event e) {
		if (e instanceof EventUpdate) {
			mc.theWorld.setThunderStrength(0.0F);
			mc.theWorld.setRainStrength(0.0F);
		}
	}
}
