package me.wavelength.baseclient.module.modules.combat;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class AntiBot extends Module {

	public AntiBot() {
		super("Antibot", "Make your balls miss da bots", 0, Category.COMBAT, AntiCheat.MATRIX);
	}
	
	public static boolean enabled;
	public static int mode;

	@Override
	public void setup() {
	}

	@Override
	public void onEnable() {
		enabled = true;
		if(this.antiCheat == AntiCheat.MATRIX) {
			mode = 1;
		}
		
	}

	@Override
	public void onDisable() {
		enabled = false;
	}

	@Override
	public void onUpdate(UpdateEvent event) {
		
		if(this.antiCheat == AntiCheat.MATRIX) {
			mode = 1;
		}
		
	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
	}

}