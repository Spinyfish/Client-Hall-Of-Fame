package intentions.modules.player;

import intentions.modules.Module;

public class LiquidInteract extends Module {

	public LiquidInteract() {
		super("LiquidInteract", 0, Category.PLAYER, "Allow you to interact with liquids", true);
	}
	
	public static boolean li = false;
	
	public void onEnable() {
		li = true;
	}
	public void onDisable() {
		li = false;
	}
	
}
