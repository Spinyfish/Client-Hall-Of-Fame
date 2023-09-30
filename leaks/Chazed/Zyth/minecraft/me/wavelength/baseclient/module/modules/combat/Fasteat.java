package me.wavelength.baseclient.module.modules.combat;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Fasteat extends Module {

	public Fasteat() {
		super("Fasteat", "Eat zoom", 0, Category.COMBAT, AntiCheat.VANILLA, AntiCheat.HYPIXEL);
	}

	@Override
	public void setup() {
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUpdate(UpdateEvent event) {
		
		if(this.antiCheat == AntiCheat.VANILLA) {
			if(mc.thePlayer.isEating()) {
		        for(int i = 0; i < 35; i++) {
		        	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
		        }
			}	
		}
		
		if(this.antiCheat == AntiCheat.HYPIXEL) {
			if(mc.thePlayer.isEating()) {
				
		        for(int i = 0; i < 3; i++) {
		        	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
		        }
			}	
		}

		
	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
	}

}