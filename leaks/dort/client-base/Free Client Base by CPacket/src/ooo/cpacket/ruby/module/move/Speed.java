package ooo.cpacket.ruby.module.move;

import ooo.cpacket.ruby.module.Module;
import ooo.cpacket.ruby.util.Timer;

public class Speed extends Module {

	public Timer timer = new Timer();

	public Speed(String name, int key, Category category) {
		super(name, key, category);
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		
	}

}