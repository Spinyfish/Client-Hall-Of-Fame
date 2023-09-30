package de.fanta.module.impl.movement;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowDown extends Module {
	public NoSlowDown() {
		super("NoSlowDown", 0, Type.Movement, Color.YELLOW);
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventTick) {
			if (mc.thePlayer.isBlocking() && (mc.thePlayer.motionX != 0.0 || mc.thePlayer.motionZ != 0.0)) {

				// mc.getNetHandler().addToSendQueue(new
				// C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
				// BlockPos.ORIGIN, EnumFacing.DOWN));
			}

			if (mc.thePlayer.isBlocking() && (mc.thePlayer.motionX != 0.0 || mc.thePlayer.motionZ != 0.0)) {

				// mc.getNetHandler().addToSendQueue(new
				// C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
			}

		}
	}

}
