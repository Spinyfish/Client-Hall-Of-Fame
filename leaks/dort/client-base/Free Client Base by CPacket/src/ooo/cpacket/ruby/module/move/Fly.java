package ooo.cpacket.ruby.module.move;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.network.EventPacket;
import ooo.cpacket.ruby.api.event.events.player.EventMotionUpdate;
import ooo.cpacket.ruby.api.event.events.player.EventRawMove;
import ooo.cpacket.ruby.module.Module;
import ooo.cpacket.ruby.util.SpeedUtils;
import ooo.cpacket.ruby.util.Timer;
import ooo.cpacket.ruby.util.mc.MCTimerHook;

public class Fly extends Module {

	private Timer timer = new Timer();

	public double startY;

	public Fly(String name, int key, Category category) {
		super(name, key, category);
		this.addNumberOption("Speed", 5, 1, 10, true);
		this.addModes("Hypixel", "Guardian", "Vanilla", "Ghostly");
	}

	static class a {
		static double startX, startZ;
	}

	@Override
	public void onEnable() {
		a.startX = mc.thePlayer.posX;
		this.startY = mc.thePlayer.posY;
		a.startZ = mc.thePlayer.posZ;
		if (this.isMode("Mineplex")) {
		} else if (this.isMode("SentinelMeme")) {
			MCTimerHook.timerSpeed = 1.9D;
		} else if (this.isMode("Guardian")) {

		}
	}

	@Override
	public void onDisable() {
		SpeedUtils.setMoveSpeed(0);
		mc.timer.timerSpeed = 1.0f;
	}

	@EventImpl
	public void onPreMotionUpdate(EventRawMove e) {
		// SpeedUtils.setMoveSpeed(e, (mc.thePlayer.ticksExisted % 8) == 0 ? 0.009 :
		// 0.2);
	}

	@EventImpl
	public void onPreMotionUpdate(EventPacket event) {
		if (mc.thePlayer.ticksExisted % 2 != 0) {
			if (event.getPacket() instanceof C03PacketPlayer) {
				// event.setSkip(true);
			}
		}
	}

	@EventImpl
	public void onPreMotionUpdate(EventMotionUpdate event) {
		double speed = this.getDouble("Speed");
		if (this.isMode("Hypixel")) {
			
		}
		if (this.isMode("Guardian")) {
			if (mc.thePlayer.ticksExisted % 2 == 0) {
				event.setPosY(event.getPosY() + 0.000001);
			}
			if (timer.hasReached(1000L)) {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 12, mc.thePlayer.posZ, true));
				timer.reset();
			}
			if (mc.thePlayer.movementInput.jump) {
				mc.thePlayer.motionY = 1.0;
			} else if (mc.thePlayer.movementInput.sneak) {
				mc.thePlayer.motionY = -1.0;
			} else {
				mc.thePlayer.motionY = 0.0;
			}
			SpeedUtils.setMoveSpeed(speed);
		} else if (this.isMode("Vanilla")) {
			if (mc.thePlayer.movementInput.jump) {
				mc.thePlayer.motionY = 1.0;
			} else if (mc.thePlayer.movementInput.sneak) {
				mc.thePlayer.motionY = -1.0;
			} else {
				mc.thePlayer.motionY = 0.0;
			}
			SpeedUtils.setMoveSpeed(speed);
		} else if (this.isMode("Ghostly")) {
			mc.timer.timerSpeed = 0.5;
			if (mc.thePlayer.movementInput.jump) {
				mc.thePlayer.motionY = speed;
			} else if (mc.thePlayer.movementInput.sneak) {
				mc.thePlayer.motionY = -speed;
			} else {
				mc.thePlayer.motionY = 0;
			}
			// mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.000000001,
			// mc.thePlayer.posZ);
			SpeedUtils.setMoveSpeed(speed);
		}
	}

}
