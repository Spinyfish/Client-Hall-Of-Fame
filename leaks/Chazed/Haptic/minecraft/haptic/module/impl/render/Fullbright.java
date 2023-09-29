package haptic.module.impl.render;

import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;

public class Fullbright extends Module {

	public Fullbright() {
		super("Fullbright", Category.RENDER, ListeningType.NEVER);
	}
	
	public void onEnable() {
		mc.gameSettings.gammaSetting = 100;
	}
	
	public void onDisable() {
		mc.gameSettings.gammaSetting = 1;
	}

}
