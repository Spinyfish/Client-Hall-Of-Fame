package haptic.processor.impl.servers;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventSendPacket;
import haptic.event.impl.EventUpdate;
import haptic.util.base.BaseUtil;
import haptic.util.network.PacketUtils;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class RedeskyProcessor implements BaseUtil {
	
	private boolean lagbacked;
	private int ticksSinceSpawn;
	
	public void onEvent(Event event) {
		if(event instanceof EventUpdate) {
			if(mc.thePlayer.ticksExisted == 3) {
				lagbacked = false;
				ticksSinceSpawn = 0;
			}
			
			if(mc.getNetHandler().doneLoadingTerrain) {
				ticksSinceSpawn++;
			}
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook && mc.getNetHandler().doneLoadingTerrain && ticksSinceSpawn > 3) {
				lagbacked = true;
			}
		}
	}
	
	public int getTicksSinceSpawn() {
		return ticksSinceSpawn;
	}
	
	public boolean gotLagbacked() {
		return lagbacked;
	}
	
}