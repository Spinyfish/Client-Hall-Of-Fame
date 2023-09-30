package axolotl.cheats.modules.impl.world;

import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;

public class ChestStealer extends Module {

	public NumberSetting stealSpeed = new NumberSetting("StealSpeed", 100, 0, 1000, 10);
	public NumberSetting closeDelay = new NumberSetting("CloseDelay", 250, 0, 1000, 10);
	
	public ChestStealer() {
		super("ChestStealer", Category.WORLD, true);
		this.addSettings(stealSpeed, closeDelay);
		this.setSpecialSetting(stealSpeed);
	}
	
}
