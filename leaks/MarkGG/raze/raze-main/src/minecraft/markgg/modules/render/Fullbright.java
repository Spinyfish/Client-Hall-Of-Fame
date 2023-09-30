package markgg.modules.render;

import markgg.modules.Module;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;

public class Fullbright extends Module {

	public Fullbright() {
		super("Fullbright", "Disables shadows", 0, Module.Category.RENDER);
	}

	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		mc.gameSettings.gammaSetting = 100.0F;
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.gameSettings.gammaSetting = 1.0F;
	}
}
