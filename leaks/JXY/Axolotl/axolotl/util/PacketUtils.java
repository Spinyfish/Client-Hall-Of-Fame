package axolotl.util;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;

public class PacketUtils {

	private static Minecraft mc = Minecraft.getMinecraft();
	
	// Packet - No event state
	private static ConcurrentHashMap<Packet, Boolean> noEvent = new ConcurrentHashMap<Packet, Boolean>();
	
	public static void sendPacketNoEvent(Packet p) {
		noEvent.put(p, true);
		mc.thePlayer.sendQueue.addToSendQueue(p);
	}
	
	public static boolean shouldSend(Packet p) {
		if(noEvent.get(p) == null)return true;
		return !noEvent.get(p).booleanValue();
	}
	
	public static void overrideSendState(Packet p, boolean state) {
		noEvent.put(p, state);
	}
	
}
