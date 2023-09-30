package markgg.modules.player;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;

public class FastPlace extends Module{	

	public ModeSetting placeMode = new ModeSetting("Mode",  this, "Normal", "Normal", "Auto");

	public FastPlace() {
		super("FastPlace", "Disables placing delay", 0, Category.PLAYER);
	}

	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			switch(placeMode.getMode()) {
			case "Normal":
				mc.rightClickDelayTimer = 0;
				break;
			case "Auto":
				if(mc.currentScreen == null){
					try {
						click();
					} catch (AWTException e1) {
						e1.printStackTrace();
					}
				}
				break;
			}
		}
	}

	private void click() throws AWTException {
		Robot bot = new Robot();
		bot.mousePress(InputEvent.BUTTON3_MASK);
		bot.mouseRelease(InputEvent.BUTTON3_MASK);
	}
	
	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.rightClickDelayTimer = 6;
	}
}
