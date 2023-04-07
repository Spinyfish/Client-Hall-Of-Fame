package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.modules.player.PingSpoof;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class Speed extends Module {
	
	public static BooleanSetting waitOnGround = new BooleanSetting("Ground", true);
	public static NumberSetting movementSpeed = new NumberSetting("Speed", 1.4, 1.1, 1.5, 0.05);
	public static BooleanSetting jump = new BooleanSetting("Jump", true);
	public static ModeSetting bypass = new ModeSetting("Mode", "None", new String[] {"Default", "Spartan","Strafe","ACD", "Default2", "Hypixel"});

	public Speed() {
		super("Speed", Keyboard.KEY_Y, Category.MOVEMENT, "Makes you go faster", true);
		this.addSettings(waitOnGround, movementSpeed, jump, bypass);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public void onEnable() {
		if(!bypass.getMode().equalsIgnoreCase("Spartan") && !bypass.getMode().equalsIgnoreCase("ACD"))
			y = mc.thePlayer.posY;
	}
	
	double y=0;
	boolean onGround = false;
	
	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
		}
	}
	
	public void onTick(){
		if (mc.thePlayer == null) {
			return;
		} else if(this.toggled) {
			if((mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) && !mc.thePlayer.isSneaking()) {
				if(bypass.getMode().equalsIgnoreCase("Spartan")) {
					y++;
					double d = (PlayerUtil.getPlayerHeightDouble() / 5f);
					mc.timer.timerSpeed = (float) (Math.min(1f + d, 1.5f));
					mc.thePlayer.motionX *= 1.001f + (mc.thePlayer.ticksExisted % 3 == 0 ? 0.0005f : 0f);
					mc.thePlayer.motionZ *= 1.001f + (mc.thePlayer.ticksExisted % 3 == 0 ? 0.0005f : 0f);
					if(PlayerUtil.getPlayerHeightDouble() > 1) {
						mc.timer.timerSpeed = 3f;
						if(y % 4 == 0)
							mc.thePlayer.motionY += 0.00002f;
					}
					if(y % 8 == 0) {
						mc.timer.timerSpeed = (float) (Math.min(1.5f + d, 1.5f));
						mc.thePlayer.motionX *= 1.001f;
						mc.thePlayer.motionZ *= 1.001f;
					}
					mc.thePlayer.speedInAir = 0.05f;
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
					}
					return;
				}
				else if (bypass.getMode().equalsIgnoreCase("ACD")) {
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
						mc.thePlayer.motionY *= 0.6f;
					}
					y++;
					mc.thePlayer.motionX *= 1.5f;
		    		mc.thePlayer.motionZ *= 1.5f;
		    		mc.timer.timerSpeed = 1f;
		    		if(y % 2 == 0) {
		    			mc.thePlayer.setSpeed(0.5f);
		    		}
					mc.thePlayer.onGround = true;
					return;
				}
				else if (bypass.getMode().equalsIgnoreCase("Strafe")) {
					if(mc.thePlayer.onGround && jump.isEnabled()) mc.thePlayer.jump();
					mc.thePlayer.motionX *= movementSpeed.getValue();
		    		mc.thePlayer.motionZ *= movementSpeed.getValue();
					mc.thePlayer.onGround = true;
		    		return;
				} else if (bypass.getMode().equalsIgnoreCase("Default2")) {
					if(mc.thePlayer.onGround && isMovingKB()) {
						if(mc.thePlayer.ticksExisted % 2 == 0) {
							mc.thePlayer.motionX *= 1.01932;
			    			mc.thePlayer.motionZ *= 1.01932;
						} else {
							mc.thePlayer.motionX *= 1.00932;
			    			mc.thePlayer.motionZ *= 1.00932;
						}
							
			    		mc.timer.timerSpeed = 6f;
			    		
			    		mc.thePlayer.jump();
					} else {
						mc.thePlayer.speedInAir = 0.02f;
						if(mc.thePlayer.ticksExisted % 3 == 0) {
			    			mc.timer.timerSpeed = 5f;
			    			mc.thePlayer.speedInAir = 0.03f;
			    			mc.thePlayer.motionX *= 1.001f;
			    			mc.thePlayer.motionZ *= 1.001f;
			    		}
						mc.timer.timerSpeed = 1.25f;
					}
		    		return;
				} else if (bypass.getMode().equalsIgnoreCase("Hypixel")) {
					mc.timer.timerSpeed = 1.1f;
					if(mc.thePlayer.onGround) {
						if (jump.isEnabled()) {
							mc.thePlayer.jump();
							mc.thePlayer.motionY = 0.4f; // ez
						}
						mc.timer.timerSpeed = 1.2f;
						mc.thePlayer.motionX *= 1.07f;
						mc.thePlayer.motionZ *= 1.07f;
					}
					if(mc.thePlayer.ticksExisted % 2 == 0) {
						mc.thePlayer.motionX *= 1.006f;
						mc.thePlayer.motionZ *= 1.006f;
					}
					mc.thePlayer.speedInAir = 0.03f;
					return;
				}
				if (mc.thePlayer.onGround && waitOnGround.isEnabled()) {
					if (jump.isEnabled()) mc.thePlayer.jump(); 
					mc.thePlayer.motionX *= (movementSpeed.getValue());
					mc.thePlayer.motionZ *= (movementSpeed.getValue());
				} else if (!waitOnGround.isEnabled()) {
					if (mc.thePlayer.onGround && jump.isEnabled()) mc.thePlayer.jump(); 
					if (!mc.thePlayer.onGround) {
			    		mc.thePlayer.motionX *= 1.08432;
			    		mc.thePlayer.motionZ *= 1.08432;
					} else {
						mc.thePlayer.motionX *= movementSpeed.getValue();
			    		mc.thePlayer.motionZ *= movementSpeed.getValue();
					}
				}
			} 
		}
	}
	
	public void onDisable() {
		mc.thePlayer.speedInAir = 0.02f;
		mc.timer.timerSpeed = 1f;
		y = 0;
	}

}
