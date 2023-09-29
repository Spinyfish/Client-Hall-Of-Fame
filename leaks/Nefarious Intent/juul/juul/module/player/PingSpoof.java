package juul.module.player;

import juul.event.Event;
import juul.event.EventPacket;
import juul.event.EventUpdate;
import juul.module.Module;
import juul.module.settings.RangeSetting;
import juul.util.value.Random;
import net.minecraft.network.Packet;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PingSpoof extends Module {

	public RangeSetting delay;

	protected LinkedList<Packet> packets = new LinkedList();

	class ExecutePacket extends TimerTask {

		private final Packet packet;

		ExecutePacket(Packet p)
		{
			this.packet = p;
		}

		@Override
		public void run()
		{
			// sendpacketnoevent(this.packet)
		}
	}

	public PingSpoof() {
		super("PingSpoof", "", 0, Category.PLAYER);
		addSetting(delay = new RangeSetting("Delay", "asd", 100, 125, 0, 200, 5));
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			for (Packet p : packets)
			{
				double dbDelay = Random.dbRandom(delay.getStart(), delay.getEnd());
				new Timer().schedule(new ExecutePacket(p), (long) dbDelay);
				packets.remove(p);
			}
		}
		else if (e instanceof EventPacket)
		{
			// HOOK THIS EVENT TODO:
			EventPacket event = (EventPacket) e;
			event.cancelled = true;
			packets.add(event.getPacket());
		}

	}


	
}
