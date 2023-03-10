/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.vecmath.Vector2d
 */
package lodomir.dev.utils.player;

import java.text.NumberFormat;
import javax.vecmath.Vector2d;
import lodomir.dev.utils.math.apache.ApacheMath;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;

public class MovementUtils {
    public static Minecraft mc = Minecraft.getMinecraft();
    static NumberFormat format = NumberFormat.getInstance();

    public static double getBlocksPerSecond() {
        if (MovementUtils.mc.thePlayer == null || MovementUtils.mc.thePlayer.ticksExisted < 1) {
            return 0.0;
        }
        return MovementUtils.mc.thePlayer.getDistance(MovementUtils.mc.thePlayer.lastTickPosX, MovementUtils.mc.thePlayer.lastTickPosY, MovementUtils.mc.thePlayer.lastTickPosZ) * (double)(Minecraft.getMinecraft().timer.ticksPerSecond * Minecraft.getMinecraft().timer.timerSpeed);
    }

    public static boolean isOverVoid() {
        for (int i = (int)(MovementUtils.mc.thePlayer.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(MovementUtils.mc.thePlayer.posX, (double)i, MovementUtils.mc.thePlayer.posZ);
            if (MovementUtils.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) continue;
            return false;
        }
        return true;
    }

    public static void stop() {
        MovementUtils.mc.thePlayer.motionZ = 0.0;
        MovementUtils.mc.thePlayer.motionX = 0.0;
    }

    public static float getSpeed() {
        return (float)Math.sqrt(MovementUtils.mc.thePlayer.motionX * MovementUtils.mc.thePlayer.motionX + MovementUtils.mc.thePlayer.motionZ * MovementUtils.mc.thePlayer.motionZ);
    }

    public static void strafe() {
        MovementUtils.strafe(MovementUtils.getSpeed());
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (MovementUtils.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (double)(MovementUtils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public static boolean getOnRealGround(EntityLivingBase entity, double y) {
        return !MovementUtils.mc.theWorld.getCollidingBoundingBoxes(MovementUtils.mc.thePlayer, entity.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty();
    }

    public static double getJumpMotion(float motionY) {
        Potion potion = Potion.jump;
        if (MovementUtils.mc.thePlayer.isPotionActive(potion)) {
            int amplifier = MovementUtils.mc.thePlayer.getActivePotionEffect(potion).getAmplifier();
            motionY += (float)(amplifier + 1) * 0.1f;
        }
        return motionY;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean isMoving() {
        if (MovementUtils.mc.thePlayer == null) return false;
        MovementInput cfr_ignored_0 = MovementUtils.mc.thePlayer.movementInput;
        if (MovementInput.moveForward != 0.0f) return true;
        MovementInput cfr_ignored_1 = MovementUtils.mc.thePlayer.movementInput;
        if (MovementInput.moveStrafe == 0.0f) return false;
        return true;
    }

    public static boolean hasMotion() {
        return MovementUtils.mc.thePlayer.motionX != 0.0 && MovementUtils.mc.thePlayer.motionZ != 0.0 && MovementUtils.mc.thePlayer.motionY != 0.0;
    }

    public static void strafe(float speed) {
        if (!MovementUtils.isMoving()) {
            return;
        }
        double yaw = MovementUtils.getDirection();
        MovementUtils.mc.thePlayer.motionX = -Math.sin(yaw) * (double)speed;
        MovementUtils.mc.thePlayer.motionZ = Math.cos(yaw) * (double)speed;
    }

    public static void forward(double length) {
        double yaw = Math.toRadians(MovementUtils.mc.thePlayer.rotationYaw);
        MovementUtils.mc.thePlayer.setPosition(MovementUtils.mc.thePlayer.posX + -Math.sin(yaw) * length, MovementUtils.mc.thePlayer.posY, MovementUtils.mc.thePlayer.posZ + Math.cos(yaw) * length);
    }

    public static float getDirections() {
        return MovementUtils.getDirection(MovementUtils.mc.thePlayer.moveForward, MovementUtils.mc.thePlayer.moveStrafing, MovementUtils.mc.thePlayer.rotationYaw);
    }

    public static double getLastDistance() {
        return ApacheMath.hypot(MovementUtils.mc.thePlayer.posX - MovementUtils.mc.thePlayer.prevPosX, MovementUtils.mc.thePlayer.posZ - MovementUtils.mc.thePlayer.prevPosZ);
    }

    public static float getDirection(float forward, float strafing, float yaw) {
        boolean reversed;
        if ((double)forward == 0.0 && (double)strafing == 0.0) {
            return yaw;
        }
        boolean bl = reversed = (double)forward < 0.0;
        float strafingYaw = 90.0f * (forward > 0.0f ? 0.5f : (reversed ? -0.5f : 1.0f));
        if (reversed) {
            yaw += 180.0f;
        }
        if (strafing > 0.0f) {
            yaw -= strafingYaw;
        } else if (strafing < 0.0f) {
            yaw += strafingYaw;
        }
        return yaw;
    }

    public static float getPlayerDirection() {
        float direction = MovementUtils.mc.thePlayer.rotationYaw;
        if (MovementUtils.mc.thePlayer.moveForward > 0.0f) {
            if (MovementUtils.mc.thePlayer.moveStrafing > 0.0f) {
                direction -= 45.0f;
            } else if (MovementUtils.mc.thePlayer.moveStrafing < 0.0f) {
                direction += 45.0f;
            }
        } else if (MovementUtils.mc.thePlayer.moveForward < 0.0f) {
            direction = MovementUtils.mc.thePlayer.moveStrafing > 0.0f ? (direction -= 135.0f) : (MovementUtils.mc.thePlayer.moveStrafing < 0.0f ? (direction += 135.0f) : (direction -= 180.0f));
        } else if (MovementUtils.mc.thePlayer.moveStrafing > 0.0f) {
            direction -= 90.0f;
        } else if (MovementUtils.mc.thePlayer.moveStrafing < 0.0f) {
            direction += 90.0f;
        }
        return direction;
    }

    public static double getDirection() {
        float rotationYaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (MovementUtils.mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MovementUtils.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (MovementUtils.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MovementUtils.mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (MovementUtils.mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static void setMotion(double speed) {
        MovementInput cfr_ignored_0 = MovementUtils.mc.thePlayer.movementInput;
        double forward = MovementInput.moveForward;
        MovementInput cfr_ignored_1 = MovementUtils.mc.thePlayer.movementInput;
        double strafe = MovementInput.moveStrafe;
        float yaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MovementUtils.mc.thePlayer.motionX = 0.0;
            MovementUtils.mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            MovementUtils.mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            MovementUtils.mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }

    public static boolean isOnGround(double height) {
        return !MovementUtils.mc.theWorld.getCollidingBoundingBoxes(MovementUtils.mc.thePlayer, MovementUtils.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    public Vector2d getMotion(double moveSpeed) {
        MovementInput movementInput = MovementUtils.mc.thePlayer.movementInput;
        double moveForward = MovementInput.moveForward;
        double moveStrafe = MovementInput.moveStrafe;
        double rotationYaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (moveForward != 0.0 || moveStrafe != 0.0) {
            if (moveStrafe > 0.0) {
                moveStrafe = 1.0;
            } else if (moveStrafe < 0.0) {
                moveStrafe = -1.0;
            }
            if (moveForward != 0.0) {
                if (moveStrafe > 0.0) {
                    rotationYaw += moveForward > 0.0 ? -45.0 : 45.0;
                } else if (moveStrafe < 0.0) {
                    rotationYaw += moveForward > 0.0 ? 45.0 : -45.0;
                }
                moveStrafe = 0.0;
                if (moveForward > 0.0) {
                    moveForward = 1.0;
                } else if (moveForward < 0.0) {
                    moveForward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians((rotationYaw *= 0.995) + 90.0));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0));
            return new Vector2d(moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin, moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos);
        }
        return new Vector2d(0.0, 0.0);
    }

    public static boolean canStep(double height) {
        if (!MovementUtils.mc.thePlayer.isCollidedHorizontally || !MovementUtils.isOnGround(0.001)) {
            return false;
        }
        return !MovementUtils.mc.theWorld.getCollidingBoundingBoxes(MovementUtils.mc.thePlayer, MovementUtils.mc.thePlayer.getEntityBoundingBox().expand(0.01, 0.0, 0.01).offset(0.0, height - 0.01, 0.0)).isEmpty() && MovementUtils.mc.theWorld.getCollidingBoundingBoxes(MovementUtils.mc.thePlayer, MovementUtils.mc.thePlayer.getEntityBoundingBox().expand(0.1, 0.0, 0.01).offset(0.0, height + 0.1, 0.0)).isEmpty();
    }
}

