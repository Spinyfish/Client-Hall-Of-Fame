package dev.africa.pandaware.utils.player;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

@UtilityClass
public class MovementUtils implements MinecraftInstance {

    public boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0;
    }

    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }

    public double getSpeedDistance() {
        double distX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double distZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        return ApacheMath.sqrt(distX * distX + distZ * distZ);
    }

    public double getBps() {
        return (getSpeedDistance() * 20) * mc.timer.timerSpeed;
    }

    public void strafe() {
        strafe(getSpeed());
    }

    public void strafe(MoveEvent event) {
        strafe(event, getSpeed());
    }

    public void strafe(double movementSpeed) {
        strafe(null, movementSpeed);
    }

    public void strafe(MoveEvent moveEvent, double movementSpeed) {
        if (mc.thePlayer.movementInput.moveForward > 0.0) {
            mc.thePlayer.movementInput.moveForward = (float) 1.0;
        } else if (mc.thePlayer.movementInput.moveForward < 0.0) {
            mc.thePlayer.movementInput.moveForward = (float) -1.0;
        }

        if (mc.thePlayer.movementInput.moveStrafe > 0.0) {
            mc.thePlayer.movementInput.moveStrafe = (float) 1.0;
        } else if (mc.thePlayer.movementInput.moveStrafe < 0.0) {
            mc.thePlayer.movementInput.moveStrafe = (float) -1.0;
        }

        if (mc.thePlayer.movementInput.moveForward == 0.0 && mc.thePlayer.movementInput.moveStrafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }

        if (mc.thePlayer.movementInput.moveForward != 0.0 && mc.thePlayer.movementInput.moveStrafe != 0.0) {
            mc.thePlayer.movementInput.moveForward *= ApacheMath.sin(0.6398355709958845);
            mc.thePlayer.movementInput.moveStrafe *= ApacheMath.cos(0.6398355709958845);
        }

        if (moveEvent != null) {
            moveEvent.x = mc.thePlayer.motionX = mc.thePlayer.movementInput.moveForward * movementSpeed * -ApacheMath.sin(ApacheMath.toRadians(mc.thePlayer.rotationYaw))
                    + mc.thePlayer.movementInput.moveStrafe * movementSpeed * ApacheMath.cos(ApacheMath.toRadians(mc.thePlayer.rotationYaw));
            moveEvent.z = mc.thePlayer.motionZ = mc.thePlayer.movementInput.moveForward * movementSpeed * ApacheMath.cos(ApacheMath.toRadians(mc.thePlayer.rotationYaw))
                    - mc.thePlayer.movementInput.moveStrafe * movementSpeed * -ApacheMath.sin(ApacheMath.toRadians(mc.thePlayer.rotationYaw));
        } else {
            mc.thePlayer.motionX = mc.thePlayer.movementInput.moveForward * movementSpeed * -ApacheMath.sin(ApacheMath.toRadians(mc.thePlayer.rotationYaw))
                    + mc.thePlayer.movementInput.moveStrafe * movementSpeed * ApacheMath.cos(ApacheMath.toRadians(mc.thePlayer.rotationYaw));
            mc.thePlayer.motionZ = mc.thePlayer.movementInput.moveForward * movementSpeed * ApacheMath.cos(ApacheMath.toRadians(mc.thePlayer.rotationYaw))
                    - mc.thePlayer.movementInput.moveStrafe * movementSpeed * -ApacheMath.sin(ApacheMath.toRadians(mc.thePlayer.rotationYaw));
        }
    }

    public static void setSpeed(MoveEvent e, double speed, float forward, float strafing, float yaw) {
        yaw = getDirection(forward, strafing, yaw);
        double x = -ApacheMath.sin(ApacheMath.toRadians(yaw));
        double z = ApacheMath.cos(ApacheMath.toRadians(yaw));
        if (e != null) {
            e.x = mc.thePlayer.motionX = (x * speed);
            e.z = mc.thePlayer.motionZ = (z * speed);
        } else {
            mc.thePlayer.motionZ = z * speed;
            mc.thePlayer.motionX = x * speed;
        }
    }

    public static void setSpeed(MoveEvent e, double speed) {
        float yaw = getDirection();
        double x = -ApacheMath.sin(ApacheMath.toRadians(yaw));
        double z = ApacheMath.cos(ApacheMath.toRadians(yaw));
        if (e != null) {
            e.x = (x * speed);
            e.z = (z * speed);
        } else {
            mc.thePlayer.motionZ = z * speed;
            mc.thePlayer.motionX = x * speed;
        }
    }

    public static double getSpeed() {
        return mc.thePlayer == null ? 0 : ApacheMath.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX
                + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static double getSpeed(MoveEvent moveEvent) {
        return mc.thePlayer == null ? 0 : ApacheMath.sqrt(moveEvent.x * moveEvent.x + moveEvent.z * moveEvent.z);
    }

    public void slowdown() {
        double baseSpeed = 0.1873;

        if (mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        strafe(MathHelper.clamp_double(getSpeed(), 0, baseSpeed));
    }

    public double getLastDistance() {
        return ApacheMath.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
    }

    public static float getDirection() {
        return getDirection(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw);
    }

    public static float getDirection(float forward, float strafing, float yaw) {
        if (forward == 0.0 && strafing == 0.0) return yaw;
        boolean reversed = (forward < 0.0);
        float strafingYaw = 90f * ((forward > 0) ? 0.5f : (reversed ? -0.5f : 1));
        if (reversed) yaw += 180;
        if (strafing > 0) {
            yaw -= strafingYaw;
        } else if (strafing < 0) {
            yaw += strafingYaw;
        }
        return yaw;
    }

    public double getLowHopMotion(double motion) {
        double base = MathUtils.roundToDecimal(mc.thePlayer.posY - (int) mc.thePlayer.posY, 2);

        if (base == 0.4) {
            return 0.31f;
        } else if (base == 0.71) {
            return 0.05f;
        } else if (base == 0.76) {
            return -0.2f;
        } else if (base == 0.56) {
            return -0.19f;
        } else if (base == 0.42) {
            return -0.12;
        }

        return motion;
    }

    public double getHypixelFunny() {
        double value = 1;
        for (int i = 0; i < RandomUtils.nextInt(4, 7); i++) {
            value *= ApacheMath.random();
        }
        return value;
    }

    public final double MODULO_GROUND = 1 / 64D;

    public boolean canSprint() {
        return mc.thePlayer != null && PlayerUtils.isMathGround() && !mc.thePlayer.isPotionActive(Potion.blindness) &&
                mc.thePlayer.getFoodStats().getFoodLevel() > 6 && isMoving() &&
                !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking();
    }
}
