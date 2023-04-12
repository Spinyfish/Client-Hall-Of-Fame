package markgg.modules.movement;

import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;

import org.lwjgl.input.Keyboard;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;

public class Sprint extends Module{

	public ModeSetting sprintMode = new ModeSetting("Mode", this, "Legit", "Legit", "Omni");

	public Sprint() {
		super("Sprint", "Sprints for you", 0, Category.MOVEMENT);
		addSettings(sprintMode);
	}

	public void onEvent(Event e) {
		if (e instanceof EventUpdate && e.isPre())
			switch (sprintMode.getMode()) {
			case "Legit":
				if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally)
					mc.thePlayer.setSprinting(true);
				break;
			case "Omni":
				mc.thePlayer.setSprinting(true);
				break;
			}
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.getIsKeyPressed());
	}
}
