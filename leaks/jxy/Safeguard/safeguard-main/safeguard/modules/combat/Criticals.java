package intentions.modules.combat;

import intentions.Client;
import intentions.modules.Module;

public class Criticals extends Module {

	public Criticals() {
		super("Criticals", 0, Category.COMBAT, "Makes all your hits critical", true);
	};
	
	public static boolean c;
	
	public void onEnable() {
		c  = true;
	}
	public void onDisable() {
		c = false;
	}
	
}
