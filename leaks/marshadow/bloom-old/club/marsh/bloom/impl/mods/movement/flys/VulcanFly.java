package club.marsh.bloom.impl.mods.movement.flys;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;

public class VulcanFly extends Mode {
	
	int ticks;
	boolean teleported = false;
	boolean damaged = false;
	BooleanValue selfDamage = new BooleanValue("Self Damage", true, () -> (this.canBeUsed()));
	public VulcanFly(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(selfDamage);
	}
	
	
	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
		ticks = 0;
		if (!selfDamage.isOn())
			damaged = true;
		else
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY-(mc.thePlayer.onGround ? 0.5 : 11), mc.thePlayer.posZ);
		//mc.thePlayer.motionY = 0.75;
	}
	
	@Override
	public void onDisable() {
		if (!this.canBeUsed()) return;
		mc.timer.timerSpeed = 1f;
		mc.thePlayer.speedInAir = 0.02f;
		teleported = false;
		damaged = false;
		ticks = 0;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
			if (!damaged) {
				if (teleported) {
					mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY+4.01,mc.thePlayer.posZ);
					teleported = false;
				}
				mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
				mc.thePlayer.speedInAir = 0;
				mc.thePlayer.onGround = false;
				if (mc.thePlayer.hurtTime != 0) {
					mc.thePlayer.speedInAir = 0.02f;
					damaged = true;
					ticks = 0;
				} else return;
			}
			if(mc.thePlayer.hurtTime != 0) {
				mc.timer.timerSpeed = 0.5f;
				switch (ticks) {
					case 0:
						//mc.getNetHandler().addToSendQueueSilent(new C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,true));
						//mc.getNetHandler().addToSendQueueSilent(new C04PacketPlayerPosition(mc.thePlayer.posX + (mc.thePlayer.getLookVec().xCoord*1.5),mc.thePlayer.posY + 1,mc.thePlayer.posZ + (mc.thePlayer.getLookVec().zCoord*1.5),false));
						mc.thePlayer.motionY = 1f;
						//mc.timer.timerSpeed = 0.1f;
						break;
					case 1: //revert motion again. set speed
						e.y -= 11;
						mc.thePlayer.jump();
						//mc.thePlayer.motionY = 0.85f;
					//mc.timer.timerSpeed = 0.5f;
					//MovementUtils.setSpeed(0.95);
						break;
					case 2:
						//MovementUtils.setSpeed(0.75);
						//mc.getNetHandler().addToSendQueueSilent(new C04PacketPlayerPosition(mc.thePlayer.posX + -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * 2,mc.thePlayer.posY + 2, mc.thePlayer.posZ + Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * 2,true));
						mc.thePlayer.jump();
						break;
					case 3: //revert motion to be more FUNNY
						mc.thePlayer.motionY = (double) (0.42f);
						break;
					case 4:
						break;
					case 5:
					case 7:
						if (mc.thePlayer.motionY > 0)
							mc.thePlayer.motionY = 0;
						break;
					case 6:
						e.y -= 11;
						break;
					//case 8:  //revert to 6th tick (0.24813599859094576 7 2).
					//	mc.thePlayer.motionY = 0.25813599859094576;
					//	break;
				}
				//if (flag.isOn()) 
				//if (teleported) {
				//	original.addMessage("vanilla teleported");
				//	mc.getNetHandler().addToSendQueueSilent(new C06PacketPlayerPosLook(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,mc.thePlayer.rotationYaw,mc.thePlayer.rotationPitch,false));
				//	//mc.getNetHandler().addToSendQueueSilent(new C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+9,mc.thePlayer.posZ,false));
				//	mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY+9,mc.thePlayer.posZ);
				//	teleported = false;
				//}
				ticks++;
		  } else {
			  mc.timer.timerSpeed = 1f;
			  ticks = 0;
			  if (mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.motionY < -0.0984)
				  mc.thePlayer.motionY = -0.0984;
		  }
		  if (teleported) {
			original.addMessage("silently accepting s08...");
			mc.getNetHandler().addToSendQueueSilent(new C06PacketPlayerPosLook(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,mc.thePlayer.rotationYaw,mc.thePlayer.rotationPitch,false));
			mc.getNetHandler().addToSendQueueSilent(new C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
			mc.thePlayer.setPosition(mc.thePlayer.posX+MovementUtils.getMotion(7.5).x,mc.thePlayer.posY+6,mc.thePlayer.posZ+MovementUtils.getMotion(7.5).y);
			
			teleported = false;
		  }
	}
	
	@Subscribe
	public void onTeleport(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (e.getPacket() instanceof S08PacketPlayerPosLook) {
			//original.addMessage("LOL?");
			teleported = true;
		}
	}
}