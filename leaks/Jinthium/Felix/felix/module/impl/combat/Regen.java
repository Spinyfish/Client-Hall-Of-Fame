package felix.module.impl.combat;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {
	
	public NumberValue<Integer> health = new NumberValue<>("Health", 5, 1, 20);
	public NumberValue<Integer> packets = new NumberValue<>("Packets", 500, 5, 1000);

	public Regen() {
		super("Regen", 0, ModuleCategory.PLAYER);
		addValues(health, packets);
	}
	
	@Handler
	public void onUpdate(final EventMotionUpdate eventPlayerUpdate) {
		if (mc.thePlayer.getHealth() < health.getValue()) {
			for (int i = 0; i < packets.getValue(); i++) {
				mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));			
			}	
		}
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
}
