package haptic.util.network;

import haptic.Haptic;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketSleepThread extends Thread {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	private Packet packet;
	private long delay;
	
	public PacketSleepThread(Packet packet, long delay) {
		this.packet = packet;
		this.delay = delay;
	}
	
	@Override
	public void run() {
		try {
			sleep(delay);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		if(mc.thePlayer != null && mc.thePlayer.ticksExisted > 0) {
			PacketUtils.sendPacketNoEvent(packet);
		}
	}
	
}
