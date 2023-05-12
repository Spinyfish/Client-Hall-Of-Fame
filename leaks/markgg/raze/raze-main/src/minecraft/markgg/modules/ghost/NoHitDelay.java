package markgg.modules.ghost;

import org.lwjgl.input.Keyboard;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.modules.Module.Category;

public class NoHitDelay extends Module {

	public NoHitDelay() {
		super("NoHitDelay", "Adds 1.7 pvp system", 0, Category.GHOST);
	}

	public void onEvent(Event e) {
		if (e instanceof EventUpdate && mc.theWorld != null && mc.thePlayer != null) {
			if (!mc.inGameHasFocus)
				return; 
			mc.leftClickCounter = 0;
		} 
	}

}
