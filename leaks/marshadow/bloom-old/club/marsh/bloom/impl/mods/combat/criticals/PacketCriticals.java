package club.marsh.bloom.impl.mods.combat.criticals;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.mods.combat.KillAura;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketCriticals extends Mode {


	public PacketCriticals(Module original, String name, Value mode) {
		super(original, name, mode);
	}





	@Subscribe
	public void onPacket(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (e.getPacket() instanceof C03PacketPlayer && mc.thePlayer.onGround && KillAura.target != null && KillAura.toggled && !mc.thePlayer.movementInput.jump && mc.thePlayer.motionY <= 0 && (mc.thePlayer.posY-mc.thePlayer.lastTickPosY) <= 0.1) {
			C03PacketPlayer player = ((C03PacketPlayer) e.getPacket());
			if (player.y % 0.015625 <= 0.001 && player.onGround) {
				player.onGround = false;
				player.y -= mc.thePlayer.ticksExisted % 2 == 0 ? 0.026 : 0.01;
				e.setPacket(player);
			}
		}
	}



}
