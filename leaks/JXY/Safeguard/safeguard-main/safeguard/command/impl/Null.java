package intentions.command.impl;

import intentions.Client;
import intentions.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class Null extends Command {

	  private Minecraft mc = Minecraft.getMinecraft();
	
	  public Null() {
		  super("Null", "Null a server with packets ;D", "null", new String[] {"null", "n"});
	  }
	
	  public void onCommand(String[] args, String command) {
		  AxisAlignedBB bounding = mc.thePlayer.boundingBox;
		  for(int i=0;i<100;i++) {
			  if(mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.equalsIgnoreCase("eu.loyisa.cn") && i < 20) {
				  if(i % 2 == 0) {
					  mc.thePlayer.sendChatMessage("/sethealth 1");
				  } else {
					  mc.thePlayer.sendChatMessage("/sethealth 20");
				  }
			  }
			mc.thePlayer.swingItem();
			mc.thePlayer.boundingBox = new AxisAlignedBB(-1, -1, -1, -1, -1, -1);
		  	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook((float) Math.random(), (float) Math.random(), (mc.thePlayer.ticksExisted % 2 == 0 ? true : false)));
		  }
		  mc.thePlayer.boundingBox = bounding;
		  mc.thePlayer.sendChatMessage(mc.getCurrentServerData().serverIP.equalsIgnoreCase("eu.loyisa.cn") ? "/sethealth 20" : ";)");
	  }
	
}
