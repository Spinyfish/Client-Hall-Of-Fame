package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.events.Event;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.modules.player.NoSlowdown;
import intentions.settings.BooleanSetting;

public class Sprint extends Module {
	
	public BooleanSetting omni = new BooleanSetting("Omni", false);
	
	public Sprint() {
		super("Sprint", Keyboard.KEY_N, Category.MOVEMENT, "Automatically makes you sprint", true);
		this.addSettings(omni);
	}
	
	public void onDisable() {
		mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.getIsKeyPressed());
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre()) {
				if(omni.isEnabled() && mc.thePlayer.moveForward < 0) {
					if(mc.thePlayer.motionZ < 0 && mc.thePlayer.motionZ > -0.15132167) Math.min(0.1132167f, mc.thePlayer.motionZ * 1.1);
					if(mc.thePlayer.motionX < 0 && mc.thePlayer.motionX > -0.15132167) Math.min(0.1132167f, mc.thePlayer.motionX * 1.1);
				}
				if((omni.isEnabled() || mc.thePlayer.moveForward > 0) && mc.thePlayer.moveForward != 0 && (!mc.thePlayer.isUsingItem() || NoSlowdown.noSlowdown) && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking()) {
					mc.thePlayer.setSprinting(true);
				}
			}
		}
	}
}
