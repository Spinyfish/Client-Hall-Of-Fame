package me.kansio.client.utils.player;

import me.kansio.client.Client;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.modules.impl.combat.KillAura;
import me.kansio.client.modules.impl.combat.TargetStrafe;
import me.kansio.client.utils.Util;
import me.kansio.client.utils.network.PacketUtil;
import me.kansio.client.utils.rotations.AimUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;

import javax.vecmath.Vector2d;
import java.util.ArrayList;

public class PlayerUtil extends Util {



    public static float getBaseSpeed() {
        float baseSpeed = 0.2873F;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0F + 0.2F * (amp + 1);
        }
        return baseSpeed;
    }

    public static float getSpeed1() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed1());
    }

    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if(mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if(mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if(mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if(mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if(mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return rotationYaw;
    }

    public static void strafe(final float speed) {
        if(!mc.thePlayer.isMoving())
            return;

        final double yaw = getDirection() / 180 * Math.PI;
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static void damagePlayer() {
        if (!mc.thePlayer.onGround) return;
        double[] normalValue = new double[]{0.062, 0.0};
        double[] hypixelValue = new double[]{0.422993999998688697815, 0.002140803780930446};
        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && (mc.getCurrentServerData().serverIP.toLowerCase().contains("funcraft") || mc.getCurrentServerData().serverIP.toLowerCase().contains("cubecraft"))) {
            for (int i = 0; i < (mc.thePlayer.isPotionActive(Potion.jump) ? 15 : 8); ++i) {
                for (int length = hypixelValue.length, j = 0; j < length; ++j) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + hypixelValue[j], mc.thePlayer.posZ, false));
                }
            }
        }
        else {
            for (int i = 0; i < (mc.thePlayer.isPotionActive(Potion.jump) ? 122 : 49); ++i) {
                for (int length = normalValue.length, j = 0; j < length; ++j) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + normalValue[j], mc.thePlayer.posZ, false));
                }
            }
        }
        //final double[] jumpValue = new double[]{0.42f, 0.7531999805212, 1.00133597911214, 1.16610926093821, 1.12160004615784, 0.96636804164123};
        //double[] jumpValue = new double[]{0.0, 0.42f, 0.7531999805212, 1.00133597911214, 1.16610926093821, 1.20000004768372, 1.12160004615784, 0.96636804164123, 0.73584067272827, 0.43152384527073, 0.05489334703208, 0.0};
    }


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

    public static void damageVerus() {
        PacketUtil.sendPacketNoEvent(
                new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

        double val1 = 0;

        for (int i = 0; i <= 6; i++) {
            val1 += 0.5;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                    mc.thePlayer.posY + val1, mc.thePlayer.posZ, true));
        }

        double val2 = mc.thePlayer.posY + val1;

        ArrayList<Float> vals = new ArrayList<>();

        vals.add(0.07840000152587834f);
        vals.add(0.07840000152587834f);
        vals.add(0.23052736891295922f);
        vals.add(0.30431682745754074f);
        vals.add(0.37663049823865435f);
        vals.add(0.44749789698342113f);
        vals.add(0.5169479491049742f);
        vals.add(0.5850090015087517f);
        vals.add(0.6517088341626192f);
        vals.add(0.1537296175885956f);

        for (float value : vals) {
            val2 -= value;
        }
        mc.thePlayer.sendQueue.addToSendQueue(
                new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, val2, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));

        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));

        mc.thePlayer.motionY = getMotion(0.42f);
    }

    public static void damageVerusNoMotion() {
        PacketUtil.sendPacketNoEvent(
                new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

        double val1 = 0;

        for (int i = 0; i <= 6; i++) {
            val1 += 0.5;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                    mc.thePlayer.posY + val1, mc.thePlayer.posZ, true));
        }

        double val2 = mc.thePlayer.posY + val1;

        ArrayList<Float> vals = new ArrayList<>();

        vals.add(0.07840000152587834f);
        vals.add(0.07840000152587834f);
        vals.add(0.23052736891295922f);
        vals.add(0.30431682745754074f);
        vals.add(0.37663049823865435f);
        vals.add(0.44749789698342113f);
        vals.add(0.5169479491049742f);
        vals.add(0.5850090015087517f);
        vals.add(0.6517088341626192f);
        vals.add(0.1537296175885956f);

        for (float value : vals) {
            val2 -= value;
        }
        mc.thePlayer.sendQueue.addToSendQueue(
                new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, val2, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));

        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
    }

    /*/public static void damageVerus() {
        if (mc.thePlayer.onGround) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4.1001, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            mc.thePlayer.jump();
        }
    }/*/

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

    public static void setMotion(MoveEvent event, double moveSpeed) {
        //EntityLivingBase entity = KillAura.currentTarget;
        EntityLivingBase entity = KillAura.target;
        TargetStrafe targetStrafeClass = (TargetStrafe) Client.getInstance().getModuleManager().getModuleByName("Target Strafe");
        boolean targetStrafe = targetStrafeClass.canStrafe();
        MovementInput movementInput = mc.thePlayer.movementInput;

        double moveForward = targetStrafe ? mc.thePlayer.getDistanceToEntity(entity) <= targetStrafeClass.radius.getValue().floatValue() ? 0 : 1 : movementInput.moveForward;
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

    /*/public static double calcFriction(double moveSpeed, double lastDist, double baseMoveSpeed) {
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
    }/*/

    public static void setMotion(double moveSpeed) {
        //EntityLivingBase entity = KillAura.currentTarget;
        EntityLivingBase entity = KillAura.target;
        TargetStrafe targetStrafeClass = (TargetStrafe) Client.getInstance().getModuleManager().getModuleByName("Target Strafe");
        boolean targetStrafe = targetStrafeClass.canStrafe();
        MovementInput movementInput = mc.thePlayer.movementInput;

        double moveForward = targetStrafe ? mc.thePlayer.getDistanceToEntity(entity) <= targetStrafeClass.radius.getValue().floatValue() ? 0 : 1 : movementInput.moveForward;
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

    // From old base
    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static double getVerusBaseSpeed() {
        double base = 0.2865;
        if (mc.thePlayer.isPotionActive(1)) {
            base *= 1.0 + 0.0495 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return base;
    }

    public static void TPGROUND(MoveEvent event, double speed, double y) {
        float yaw = mc.thePlayer.rotationYaw;
        final float forward = mc.thePlayer.moveForward;
        final float strafe = mc.thePlayer.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        if (strafe > 0.0f) {
            yaw -= ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        float direction =  yaw * 0.017453292f;

        final double posX = mc.thePlayer.posX;
        final double posY = mc.thePlayer.posY;
        final double posZ = mc.thePlayer.posZ;
        final double raycastFirstX = -Math.sin(direction);
        final double raycastFirstZ = Math.cos(direction);
        final double raycastFinalX = raycastFirstX * speed;
        final double raycastFinalZ = raycastFirstZ * speed;
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX + raycastFinalX, posY + y, posZ + raycastFinalZ, true));
        mc.thePlayer.setPosition(posX + raycastFinalX, posY + y, posZ + raycastFinalZ);
    }

    public static void TP(MoveEvent event, double speed, double y) {
        float yaw = mc.thePlayer.rotationYaw;
        final float forward = mc.thePlayer.moveForward;
        final float strafe = mc.thePlayer.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        int var1 = (forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45);
        if (strafe < 0.0f) {
            yaw += var1;
        }
        if (strafe > 0.0f) {
            yaw -= var1;
        }
        float direction =  yaw * 0.017453292f;

        final double posX = mc.thePlayer.posX;
        final double posY = mc.thePlayer.posY;
        final double posZ = mc.thePlayer.posZ;
        final double raycastFirstX = -Math.sin(direction);
        final double raycastFirstZ = Math.cos(direction);
        final double raycastFinalX = raycastFirstX * speed;
        final double raycastFinalZ = raycastFirstZ * speed;
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX + raycastFinalX, posY + y, posZ + raycastFinalZ, mc.thePlayer.onGround));
        mc.thePlayer.setPosition(posX + raycastFinalX, posY + y, posZ + raycastFinalZ);
    }

    public static double getPlayerSpeed() {
        double val = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionY * mc.thePlayer.motionZ);

        if (Double.isNaN(val)) {
            return 0;
        }

        return val;
    }
}
