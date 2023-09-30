package axolotl.cheats.modules.impl.movement;

import axolotl.Axolotl;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.impl.world.Scaffold;
import org.lwjgl.input.Keyboard;

import axolotl.cheats.events.Event;
import axolotl.cheats.modules.Module;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", Category.MOVEMENT, true);
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
	}
	
	public void onEvent(Event e) {
		
		if(!(e instanceof EventUpdate) || e.eventType != EventType.PRE)return;
		mc.gameSettings.keyBindSprint.pressed = true;

		Scaffold scaffold = (Scaffold)Axolotl.INSTANCE.moduleManager.getModule("Scaffold");

		if(scaffold.toggled && !scaffold.sprint.isEnabled()) {
			mc.gameSettings.keyBindSprint.pressed = false;
		}

	}
	
}
