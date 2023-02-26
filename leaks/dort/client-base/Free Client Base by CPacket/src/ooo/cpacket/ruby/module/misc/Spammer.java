package ooo.cpacket.ruby.module.misc;

import org.apache.commons.lang3.RandomStringUtils;

import net.minecraft.network.play.client.C01PacketChatMessage;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.network.EventPacket;
import ooo.cpacket.ruby.api.event.events.player.EventMotionUpdate;
import ooo.cpacket.ruby.module.Module;
import ooo.cpacket.ruby.util.Timer;

public class Spammer extends Module {
	public Timer t = new Timer();

	public Spammer(String name, int key, Category category) {
		super(name, key, category);
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	@EventImpl
	public void onUpdate(final EventMotionUpdate e) {
		if (t.hasReached(1250L)) {
			String b1 = RandomStringUtils.random(6, "abcdef0123456789");
			String b2 = RandomStringUtils.random(6, "abcdef0123456789");
			mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("<" + b1 + ">" + "Client Base by CPacket" + "<" + b2 + ">"));
			t.reset();
		}
	}

	@EventImpl
	public void onPacket(EventPacket e) {

	}

}
