package markgg.modules.movement;

import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class Step extends Module {

	public Step() {
		super("Step", "Helps climbing blocks", 0, Category.MOVEMENT);
	}

	public void onEvent(Event e){
		if(e instanceof EventMotion && e.isPre()) {
			if(mc.thePlayer.isCollidedHorizontally) {
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.01, mc.thePlayer.posZ);
				mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.01, mc.thePlayer.posZ, false));
				mc.thePlayer.jump();
			}
		}
	}
	
	public void onDisable(){
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.gameSettings.keyBindUseItem.pressed = false;
		mc.timer.timerSpeed = 1;			 
	}
}
