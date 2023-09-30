package markgg.modules.player;

import markgg.modules.Module.Category;
import markgg.settings.NumberSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.Client;
import markgg.modules.Module;

public class VClip extends Module{	

	public NumberSetting height = new NumberSetting("Blocks", this, 5, -10, 10, 1);

	public VClip() {
		super("VClip", "Clips you up", 0, Category.PLAYER);
		addSettings(height);
	}

	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + height.getValue(), mc.thePlayer.posZ);
		toggle();
	}
}
