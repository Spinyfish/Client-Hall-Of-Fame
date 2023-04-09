package tech.dort.dortware.impl.utils.movement;

import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.util.Util;
import tech.dort.dortware.impl.events.MovementEvent;
import tech.dort.dortware.impl.modules.combat.KillAura;
import tech.dort.dortware.impl.modules.combat.TargetStrafe;
import tech.dort.dortware.impl.utils.combat.AimUtil;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

import javax.vecmath.Vector2d;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MotionUtils implements Util {

    private static List<Double> frictionValues = Arrays.asList(0.0, 0.0, 0.0);


    /**
     * Attempts to damage the user via fall damage.
     *
     * @param groundCheck - if true, you will need to be on the ground for this method to complete successfully
     */
    public static void damagePlayer(final boolean groundCheck) {
        if (!groundCheck || mc.thePlayer.onGround) {
            final double x = mc.thePlayer.posX;
            final double y = mc.thePlayer.posY;
            final double z = mc.thePlayer.posZ;

            double fallDistanceReq = 3;

            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
                fallDistanceReq += (float) (amplifier + 1);
            }

            for (int i = 0; i < (int) Math.ceil(fallDistanceReq / 0.0624); i++) {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0624, z, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
            }

            PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
        }
    }

    public static boolean isOnWater() {
        return mc.thePlayer.isCollidedVertically && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer)).getBlock() instanceof BlockLiquid;
    }

    public static boolean isBlockUnder() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            final AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }

    public static double getMotion(float baseMotionY) {
        Potion potion = Potion.jump;
        if (mc.thePlayer.isPotionActive(potion)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(potion).getAmplifier();
            baseMotionY += (amplifier + 1) * 0.1F;
        }

        return baseMotionY;
    }

    /**
     * @author aristhena
     */
    public static void setMotion(MovementEvent event, double moveSpeed) {
        EntityLivingBase entity = KillAura.currentTarget;
        TargetStrafe targetStrafeClass = Client.INSTANCE.getModuleManager().get(TargetStrafe.class);
        boolean targetStrafe = TargetStrafe.canStrafe();
        MovementInput movementInput = mc.thePlayer.movementInput;

        double moveForward = targetStrafe ? mc.thePlayer.getDistanceToEntity(entity) <= targetStrafeClass.range.getValue() ? 0 : 1 : movementInput.moveForward;
        double moveStrafe = targetStrafe ? TargetStrafe.dir : movementInput.moveStrafe;

        double rotationYaw = targetStrafe ? AimUtil.getRotationsRandom(entity).getRotationYaw() : mc.thePlayer.rotationYaw;

        event.setStrafeSpeed(moveSpeed);

        if (moveForward == 0.0D && moveStrafe == 0.0D) {
            event.setMotionX(0);
            event.setMotionZ(0);
        } else {
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
            event.setMotionX(moveForward * moveSpeed * cos
                    + moveStrafe * moveSpeed * sin);
            event.setMotionZ(moveForward * moveSpeed * sin
                    - moveStrafe * moveSpeed * cos);
        }
    }

    public static double calcFriction(double moveSpeed, double lastDist, double baseMoveSpeed) {
        frictionValues.set(0, lastDist - (lastDist / 160.0 - 1.0E-3));
        frictionValues.set(1, lastDist - ((moveSpeed - lastDist) / 33.3D));
        double materialFriction =
                mc.thePlayer.isInWater() ?
                        0.89F :
                        mc.thePlayer.isInLava() ?
                                0.535F :
                                0.98F;
        frictionValues.set(2, lastDist - (baseMoveSpeed * (1.0D - materialFriction)));
        return Collections.min(frictionValues);
    }

    public static void setMotion(double moveSpeed) {
        EntityLivingBase entity = KillAura.currentTarget;
        TargetStrafe targetStrafeClass = Client.INSTANCE.getModuleManager().get(TargetStrafe.class);
        boolean targetStrafe = TargetStrafe.canStrafe();
        MovementInput movementInput = mc.thePlayer.movementInput;

        double moveForward = targetStrafe ? mc.thePlayer.getDistanceToEntity(entity) <= targetStrafeClass.range.getValue() ? 0 : 1 : movementInput.moveForward;
        double moveStrafe = targetStrafe ? TargetStrafe.dir : movementInput.moveStrafe;
        double rotationYaw = targetStrafe ? AimUtil.getRotationsRandom(entity).getRotationYaw() : mc.thePlayer.rotationYaw;

        if (moveForward == 0.0D && moveStrafe == 0.0D) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        } else {
            if (moveStrafe > 0) {
                moveStrafe = 1;
            } else if (moveStrafe < 0) {
                moveStrafe = -1;
            }
            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += (moveForward > 0.0D ? -45 : 45);
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += (moveForward > 0.0D ? 45 : -45);
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
            mc.thePlayer.motionX = moveForward * moveSpeed * cos
                    + moveStrafe * moveSpeed * sin;
            mc.thePlayer.motionZ = moveForward * moveSpeed * sin
                    - moveStrafe * moveSpeed * cos;
        }
    }

    public static double getSpeed() {
        double baseSpeed = 0.2873;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
            baseSpeed *= 1 + 0.2 * amp;
        }

        return baseSpeed;
    }

    public static double[] teleportForward(final double speed) {
        final float forward = 1.0F;
        final float side = 0;
        final float yaw = mc.thePlayer.prevRotationYaw + (mc.thePlayer.rotationYaw - mc.thePlayer.prevRotationYaw) * mc.timer.renderPartialTicks;
        final double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double getBaseSpeed(ACType value) {
        double baseSpeed = value == ACType.MINEPLEX ? 0.4225 : 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
            baseSpeed *= 1 + 0.2 * amp;
        }
        return baseSpeed;
    }

    public static void sendPosition(double x, double y, double z, boolean ground) {
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, ground));
    }

    public static Vector2d getMotion(double moveSpeed) {
        MovementInput movementInput = mc.thePlayer.movementInput;

        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;

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
            return new Vector2d(moveForward * moveSpeed * cos
                    + moveStrafe * moveSpeed * sin, moveForward * moveSpeed * sin
                    - moveStrafe * moveSpeed * cos);
        }
        return new Vector2d(0, 0);
    }

    public static void sendMotion(double speed, double dist) {
        Vector2d motion = new Vector2d(0, 0);
        final double x = mc.thePlayer.posX;
        final double y = Math.round(mc.thePlayer.posY / 0.015625) * 0.015625;
        final double z = mc.thePlayer.posZ;
        double d = dist;
        while (d < speed) {
            if (d > speed) {
                d = speed;
            }
            motion = getMotion(d);
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x + motion.x, y, z + motion.y, true));
            d += dist;
        }
        mc.thePlayer.setPosition(x + motion.x, mc.thePlayer.posY, z + motion.y);
    }

}