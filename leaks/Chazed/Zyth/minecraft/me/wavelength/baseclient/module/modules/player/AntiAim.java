package me.wavelength.baseclient.module.modules.player;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;

public class AntiAim extends Module {

	public AntiAim() {
		super("AntiAim", "Spinny", 0, Category.MOVEMENT, AntiCheat.VANILLA);
	}
	
	private int spinny;

	@Override
	public void setup() {
	}

	@Override
	public void onEnable() {
		spinny = 0;
	}

	@Override
	public void onDisable() {
	}
	
    public void onPacketSent(PacketSentEvent event) {
    	
    	if(mc.thePlayer.ticksExisted >= 20) {
        	if(event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
        		C05PacketPlayerLook C05 = (C05PacketPlayerLook)event.getPacket();
        		if(C05.getYaw() != spinny) {
        			event.setCancelled(true);
        			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(spinny, C05.getPitch(), C05.isOnGround()));
        			Utils.message = "Fail.";
        			//Utils.print();
        		}
        	}

        	if(event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
        		C06PacketPlayerPosLook C06 = (C06PacketPlayerPosLook)event.getPacket();
        		if(C06.getYaw() != spinny) {
        			event.setCancelled(true);
        			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(C06.getPositionX(), C06.getPositionY(), C06.getPositionZ(), spinny, C06.getPitch(),  mc.thePlayer.onGround));
        			Utils.message = "Fail.";
        			//Utils.print();
        		}
        	}
    	}

    }

	@Override
	public void onUpdate(UpdateEvent event) {
		spinny = spinny + 50;		

	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
	}

}