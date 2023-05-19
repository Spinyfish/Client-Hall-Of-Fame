package club.marsh.bloom.impl.utils.movement;

import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.mods.movement.TargetStrafe;
import club.marsh.bloom.impl.utils.render.Vector2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
/*
 * @author dort, marshadow
 */
public class MovementUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static void hclip(double horizontal) {
         double radians = Math.toRadians(mc.thePlayer.rotationYaw);
         mc.thePlayer.setPosition(mc.thePlayer.posX + horizontal * -Math.sin(radians), mc.thePlayer.posY, mc.thePlayer.posZ + horizontal * Math.cos(radians));
    }

    public static void collide(CollideEvent event) {
        double x = event.getPos().getX();
        double y = event.getPos().getY();
        double z = event.getPos().getZ();
        event.setBoundingBox(AxisAlignedBB.fromBounds(-50, -1, -50, 50, 1.0F, 50).offset(x, y, z));
    }

    public static void setMotionPacket(double hDist, int packets, double incrementPerPacket, double yOffset, int incrementDelay, double incrementSpeed) {
        setSpeed(hDist);
        EntityPlayerSP playerInstance = mc.thePlayer;
        double roundedY = playerInstance.posY - playerInstance.posY % 0.015625 + yOffset;
        for (int i = 0; i < packets; i++) {
            Vector2 motion = getMotion(hDist + (i % Math.max(1, incrementDelay) == 0 ? incrementSpeed : 0));
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(motion.x,0,motion.y)).getBlock() == Blocks.air)
                return;
            playerInstance.moveEntity(motion.x, 0, motion.y);
            mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(playerInstance.posX,
                    roundedY, playerInstance.posZ, true));
            hDist += incrementPerPacket;
        }
    }
    
    public static void strafe()
    {
    	strafe(getSpeed());
    }
    
    public static void strafe(double speed)
    {
    	if (isMoving())
    	{
    		double yaw = getDirection();
    		mc.thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
    		mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
    	}
    }

    public static boolean blockUnder(double amount) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -amount, 0.0D)).isEmpty();
    }

    public static boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0;
    }

    public static float getDirection() {
    	float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0F) rotationYaw += 180F;
        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F) forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F) forward = 0.5F;
        if (mc.thePlayer.moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (mc.thePlayer.moveStrafing < 0F) rotationYaw += 90F * forward;
        return (float) Math.toRadians(rotationYaw);
    }

    public static Vector2 getMotion(double moveSpeed, float rotationYaw, double moveStrafe, double moveForward) {
        Vector2 vec =  new Vector2(0D,0D);
        if (moveForward != 0.0D || moveStrafe != 0.0D) {
            if (moveStrafe > 0) {
                moveStrafe = 1;
            } else if (moveStrafe < 0) {
                moveStrafe = -1;
            }
            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += moveForward > 0.0D ? -45 : 45;
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += moveForward > 0.0D ? 45 : -45;
                }
                moveStrafe = 0.0D;
                if (moveForward > 0.0D) {
                    moveForward = 1.0D;
                } else if (moveForward < 0.0D) {
                    moveForward = -1.0D;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            vec.x = moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin;
            vec.y = moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos;
            return vec;
        }
        return new Vector2(0D, 0D);
    }

    public static Vector2 getMotion(double moveSpeed) {
        MovementInput movementInput = mc.thePlayer.movementInput;
        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;
        Vector2 vec =  new Vector2(0D,0D);
        double rotationYaw = mc.thePlayer.rotationYaw;
        if (moveForward != 0.0D || moveStrafe != 0.0D) {
            if (moveStrafe > 0) {
                moveStrafe = 1;
            } else if (moveStrafe < 0) {
                moveStrafe = -1;
            }
            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += moveForward > 0.0D ? -45 : 45;
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += moveForward > 0.0D ? 45 : -45;
                }
                moveStrafe = 0.0D;
                if (moveForward > 0.0D) {
                    moveForward = 1.0D;
                } else if (moveForward < 0.0D) {
                    moveForward = -1.0D;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            vec.x = moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin;
            vec.y = moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos;
            return vec;
        }
        return new Vector2(0D, 0D);
    }

    public static void setPosition(double motion) {
        if (mc.thePlayer.movementInput.moveStrafe != 0 || mc.thePlayer.movementInput.moveForward != 0) {
            mc.thePlayer.setPosition(mc.thePlayer.posX+getMotion(motion).x,mc.thePlayer.posY,mc.thePlayer.posZ+getMotion(motion).y);
        }
    }

    public static float getBaseSpeed() {
        float baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * 2.873f;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int ampl = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * ampl);
        }
        return baseSpeed;
    }

    public static void setSpeed(double motion) {
        if (TargetStrafe.doTargetStrafe(motion)) return;
        
        if (mc.thePlayer.movementInput.moveStrafe != 0 || mc.thePlayer.movementInput.moveForward != 0) {
            mc.thePlayer.motionX = getMotion(motion).x;
            mc.thePlayer.motionZ = getMotion(motion).y;
        } else {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        }
    }

}
