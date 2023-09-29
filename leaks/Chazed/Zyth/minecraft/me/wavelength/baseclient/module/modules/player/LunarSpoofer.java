package me.wavelength.baseclient.module.modules.player;

import java.io.ByteArrayOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class LunarSpoofer extends Module {

	public LunarSpoofer() {
		super("LunarSpoofer", "Spoof la lunar.gg", 0, Category.PLAYER, AntiCheat.LUNAR);
	}

	@Override
	public void setup() {
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUpdate(UpdateEvent event) {
	}

	@Override
	public void onPacketSent(PacketSentEvent event) {
		
		if(event.getPacket() instanceof C17PacketCustomPayload) {
			
			C17PacketCustomPayload pay = (C17PacketCustomPayload)event.getPacket();
			if(pay.getChannelName().equalsIgnoreCase("MC|Brand")) {
					mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().writeBytes("Lunar-Client".getBytes()))));
					Utils.message = "You are now on lunar client!";
					Utils.print();
			}
		}
		
	}

}