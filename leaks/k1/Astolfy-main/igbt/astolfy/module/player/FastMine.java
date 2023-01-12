package igbt.astolfy.module.player;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.events.listeners.EventUpdate;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.PotionEffect;

public class FastMine extends ModuleBase {
	
	public ModeSetting mode;
	
	public FastMine() {
		super("FastMine", Keyboard.KEY_M, Category.PLAYER);
		mode = new ModeSetting("Mode", "Potion","Packet");
		addSettings(mode);
	}
	
	public void onEvent(Event e) {
		if(mode.getCurrentValue().equalsIgnoreCase("Potion")) {
			if(e instanceof EventUpdate) {
				mc.thePlayer.addPotionEffect(new PotionEffect(3,2,1));
			}
		}else {
			if(e instanceof EventPacket) {
				EventPacket event = (EventPacket)e;
				
				if(event.getPacket() instanceof C07PacketPlayerDigging) {
					//if(mc.thePlayer.ticksExisted % 2 == 1)
						mc.thePlayer.sendQueue.addToSendQueueNoEvent(event.getPacket());
				}
			}
		}
	}
}
