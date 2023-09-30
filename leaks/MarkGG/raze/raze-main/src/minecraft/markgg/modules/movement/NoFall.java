package markgg.modules.movement;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module{	

	public ModeSetting fallMode = new ModeSetting("Mode", this, "Packet", "Packet", "Verus", "AAC");
	
	public NoFall() {
		super("NoFall", "Disables fall damage", 0, Category.MOVEMENT);
		addSettings(fallMode);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			switch (fallMode.getMode()) {
			case "Packet":
				if (mc.thePlayer.fallDistance > 2.0f) {
					mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer(true));
				}
				break;
			case "Verus":
				if(mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
					mc.thePlayer.motionY = 0.0;
					mc.thePlayer.fallDistance = 0.0f;
					mc.thePlayer.motionX *= 0.6;
					mc.thePlayer.motionZ *= 0.6;
					mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer(true));
				}
				break;
			case "AAC":
				if(mc.thePlayer.isAirBorne && mc.thePlayer.fallDistance  >= 3){
		            mc.timer.timerSpeed = 0.1f;
		            mc.thePlayer.motionY -= 0.965F;
		            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,true));
		        }else {
		        	mc.timer.timerSpeed = 1.0f;
		        }
				break;
			}
		}
	}
	
	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.timer.timerSpeed = 1;
	}
}
