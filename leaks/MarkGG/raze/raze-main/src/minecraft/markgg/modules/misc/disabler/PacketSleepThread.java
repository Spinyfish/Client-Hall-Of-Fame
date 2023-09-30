package markgg.modules.misc.disabler;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketSleepThread extends Thread {
	private Packet packet;
	  
	  private long delay;
	  
	  public PacketSleepThread(Packet packet, long delay) {
	    this.packet = packet;
	    this.delay = delay;
	  }
	  
	  public void run() {
	    try {
	      sleep(this.delay);
	      Minecraft.getMinecraft().getNetHandler().sendPacketNoEvent(this.packet);
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    } 
	  }
}
