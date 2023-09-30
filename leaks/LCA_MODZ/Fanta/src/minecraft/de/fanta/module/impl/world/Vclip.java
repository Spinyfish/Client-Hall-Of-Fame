package de.fanta.module.impl.world;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;

public class Vclip extends Module {
	public Vclip() {
		super("Vclip", 0, Type.World, Color.orange);
	}

	@Override
	public void onEvent(Event event) {
		if (mc.gameSettings.keyBindSneak.isPressed()) {
			if(!Client.INSTANCE.moduleManager.getModule("Flight").isState()) {
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3, mc.thePlayer.posZ);
			if (mc.theWorld == null || mc.thePlayer == null)
				if (mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater()) {
					mc.thePlayer.motionY -= 6;
				}
			}
		}
	}
}
