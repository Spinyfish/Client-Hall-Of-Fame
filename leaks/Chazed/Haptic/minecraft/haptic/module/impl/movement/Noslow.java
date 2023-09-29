package haptic.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventPostMotionUpdate;
import haptic.event.impl.EventSendPacket;
import haptic.event.impl.EventSlowdown;
import haptic.module.Category;
import haptic.module.Module;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.network.PacketUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

public class Noslow extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP");
	public NumberSetting forward = new NumberSetting("Forward", 1, 0.2, 1, 0.05, this);
	public NumberSetting strafe = new NumberSetting("Strafe", 1, 0.2, 1, 0.05, this);
	
	private boolean stoppedBlocking = true;
	
	public Noslow() {
		super("Noslow", Category.MOVEMENT);
		this.addSettings(mode, forward, strafe);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
			
			if(player.isUsingItem()) {
				if(mode.is("NCP")) {
					if(player.isUsingSword()) {
						packet.releaseUseItem(false);
					}
				}
			}
		} else if(event instanceof EventPostMotionUpdate) {
			if(mode.is("NCP")) {
				if(player.isUsingSword()) {
					packet.sendBlocking(false, false);
				}
			}
		}  else if(event instanceof EventSlowdown) {
			EventSlowdown e = (EventSlowdown) event;
			if(forward.getValue() == 1 && strafe.getValue() == 1) {
				e.setCancelled(true);
			} else {
				e.setForward((float) forward.getValue());
				e.setStrafe((float) strafe.getValue());
			}
		}
	}

}
