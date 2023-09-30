package felix.module.impl.combat;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.NumberValue;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Reach extends Module {
	
	public NumberValue<Float> minReach = new NumberValue<>("Minimum Reach", 3.0f, 3.0f, 6.0f);
	public NumberValue<Float> maxReach = new NumberValue<>("Maximum Reach", 3.2f, 3.0f, 6.0f);
	
	public double currentReach;
	public boolean reverse;
	
	public Reach() {
		super("Reach", 0, ModuleCategory.COMBAT);
		addValues(minReach, maxReach);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	@Handler
	public void onSendPacket(final EventPacketSend e) {
    	if (e.getPacket() instanceof C02PacketUseEntity) {
    		C02PacketUseEntity packet = (C02PacketUseEntity)e.getPacket();
    		if (packet.getAction().equals(Action.ATTACK)) {
    		}
    	}
    	if (e.getPacket() instanceof C03PacketPlayer) {
			currentReach += reverse ? -.005 : .005;
        		
			if (currentReach >= maxReach.getValue()) 
				reverse = true;
			
			else if (currentReach <= minReach.getValue()) reverse = false;
			}
	}
}
