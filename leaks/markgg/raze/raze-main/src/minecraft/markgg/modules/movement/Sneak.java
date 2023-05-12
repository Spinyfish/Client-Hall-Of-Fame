package markgg.modules.movement;

import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Sneak extends Module{

	public ModeSetting sneakMode = new ModeSetting("Mode", this, "Legit", "Legit", "Packet");

	public Sneak() {
		super("Sneak", "Always sneaks for you", 0, Category.MOVEMENT);
		addSettings(sneakMode);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			switch (sneakMode.getMode()) {
			case "Legit":
				mc.gameSettings.keyBindSneak.pressed = false;
				break;
			case "Packet":
				mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction((Entity)mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
				break;
			}
		}
	}
	
	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.gameSettings.keyBindSneak.pressed = false;
		mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction((Entity)mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
	}


}
