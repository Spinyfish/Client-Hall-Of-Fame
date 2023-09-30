package axolotl.cheats.modules.impl.movement;

import axolotl.cheats.events.EventType;
import axolotl.cheats.settings.ModeSetting;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.MoveEvent;
import axolotl.cheats.modules.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.stats.StatList;

public class Step extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "AAC", "Spartan", "Flappy", "ACD");
	
	public Step() {
		super("Step", Category.MOVEMENT, true);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}
	
	public boolean shouldstep = false, jumped = false;
	public double cacheY, stepX, stepY, stepZ, groundTicks, airTicks;
	
	public void onEvent(Event e) {
		
		if(!(e instanceof MoveEvent) || e.eventType != EventType.PRE)return;
		
		if(mc.thePlayer.onGround) {
			airTicks = 0;
			groundTicks++;
		} else {
			groundTicks = 0;
			airTicks++;
		}
		
		if(mc.thePlayer.onGround)mc.timer.timerSpeed = 1f;
		
		if(mc.thePlayer.isCollidedHorizontally) {
            stepX = mc.thePlayer.posX;
            stepY = mc.thePlayer.posY;
            stepZ = mc.thePlayer.posZ;
            jumped = true;
			mc.thePlayer.stepHeight = 0.5F;
			switch(mode.getMode()) {
				
				case "Flappy":
					mc.thePlayer.motionY = 0;
					vClip(0.3f);
					break;
			
				case "Vanilla":
					
					mc.thePlayer.stepHeight = 1.0F;
					
					break;
					
				case "AAC":
					mc.thePlayer.stepHeight = 0.5f;
					if (shouldstep) {
						mc.timer.timerSpeed = (float) (0.9 + mc.thePlayer.ticksExisted % 4 / 20);
					}
					if (mc.thePlayer.onGround) {
						shouldstep = false;
						mc.timer.timerSpeed = 1;
					}
					if (mc.thePlayer.isCollidedHorizontally && isMoving() && mc.thePlayer.onGround) {
						cacheY = mc.thePlayer.posY;
						mc.thePlayer.isAirBorne = true;
						shouldstep = true;
						mc.timer.timerSpeed = 4;
						mc.thePlayer.motionY += 0.47;
						//mc.thePlayer.motionY += 0.55; //for 1.5 block step
					}
					
					break;
					
				case "Spartan":
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
						mc.timer.timerSpeed = 2f;
					}
					break;
			
				case "ACD":
					
					mc.thePlayer.stepHeight = 0.5f;
					mc.timer.timerSpeed = 0.5f;
					vClip(1f);
					
					break;
					
				default:
					break;
			
			}
			
		} else {
			
			mc.timer.timerSpeed = mc.timer.timerSpeed == 0.5f ? 1 : mc.timer.timerSpeed;
			
			switch(mode.getMode()) {
			
				case "Spartan":
					if(jumped) {
						mc.timer.timerSpeed = 1f;
						jumped = false;
					}
					break;
					
				case "Flappy":
					if(jumped) {
						mc.thePlayer.motionY = 0;
						jumped = false;
					}
					break;
			
				default:
					break;
			
			}
			
		}
		
	}
	
	public void jump(boolean real) {
		if(real)mc.thePlayer.jump();
		else {
			mc.thePlayer.isAirBorne = true;
	        mc.thePlayer.triggerAchievement(StatList.jumpStat);
		}
	}
	
}
