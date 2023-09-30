package haptic.module.impl.player;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.module.Category;
import haptic.module.Module;
import haptic.setting.impl.ModeSetting;
import haptic.util.network.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Nofall extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Edit", "Edit", "Packet");
	
	private double currentFallDistance;
	private double ticksSpoofedGround;
	private boolean blinking;
	
	public Nofall() {
		super("Nofall", Category.PLAYER);
		this.addSettings(mode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		if(blinking) { // In case you add a nofall that uses blink
			Haptic.getPacketsProcessor().setBlinking(false);
			blinking = false;
		}
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventMotionUpdate) {
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
			
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			if(mc.thePlayer.onGround) {
				currentFallDistance = 0;
				ticksSpoofedGround = 0;
			} else {
				if(mc.thePlayer.motionY < 0) {
					currentFallDistance += -mc.thePlayer.motionY;
				}
			}
			
			switch(mode.getMode()) {
			case "Edit":
				if(currentFallDistance >= 3) {
					e.setOnGround(true);
					currentFallDistance = 0;
				}
				if(blinking) { // In case you add a nofall that uses blink
					Haptic.getPacketsProcessor().setBlinking(false);
					blinking = false;
				}
				break;
			case "Packet":
				if(currentFallDistance >= 2) {
					PacketUtils.sendPacket(new C03PacketPlayer(true));
					currentFallDistance = 0;
				}
				if(blinking) { // In case you add a nofall that uses blink
					Haptic.getPacketsProcessor().setBlinking(false);
					blinking = false;
				}
				break;
			}
		}
	}

}