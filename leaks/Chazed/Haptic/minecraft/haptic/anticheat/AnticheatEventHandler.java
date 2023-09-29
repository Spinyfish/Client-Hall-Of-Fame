package haptic.anticheat;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventPlayerPosUpdate;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventUpdate;
import haptic.util.base.BaseUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

public class AnticheatEventHandler implements BaseUtil {
	
	public static void handle(Event e) {
		if(e instanceof EventUpdate) {
			if(mc.thePlayer.ticksExisted == 20) {
				for(Entity entity : mc.theWorld.getLoadedEntityList()) {
					if(entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if(player != mc.thePlayer) {
							ChecksManager.addPlayer(player);
						}
					}
				}
			}
		} else if(e instanceof EventReceivePacket) {
			EventReceivePacket event = (EventReceivePacket) e;
			if(mc.thePlayer.ticksExisted > 20) {
				if(event.getPacket() instanceof S0CPacketSpawnPlayer) {
					S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) event.getPacket();
					if(packet.getEntityPlayer() != mc.thePlayer) {
						ChecksManager.addPlayer(packet.getEntityPlayer());
					}
				}
			}
		} else if(e instanceof EventPlayerPosUpdate) {
			EventPlayerPosUpdate event = (EventPlayerPosUpdate) e;
			if(mc.thePlayer.ticksExisted > 20) {
				for(User user : ChecksManager.getUsers()) {
					if(user.getPlayer() != null) {
						if(user.getPlayer().equals(event.getPlayer())) {
							for(Check check : user.getChecks()) {
								check.handle();
							}
						}
					}
				}
			}
		}
	}
	
}