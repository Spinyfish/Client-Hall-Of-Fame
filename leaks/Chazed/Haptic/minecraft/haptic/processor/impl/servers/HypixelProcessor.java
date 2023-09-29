package haptic.processor.impl.servers;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventSendPacket;
import haptic.module.impl.combat.Killaura;
import haptic.util.base.BaseUtil;
import haptic.util.misc.MathUtils;
import haptic.util.movement.MovementUtils;
import haptic.util.network.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class HypixelProcessor implements BaseUtil {
	
	public void onEvent(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			
			//Hypixel strafe disabler
			
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				if(mc.getNetHandler().doneLoadingTerrain && mc.thePlayer.ticksExisted < 100) {
					S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
		            e.setCancelled(true);
		            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.getX(), packet.getY(), packet.getZ(), false));
				}
	        }
		} else if(event instanceof EventMotionUpdate) {
			if(mc.thePlayer.ticksExisted < 10) {
				//Makes sure you aren't pingspoofing with transactions, that silent flags and reduces your playtime when staff is active.
				Haptic.getPacketsProcessor().setMovePacketsDelay(0);
				Haptic.getPacketsProcessor().setPingPacketsDelay(0);
			}
		}
	}
	
}