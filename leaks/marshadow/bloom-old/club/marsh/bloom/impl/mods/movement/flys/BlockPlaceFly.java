package club.marsh.bloom.impl.mods.movement.flys;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.UpdateEvent;

public class BlockPlaceFly extends Mode {
	

	public BlockPlaceFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround || (mc.thePlayer.fallDistance > 0 && mc.thePlayer.ticksExisted % 2 == 0)) {
			mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ), 1, null, 0f, 1f, 0f));
		}
		if (mc.thePlayer.onGround)
			mc.thePlayer.jump();
	}
	
	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.fallDistance > 0.25f || mc.thePlayer.fallDistance == 0)
			MovementUtils.collide(e);
	}
}
