package club.marsh.bloom.impl.mods.combat.criticals;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.AttackEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.mods.combat.KillAura;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

public class JumpCriticals extends Mode {


	public JumpCriticals(Module original, String name, Value mode) {
		super(original, name, mode);
	}





	@Subscribe
	public void onAttack(AttackEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround) {
			mc.thePlayer.jump();
		}
	}



}
