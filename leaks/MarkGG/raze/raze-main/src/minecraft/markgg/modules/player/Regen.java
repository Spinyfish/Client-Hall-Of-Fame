package markgg.modules.player;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module{

	public ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "Fast");

	public Regen() {
		super("Regen", "Regenerates your health", 0, Category.PLAYER);
		addSettings(mode);
	}

	public void onEvent(Event e) {
		if(e instanceof EventUpdate && e.isPre()) {
			switch (mode.getMode()) {
			case "Vanilla":
				mc.thePlayer.setHealth(20);
				mc.thePlayer.getFoodStats().setFoodLevel(20);
				break;
			case "Fast":
				if(mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth())
					for (int i = 0; i < 10; i++) {
						mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
					}
				break;
			}
		}
	}
	
	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		switch (mode.getMode()) {
		case "Vanilla":
			Client.addChatMessage("This mode only works on vanilla 1.8 servers!");
			break;
		case "Fast":
			Client.addChatMessage("This mode needs food to work!");
			break;
		}
	}

}
