package markgg.modules.combat;

import markgg.Client;
import markgg.events.Event;
import markgg.events.EventGetPacket;
import markgg.events.listeners.EventPacket;
import markgg.modules.Module;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module{

	public ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "Watchdog");
	public NumberSetting veloAmnt = new NumberSetting("Amount", this, 25, 1, 100, 1);

	public Velocity() {
		super("Velocity", "Disables knockback", 0, Category.COMBAT);
		addSettings(mode, veloAmnt);
	}

	public void onEvent(Event e) {
		if(e instanceof EventGetPacket) {
			//thanks to DAVIDNE#4672
			EventGetPacket pe = (EventGetPacket)e;
			if(pe.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) ((EventPacket) e).getPacket();

				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					//func_149411_d = getMotionX
					//func_149410_e = getMotionY
					//func_149409_f = getMotionZ
					switch (mode.getMode()) {
					case "Vanilla":
						packet.field_149415_b = (int)(packet.func_149411_d() * (veloAmnt.getValue() / 100));
						packet.field_149416_c = (int)(packet.func_149410_e() * (veloAmnt.getValue() / 100));
						packet.field_149414_d = (int)(packet.func_149409_f() * (veloAmnt.getValue() / 100));
						break;
					case "Watchdog":
						if(Client.isModuleToggled("Speed"))
							return;
						packet.field_149415_b = 0; //motionX
						packet.field_149414_d = 0; //motionZ
						packet.field_149416_c = 0; //motionY
						break;
					}
				}
			}
			if(pe.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion packet1 = (S27PacketExplosion) ((EventPacket) e).getPacket();
				//func_149149_c = getMotionX
				//func_149144_d = getMotionY
				//func_149147_e = getMotionZ
				switch (mode.getMode()) {
				case "Vanilla":
					packet1.field_149152_f = (int)(packet1.func_149149_c() * (veloAmnt.getValue() / 100));
					packet1.field_149153_g = (int)(packet1.func_149144_d() * (veloAmnt.getValue() / 100));
					packet1.field_149159_h = (int)(packet1.func_149147_e() * (veloAmnt.getValue() / 100));
					break;
				case "Watchdog":
					break;
				}
			}
		}
	}

}
