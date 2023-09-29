package juul.module.movement;

import juul.event.Event;
import juul.event.EventUpdate;
import juul.module.Module;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", "Automatically sprints for you.", 0, Category.MOVEMENT);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			mc.getGameSettings().keyBindSprint.setPressed(true);
		}
	}
	
}
