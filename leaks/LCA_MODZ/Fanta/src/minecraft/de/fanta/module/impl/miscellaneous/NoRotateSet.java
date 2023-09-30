package de.fanta.module.impl.miscellaneous;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotateSet extends Module {
	public NoRotateSet() {
		super("NoRotateSet", 0, Type.Misc, Color.RED);
	}

	@Override
	public void onEvent(Event event) {

		if (event instanceof EventReceivedPacket) {
			Packet p = EventReceivedPacket.INSTANCE.getPacket();
			if (p instanceof S08PacketPlayerPosLook) {
				final S08PacketPlayerPosLook Look = (S08PacketPlayerPosLook) p;
				Look.yaw = mc.thePlayer.rotationYaw;
				Look.pitch = mc.thePlayer.rotationPitch;
			}
		}
	}

}