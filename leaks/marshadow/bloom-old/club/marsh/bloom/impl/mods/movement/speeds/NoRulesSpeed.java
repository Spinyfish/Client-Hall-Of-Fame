package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NoRulesSpeed extends Mode {

	int ticks;

	public NoRulesSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onEnable() {
		//mc.timer.timerSpeed = 1.05f;
	}
	
	@Override
	public void onDisable() {
		//mc.timer.timerSpeed = 1f;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround) {
			MovementUtils.setSpeed(0.45);
			mc.thePlayer.jump();
			ticks = 1;
		} else {
			MovementUtils.setSpeed(0.45);
		}
	}

	@Subscribe
	public void onPacket(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (e.getPacket() instanceof S08PacketPlayerPosLook) {
			if (ticks == 1) {
				e.setCancelled(true);
				ticks = 0;
			}
		}
	}


}
