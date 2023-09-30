package me.wavelength.baseclient.module.modules.player;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Phase extends Module {

	public Phase() {
		super("Phase", "Clip thru blocks", 0, Category.PLAYER, AntiCheat.VCLIP, AntiCheat.MINEMORA);
	}

	@Override
	public void setup() {
		moduleSettings.addDefault("distance", 4D);
	}

	@Override
	public void onEnable() {
		if(this.antiCheat == AntiCheat.VCLIP) {
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - this.moduleSettings.getDouble("distance"), mc.thePlayer.posZ);
			toggle();
		}
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUpdate(UpdateEvent event) {
		if(this.isToggled()) {
			if(this.antiCheat == AntiCheat.MINEMORA) {
				
				if(timer.delay(250)) {
	                mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * 1, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * 1);
					timer.reset();
				}

			}
		}

	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
	}

}