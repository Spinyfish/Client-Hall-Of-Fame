package igbt.astolfy.module.visuals;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.NumberSetting;

public class ViewBobbing extends ModuleBase {

	public NumberSetting multiplier = new NumberSetting("Multipler", 5, 0.1, 1, 10);
	
	public ViewBobbing() {
		super("ViewBobbing", 0, Category.VISUALS);
		addSettings(multiplier);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
	        mc.thePlayer.cameraYaw = (float)(double)multiplier.getValue() / 20;
		}
	}
	
	

}
