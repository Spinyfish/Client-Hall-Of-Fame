package markgg.modules.misc;

import org.lwjgl.input.Keyboard;

import markgg.events.Event;
import markgg.events.EventMove;
import markgg.events.listeners.EventPacket;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.modules.misc.disabler.PacketSleepThread;
import markgg.settings.ModeSetting;
import markgg.util.MathUtil;
import markgg.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import org.apache.commons.lang3.RandomUtils;

public class Disabler extends Module {

	public Timer timer = new Timer();

	public ModeSetting mode = new ModeSetting("Mode", this, "VerusCmbt", "VerusCmbt", "OldVerus");

	public Disabler() {
		super("Disabler", "Disables anticheats", 0, Category.MISC);
		addSettings(mode);
	}

	public void onEvent(Event e) {
		if (e instanceof EventPacket && e.isIncoming()) {
			switch (mode.getMode()) {
			case "VerusCmbt":
				Packet packet = EventPacket.getPacket();
				C0FPacketConfirmTransaction C0FPacket = (C0FPacketConfirmTransaction)EventPacket.getPacket();
				if (packet instanceof C0FPacketConfirmTransaction) {
					(new PacketSleepThread((Packet)C0FPacket, RandomUtils.nextInt(6000, 12000))).start();
					e.setCancelled(true);
				}
				break;
			case "OldVerus":
				Packet packet1 = EventPacket.getPacket();
				if (packet1 instanceof C0FPacketConfirmTransaction || packet1 instanceof C00PacketKeepAlive) {
					e.setCancelled(true);
				}
				break;
			}
		}
	}

}
