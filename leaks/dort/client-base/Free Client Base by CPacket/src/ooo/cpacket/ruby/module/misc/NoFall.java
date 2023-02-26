package ooo.cpacket.ruby.module.misc;

import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.player.EventMotionUpdate;
import ooo.cpacket.ruby.module.Module;

public class NoFall extends Module {

	public NoFall(String name, int key, Category category) {
		super(name, key, category);
	}

	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {}
	
	@EventImpl
	public void onUpdate(EventMotionUpdate e) {
		if (mc.thePlayer.fallDistance > 3) {
			e.setOnGround(true);
		}
	}
	
}
