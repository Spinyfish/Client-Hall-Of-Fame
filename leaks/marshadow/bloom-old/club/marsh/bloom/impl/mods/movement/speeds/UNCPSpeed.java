package club.marsh.bloom.impl.mods.movement.speeds;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import club.marsh.bloom.impl.events.UpdateEvent;

public class UNCPSpeed extends Mode {
	

	
	public UNCPSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround) {
			mc.thePlayer.jump();
			if (MovementUtils.getSpeed() < 0.48)
				MovementUtils.setSpeed(0.48);
			else
				MovementUtils.setSpeed(MovementUtils.getSpeed()); //addMessage(MovementUtils.getSpeed() + "");
		} else {
			MovementUtils.setSpeed(MovementUtils.getSpeed());
		}
	}

}
