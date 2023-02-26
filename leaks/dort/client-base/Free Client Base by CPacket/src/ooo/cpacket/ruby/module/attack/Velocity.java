package ooo.cpacket.ruby.module.attack;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import ooo.cpacket.lemongui.settings.Setting;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.network.EventPacket;
import ooo.cpacket.ruby.module.Module;

public class Velocity extends Module {

	public Velocity(String name, int key, Category category) {
		super(name, key, category);
		this.addBool("Cancel", true);
		this.addNumberOption("HVel", 0.0, -100.0, 100.0);
		this.addNumberOption("VVel", 0.0, -100.0, 100.0);
	}
	@Override
	public void onEnable() {
		
	}

	@Override
	public void onDisable() {
		
	}
	
	@EventImpl
	public void onPacket(EventPacket e) {
		if (e.getPacket() instanceof S27PacketExplosion) {
			S27PacketExplosion s27 = (S27PacketExplosion)e.getPacket();
			if (!this.getBool("Cancel")) {
				double hvel = this.getDouble("HVel");
				double vvel = this.getDouble("VVel");
				mc.thePlayer.setVelocity(s27.func_149148_f() * (hvel * 0.00001), s27.func_149143_g() * (vvel * 0.00001), s27.func_149145_h() * (hvel * 0.00001));
			}
			e.setSkip(true);
		}
		if (e.getPacket() instanceof S12PacketEntityVelocity) {
			S12PacketEntityVelocity s12 = (S12PacketEntityVelocity)e.getPacket();
			if (s12.func_149412_c() != mc.thePlayer.getEntityId())
				return;
			if (!this.getBool("Cancel")) {
				double hvel = this.getDouble("HVel");
				double vvel = this.getDouble("VVel");
				mc.thePlayer.setVelocity(s12.field_149415_b * (hvel * 0.00001), s12.field_149416_c * (vvel * 0.00001), s12.field_149414_d * (hvel * 0.00001));
			}
			e.setSkip(true);
		}
	}
}
