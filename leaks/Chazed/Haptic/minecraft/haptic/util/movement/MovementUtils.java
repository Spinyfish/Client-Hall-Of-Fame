package haptic.util.movement;

import haptic.Haptic;
import haptic.event.impl.MovementEvent;
import haptic.module.impl.combat.Killaura;
import haptic.util.base.BaseUtil;
import haptic.util.network.ServerUtils;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class MovementUtils implements BaseUtil {
	
	public static void setSpeed(double speed) {
		if(isWalking()) {
			mc.thePlayer.motionX = -Math.sin(getDirection()) * speed;
			mc.thePlayer.motionZ = Math.cos(getDirection()) * speed;
		} else {
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;
		}
	}
	
	public static void setSpeed(MovementEvent event, double speed) {
		if(isWalking()) {
			event.setMotionX(mc.thePlayer.motionX = -Math.sin(getDirection()) * speed);
			event.setMotionZ(mc.thePlayer.motionZ = Math.cos(getDirection()) * speed);
		} else {
			event.setMotionX(mc.thePlayer.motionX = 0);
			event.setMotionZ(mc.thePlayer.motionZ = 0);
		}
	}
	
	public static double getYawDirection() {
		boolean movingForward = mc.thePlayer.moveForward > 0.0F;
		boolean movingBackward = mc.thePlayer.moveForward < 0.0F;
		boolean movingRight = mc.thePlayer.moveStrafing > 0.0F;
		boolean movingLeft = mc.thePlayer.moveStrafing < 0.0F;
		
		boolean isMovingSideways = movingLeft || movingRight;
		boolean isMovingStraight = movingForward || movingBackward;
		
		double direction = mc.thePlayer.rotationYaw;
		
		if(movingForward && !isMovingSideways) {
			
		} else if(movingBackward && !isMovingSideways) {
			direction += 180;
		} else if(movingForward && movingLeft) {
			direction += 45;
		} else if(movingForward) {
			direction -= 45;
		} else if(!isMovingStraight && movingLeft) {
			direction += 90;
		} else if(!isMovingStraight && movingRight) {
			direction -= 90;
		} else if(movingBackward && movingRight) {
			direction -= 135;
		} else if(movingBackward) {
			direction += 135;
		}
		
		return direction;
	}
	
	public static double getDirection() {
		
		boolean movingForward = mc.thePlayer.moveForward > 0.0F;
		boolean movingBackward = mc.thePlayer.moveForward < 0.0F;
		boolean movingRight = mc.thePlayer.moveStrafing > 0.0F;
		boolean movingLeft = mc.thePlayer.moveStrafing < 0.0F;
		
		boolean isMovingSideways = movingLeft || movingRight;
		boolean isMovingStraight = movingForward || movingBackward;
		
		double direction = mc.thePlayer.rotationYaw;
		
		if(movingForward && !isMovingSideways) {
			
		} else if(movingBackward && !isMovingSideways) {
			direction += 180;
		} else if(movingForward && movingLeft) {
			direction += 45;
		} else if(movingForward) {
			direction -= 45;
		} else if(!isMovingStraight && movingLeft) {
			direction += 90;
		} else if(!isMovingStraight && movingRight) {
			direction -= 90;
		} else if(movingBackward && movingRight) {
			direction -= 135;
		} else if(movingBackward) {
			direction += 135;
		}
		
		return Math.toRadians(direction);
	}
	
	public static boolean isWalking() {
		return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
	}
	
	public static void jump() {
		if(!mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.thePlayer.jump();
		}
	}
	
	public static void jump(double motion) {
		if(!mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.thePlayer.jump();
		}
		mc.thePlayer.motionY = motion;
	}

	public static double getCurrentMotion() {
		return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
	}
	
	public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
	
	public static void speedBoost(float speed) {
		float f = mc.thePlayer.rotationYaw * 0.017453292F;
        
        if(Haptic.getModuleManager().getModuleByName("Killaura").isEnabled() && ((Killaura) (Haptic.getModuleManager().getModuleByName("Killaura"))).target != null && ((Killaura) (Haptic.getModuleManager().getModuleByName("Killaura"))).moveFix.isEnabled()) {
            f = ((Killaura) (Haptic.getModuleManager().getModuleByName("Killaura"))).yaw * 0.017453292F;
        }
        mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * speed);
        mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * speed);
	}
	
	public static void motionMult(double mult) {
		mc.thePlayer.motionX *= mult;
		mc.thePlayer.motionZ *= mult;
	}
	
	public static boolean isGoingDiagonally() {
		return Math.abs(mc.thePlayer.motionX) > 0.08 && Math.abs(mc.thePlayer.motionZ) > 0.08;
	}

}