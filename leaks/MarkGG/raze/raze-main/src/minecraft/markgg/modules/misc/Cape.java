package markgg.modules.misc;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.util.ResourceLocation;

public class Cape extends Module{

	public ModeSetting capeMode = new ModeSetting("Design", this, "Red", "Red", "Xray", "Minecon1", "Minecon2", "Migration", "Optifine", "Official");

	public Cape() {
		super("Cape", "Gives you a cape", 0, Category.MISC);
		addSettings(capeMode);
	}

	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			mc.thePlayer.setLocationOfCape(new ResourceLocation("Raze/Capes/" + capeMode.getMode() + ".png"));
		}
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.thePlayer.setLocationOfCape(null);
	}

}
