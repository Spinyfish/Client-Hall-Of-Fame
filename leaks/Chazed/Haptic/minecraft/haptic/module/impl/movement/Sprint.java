package haptic.module.impl.movement;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.network.PacketUtils;

public class Sprint extends Module {
	
	private BooleanSetting omnisprint = new BooleanSetting("Omnisprint", false, this);
	
	public Sprint() {
		super("Sprint", Category.MOVEMENT);
		this.setEnabledSilently(true);
		this.addSettings(omnisprint);
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindSprint.pressed = false;
		if(omnisprint.isEnabled()) {
			mc.thePlayer.setSprinting(false);
		}
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(omnisprint.isEnabled()) {
				mc.thePlayer.setSprinting(true);
			} else {
				mc.thePlayer.setSprinting(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.getFoodStats().getFoodLevel() > 6);
				if(mc.thePlayer.isUsingItem() && !Haptic.getModuleManager().getModuleByName("Noslow").isEnabled()) {
					mc.thePlayer.setSprinting(false);
				}
			}
		}
	}

}