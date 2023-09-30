package axolotl.cheats.modules.impl.render;

import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.BooleanSetting;

public class HUD extends Module {

	public BooleanSetting BPS = new BooleanSetting("BPS", true);

	public HUD() {
		super("HUD", 0, Category.RENDER, true, true);
		this.addSettings(BPS);
		this.toggled = true;
	}
	
}
