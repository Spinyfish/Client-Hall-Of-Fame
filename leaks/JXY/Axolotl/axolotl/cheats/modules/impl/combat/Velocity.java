package axolotl.cheats.modules.impl.combat;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.NumberSetting;

public class Velocity extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Stop", "Jump", "AGC");
	public NumberSetting horizontal = new NumberSetting("Horizontal", 0, 0, 100, 5);
	public NumberSetting vertical = new NumberSetting("Vertical", 0, 0, 100, 5);
	
	public Velocity() {
		super("Velocity", Category.COMBAT, true);
		this.mode.getSettingCluster("Stop").addSettings(vertical, horizontal);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}
	
	public void onEvent(Event event) {
    	if(mc.thePlayer == null || !(event instanceof EventUpdate) || event.eventType != EventType.PRE) return;
    	
    	switch(mode.getMode()) {
    	
    		case "Stop":
    			double h = horizontal.getNumberValue() / 100;
				double v = vertical.getNumberValue() / 100;
    			
		    	if(mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime - 1) {
		    		mc.thePlayer.motionX *= h;
		    		mc.thePlayer.motionY *= v;
		    		mc.thePlayer.motionZ *= h;
		    	}
		    	break;
		    	
	    	default:
	    		break;
    	}
	}
    	
}
