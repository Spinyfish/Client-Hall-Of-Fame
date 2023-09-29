package haptic.module.impl.player;

import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.Module;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;

public class Antivoid extends Module {
	
	double oldX, oldY, oldZ;
	
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "OldHypixel");
	private NumberSetting minFallDist = new NumberSetting("Min fall dist", 4, 2, 10, 0.2, this);
	
	public Antivoid() {
		super("Antivoid", Category.PLAYER);
		this.addSettings(mode, minFallDist);
	}
	
	public void onEnable() {
		oldX = mc.thePlayer.posX;
		oldY = mc.thePlayer.posY;
		oldZ = mc.thePlayer.posZ;
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event event) {
		if(mc.thePlayer.ticksExisted < 30) return;
		
		if(event instanceof EventUpdate) {
			if(mc.thePlayer.onGround && world.isBlockUnderPlayer()) {
                oldX = mc.thePlayer.posX;
                oldY = mc.thePlayer.posY;
                oldZ = mc.thePlayer.posZ;
            }
			
			if(shouldSetBack()) {
				if(mode.is("Vanilla")) {
            		mc.thePlayer.setPosition(oldX, oldY, oldZ);
            	}
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			if(shouldSetBack()) {
				if(mode.is("OldHypixel")) {
					e.setY(e.getY() + 9);
				}
			}
		}
	}
	
	private boolean shouldSetBack() {
		return !mc.thePlayer.onGround && !world.isBlockUnderPlayer() && oldY - mc.thePlayer.posY >= minFallDist.getValue();
	}

}
