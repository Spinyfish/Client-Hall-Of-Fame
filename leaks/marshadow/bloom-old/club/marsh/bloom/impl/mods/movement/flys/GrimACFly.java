package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GrimACFly extends Mode {

	int ticks;
	private final Deque<Packet<?>> packetDeque = new ConcurrentLinkedDeque<>();

	public GrimACFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
	}

	@Override
	public void onDisable() {
		if (!this.canBeUsed()) return;
		ticks = 0;
		while (!packetDeque.isEmpty())
			mc.getNetHandler().addToSendQueueSilent(packetDeque.poll());
		packetDeque.clear();
		mc.thePlayer.speedInAir = 0.02f;
		//mc.gameSettings.keyBindUseItem.pressed = false;
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (ticks % 60 + (int) (Math.random()) == 0) {
			if (packetDeque.size() > 15) {
				ticks -= 5;
				for (int i = 10; i > 0; --i)
					mc.getNetHandler().addToSendQueueSilent(packetDeque.poll());
			}
		}
		if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).down()).getBlock() == Blocks.air) {
			mc.gameSettings.keyBindSneak.pressed = true;
		} else {
			mc.gameSettings.keyBindSneak.pressed = mc.thePlayer.ticksExisted % 4 == 0;
		}
		mc.rightClickDelayTimer = 0;
		//mc.thePlayer.rotationYaw = RotationUtils.getlookdir(new Vec3(Math.round(mc.thePlayer.posX)+0.5,mc.thePlayer.posY-0.5,Math.round(mc.thePlayer.posZ)+0.5))[0];
		//e.pitch = RotationUtils.getlookdir(new Vec3(Math.round(mc.thePlayer.posX)+0.5,mc.thePlayer.posY-0.5,Math.round(mc.thePlayer.posZ)+0.5))[1];
		ticks++;
	}

	@Subscribe
	public void onPacket(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.ticksExisted <= 1)
			packetDeque.clear();
		if (e.getPacket().getClass().getName().contains("client")) {
			e.setCancelled(true);
			packetDeque.add(e.getPacket());
		};
	}

	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;

	}
}
