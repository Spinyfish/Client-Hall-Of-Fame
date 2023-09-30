package igbt.astolfy.module.combat;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.module.ModuleBase;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Velocity extends ModuleBase {

	public Velocity() {
		super("Velocity", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventPacket && ((EventPacket) e).getPacket() instanceof S12PacketEntityVelocity) {
			e.setCancelled(true);
		}
	}

}
