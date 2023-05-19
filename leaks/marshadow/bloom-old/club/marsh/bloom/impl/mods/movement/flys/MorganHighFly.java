package club.marsh.bloom.impl.mods.movement.flys;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;

public class MorganHighFly extends Mode {
	
	int ticks;
	public MorganHighFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
		//mc.thePlayer.motionY = 0.75;
		//original.addMessage("Press space to fly.");
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY-(mc.thePlayer.onGround ? 0.5 : 11), mc.thePlayer.posZ);
		mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
		ticks = 0;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		switch (ticks) {
			case 1:
				mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY+7,mc.thePlayer.posZ);
				mc.thePlayer.jump();
				//MovementUtils.setSpeed(0.65);
				mc.thePlayer.motionY = 0.65;
				ticks = 2;
				break;
			case 2:
				if (!mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0;
					mc.thePlayer.setPosition(e.x, e.y - e.y % 0.1152F, e.z);
					mc.thePlayer.movementInput.jump = false;
					mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ), 1, null, 0f, 1f, 0f));
				}
				break;
			case 0:
				mc.thePlayer.motionX = 0;
				mc.thePlayer.motionZ = 0;
				break;
		}
	}
	
	@Subscribe
	public void onPacket(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (e.getPacket() instanceof S08PacketPlayerPosLook) {
			ticks = 1;
		}
	}
	
}
