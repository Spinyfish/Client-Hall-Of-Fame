package ooo.cpacket.ruby.module.attack;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.network.EventPacket;
import ooo.cpacket.ruby.module.Module;
import ooo.cpacket.ruby.module.move.Speed;

public class Criticals extends Module {
	public Criticals() {
		super("Criticals", 0, Category.ATTACK);
		this.addModes("GroundSpoof", "Packet");
	}

	@EventImpl
	public void XD(final EventPacket xd) {
		if (mc.thePlayer == null)
			return;
		if (xd.getPacket() instanceof C03PacketPlayer && this.isMode("GroundSpoof") && mc.thePlayer.onGround /* && !ClientBase.INSTANCE.getModuleManager().getModule(HighJump.class).isEnabled() */ && !ClientBase.INSTANCE.getModuleManager().getModule(Speed.class).isEnabled()) {
			((C03PacketPlayer) xd.getPacket()).field_149474_g = false;
		}
		if (this.isMode("Packet") && mc.thePlayer.isCollidedVertically && xd.getPacket() instanceof C02PacketUseEntity
				&& ((C02PacketUseEntity) xd.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
			mc.thePlayer.upC04(0.0625, false);
			mc.thePlayer.upC04(0, false);
		}
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}
}
