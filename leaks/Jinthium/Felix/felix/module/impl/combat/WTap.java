package felix.module.impl.combat;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketSend;
import felix.module.Module;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class WTap extends Module {
	
	public WTap() {
		super("WTap", 0, ModuleCategory.COMBAT);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	@Handler
	public void onSendPacket(final EventPacketSend event) {
		if (mc.theWorld != null && mc.thePlayer != null) {
			if(event.getPacket() instanceof C02PacketUseEntity) {
				final C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
				if ((packet.getAction() == C02PacketUseEntity.Action.ATTACK) && (packet.getEntityFromWorld(mc.theWorld) != mc.thePlayer) && (mc.thePlayer.getFoodStats().getFoodLevel() > 6)) {
					boolean sprint = mc.thePlayer.isSprinting();
					mc.thePlayer.setSprinting(false);
					mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
					mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					mc.thePlayer.setSprinting(sprint);
				}
			}
		}
	}
}
