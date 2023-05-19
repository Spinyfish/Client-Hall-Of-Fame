package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.components.BlinkingComponent;
import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.ClipUtils;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.impl.utils.other.Timer;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;

public class VulcanNewFly extends Mode {
	NumberValue speed = new NumberValue("Vulcan Speed",1.0,0,2.5, () -> (canBeUsed()));
	double lastSentPacketX = 0, lastSentPacketY = 0, lastSentPacketZ = 0;
	public Timer timer = new Timer();

	public VulcanNewFly(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(speed);
	}

	@Override
	public void onEnable() {
		timer.reset();
		lastSentPacketY = 0;
	}

	@Override
	public void onDisable() {
		mc.thePlayer.speedInAir = 0.02f;
		BlinkingComponent.INSTANCE.releasePackets();
		BlinkingComponent.INSTANCE.setOn(false);
		BlinkingComponent.INSTANCE.setServerside(false);
	}
	@Subscribe
	public void onPacket(PacketEvent e) {
		//if (e.getPacket() instanceof C03PacketPlayer) {
		//	C03PacketPlayer pos = (C03PacketPlayer) (e.getPacket());
			//if (new BlockPos(lastSentPacketX,lastSentPacketY,lastSentPacketZ).distanceSq(pos.x,pos.y,pos.z) < 7.5) {
			//	e.setCancelled(true);
			//} else {
			//	lastSentPacketX = pos.x;
			//	lastSentPacketY = pos.y;
			//	lastSentPacketZ = pos.z;
			//}
		//}
		//if (e.getPacket() instanceof C0FPacketConfirmTransaction)
		//	e.setCancelled(true);
	}
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (lastSentPacketY == 0) {
			if (mc.thePlayer.onGround)
				mc.thePlayer.jump();
			e.pitch = 90;
			mc.gameSettings.keyBindUseItem.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
			if (MovementUtils.blockUnder(0.4) && !mc.thePlayer.onGround) {
				lastSentPacketY = 1;
			}
		} else {
			if (timer.hasTimeElapsed(2000L, true)) {
				original.addMessage("Dealt with " + BlinkingComponent.INSTANCE.getPacketDeque().size() + " packets.");
				BlinkingComponent.INSTANCE.releasePackets();
			}
			mc.timer.timerSpeed = 0.45f;
			BlinkingComponent.INSTANCE.setOn(true);
			BlinkingComponent.INSTANCE.setServerside(true);
			if (mc.thePlayer.onGround) {
				mc.thePlayer.jump();
				return;
			}
			mc.thePlayer.setPosition(e.x,e.y = Math.round(e.y),e.z);
			//mc.thePlayer.speedInAir = 0f;
			//if (mc.thePlayer.ticksExisted % 2 == 0) {
			//	mc.thePlayer.motionY = 0;
			//	mc.thePlayer.speedInAir = 0.02f;
			//} else {
			//	mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
			//}
			MovementUtils.setPosition(speed.getValDouble());
			mc.thePlayer.motionX = mc.thePlayer.motionZ = mc.thePlayer.motionY = 0;
		}

	}
}
