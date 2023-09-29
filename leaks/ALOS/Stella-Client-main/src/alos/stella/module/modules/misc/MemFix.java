package alos.stella.module.modules.misc;

import alos.stella.event.events.WorldEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;

@ModuleInfo(name = "MemFix", description = "MemFix", category = ModuleCategory.MISC)
public class MemFix extends Module {
	
	public void onEnable() {
        Runtime.getRuntime().gc();
	}
	public void onWorld(WorldEvent event) {
        Runtime.getRuntime().gc();
	}
}
