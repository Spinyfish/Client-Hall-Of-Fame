package haptic.module.impl.ghost;

import haptic.event.Event;
import haptic.event.impl.EventReach;
import haptic.module.Category;
import haptic.module.Module;
import haptic.setting.impl.NumberSetting;

public class Reach extends Module {
	
	public NumberSetting reach = new NumberSetting("Reach", 4, 3, 6, 0.05, this);
	
	public Reach() {
		super("Reach", Category.GHOST);
		this.addSettings(reach);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventReach) {
			EventReach e = (EventReach) event;
			e.setReach(reach.getValue());
		}
	}

}
