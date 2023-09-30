package markgg.modules.render;

import org.lwjgl.input.Keyboard;

import markgg.modules.Module.Category;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;

public class Rotations extends Module{
	public Rotations() {
		super("Rotations", "Cool rotation animation", 0, Category.RENDER);
	}

	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			mc.thePlayer.rotationYawHead = event.getYaw();
			mc.thePlayer.renderYawOffset = event.getYaw();
		} 
	}
}
