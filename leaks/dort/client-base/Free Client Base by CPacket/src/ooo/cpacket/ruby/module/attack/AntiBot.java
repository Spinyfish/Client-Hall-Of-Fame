package ooo.cpacket.ruby.module.attack;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.player.EventMotionUpdate;
import ooo.cpacket.ruby.module.Module;

public class AntiBot extends Module {

	public AntiBot(String name, int key, Category category) {
		super(name, key, category);
	}

	@Override
	public void onEnable() {
		System.out.println("XD");
	}

	@Override
	public void onDisable() {}
	
	public static boolean isEntityValidTarget(Entity o) {
		if (o instanceof EntityVillager) {
			return true;
		}
		return o.hasSwung();
	}
	
	@EventImpl
	public void onUpdate(EventMotionUpdate e) {
		for (Entity o : (List<Entity>)mc.theWorld.loadedEntityList) {
			o.swingCheck();
		}
	}
	
}
