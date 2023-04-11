//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package byron.Mono.utils;

import java.util.ArrayList;
import java.util.Iterator;

import byron.Mono.event.impl.EventMove;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class MovementUtils {
    protected static Minecraft mc = Minecraft.getMinecraft();

    public MovementUtils() {
    }

    public static double defaultSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
        }

        return baseSpeed;
    }

    public static void strafe(double speed) {
        float a = mc.thePlayer.rotationYaw * 0.017453292F;
        float l = mc.thePlayer.rotationYaw * 0.017453292F - 4.712389F;
        float r = mc.thePlayer.rotationYaw * 0.017453292F + 4.712389F;
        float rf = mc.thePlayer.rotationYaw * 0.017453292F + 0.5969026F;
        float lf = mc.thePlayer.rotationYaw * 0.017453292F + -0.5969026F;
        float lb = mc.thePlayer.rotationYaw * 0.017453292F - 2.3876104F;
        float rb = mc.thePlayer.rotationYaw * 0.017453292F - -2.3876104F;
        EntityPlayerSP var10000;
        if (mc.gameSettings.keyBindForward.pressed) {
            if (mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
                var10000 = mc.thePlayer;
                var10000.motionX -= (double)MathHelper.sin(lf) * speed;
                var10000 = mc.thePlayer;
                var10000.motionZ += (double)MathHelper.cos(lf) * speed;
            } else if (mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindLeft.pressed) {
                var10000 = mc.thePlayer;
                var10000.motionX -= (double)MathHelper.sin(rf) * speed;
                var10000 = mc.thePlayer;
                var10000.motionZ += (double)MathHelper.cos(rf) * speed;
            } else {
                var10000 = mc.thePlayer;
                var10000.motionX -= (double)MathHelper.sin(a) * speed;
                var10000 = mc.thePlayer;
                var10000.motionZ += (double)MathHelper.cos(a) * speed;
            }
        } else if (mc.gameSettings.keyBindBack.pressed) {
            if (mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
                var10000 = mc.thePlayer;
                var10000.motionX -= (double)MathHelper.sin(lb) * speed;
                var10000 = mc.thePlayer;
                var10000.motionZ += (double)MathHelper.cos(lb) * speed;
            } else if (mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindLeft.pressed) {
                var10000 = mc.thePlayer;
                var10000.motionX -= (double)MathHelper.sin(rb) * speed;
                var10000 = mc.thePlayer;
                var10000.motionZ += (double)MathHelper.cos(rb) * speed;
            } else {
                var10000 = mc.thePlayer;
                var10000.motionX += (double)MathHelper.sin(a) * speed;
                var10000 = mc.thePlayer;
                var10000.motionZ -= (double)MathHelper.cos(a) * speed;
            }
        } else if (mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindBack.pressed) {
            var10000 = mc.thePlayer;
            var10000.motionX += (double)MathHelper.sin(l) * speed;
            var10000 = mc.thePlayer;
            var10000.motionZ -= (double)MathHelper.cos(l) * speed;
        } else if (mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindBack.pressed) {
            var10000 = mc.thePlayer;
            var10000.motionX += (double)MathHelper.sin(r) * speed;
            var10000 = mc.thePlayer;
            var10000.motionZ -= (double)MathHelper.cos(r) * speed;
        }

    }

    public static void setMotion(double speed) {
        double forward = (double)mc.thePlayer.movementInput.moveForward;
        double strafe = (double)mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0D && strafe == 0.0D) {
            mc.thePlayer.motionX = 0.0D;
            mc.thePlayer.motionZ = 0.0D;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (float)(forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (float)(forward > 0.0D ? 45 : -45);
                }

                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) {
                    forward = -1.0D;
                }
            }

            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F)));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        }

    }

    public static boolean checkTeleport(double x, double y, double z, double distBetweenPackets) {
        double var10000 = mc.thePlayer.posX - x;
        var10000 = mc.thePlayer.posY - y;
        var10000 = mc.thePlayer.posZ - z;
        double dist = Math.sqrt(mc.thePlayer.getDistanceSq(x, y, z));
        double nbPackets = (double)(Math.round(dist / distBetweenPackets + 0.49999999999D) - 1L);
        double xtp = mc.thePlayer.posX;
        double ytp = mc.thePlayer.posY;
        double ztp = mc.thePlayer.posZ;

        for(int i = 1; (double)i < nbPackets; ++i) {
            double xdi = (x - mc.thePlayer.posX) / nbPackets;
            xtp += xdi;
            double zdi = (z - mc.thePlayer.posZ) / nbPackets;
            ztp += zdi;
            double ydi = (y - mc.thePlayer.posY) / nbPackets;
            ytp += ydi;
            AxisAlignedBB bb = new AxisAlignedBB(xtp - 0.3D, ytp, ztp - 0.3D, xtp + 0.3D, ytp + 1.8D, ztp + 0.3D);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static int getJumpEffect() {
        return mc.thePlayer.isPotionActive(Potion.jump) ? mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0;
    }

    public static int getSpeedEffect() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }

    public static Block getBlockAtPosC(double x, double y, double z) {
        EntityPlayer inPlayer = Minecraft.getMinecraft().thePlayer;
        return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(inPlayer.posX + x, inPlayer.posY + y, inPlayer.posZ + z)).getBlock();
    }

    public static float getDistanceToGround(Entity e) {
        if (mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
            return 0.0F;
        } else {
            for(float a = (float)e.posY; a > 0.0F; --a) {
                int[] stairs = new int[]{53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180};
                int[] exemptIds = new int[]{6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157, 171, 175, 176, 177};
                Block block = mc.theWorld.getBlockState(new BlockPos(e.posX, (double)(a - 1.0F), e.posZ)).getBlock();
                if (!(block instanceof BlockAir)) {
                    if (Block.getIdFromBlock(block) != 44 && Block.getIdFromBlock(block) != 126) {
                        int[] arrayOfInt1 = stairs;
                        int j = stairs.length;

                        int i;
                        int id;
                        for(i = 0; i < j; ++i) {
                            id = arrayOfInt1[i];
                            if (Block.getIdFromBlock(block) == id) {
                                return (float)(e.posY - (double)a - 1.0D) < 0.0F ? 0.0F : (float)(e.posY - (double)a - 1.0D);
                            }
                        }

                        arrayOfInt1 = exemptIds;
                        j = exemptIds.length;

                        for(i = 0; i < j; ++i) {
                            id = arrayOfInt1[i];
                            if (Block.getIdFromBlock(block) == id) {
                                return (float)(e.posY - (double)a) < 0.0F ? 0.0F : (float)(e.posY - (double)a);
                            }
                        }

                        return (float)(e.posY - (double)a + block.getBlockBoundsMaxY() - 1.0D);
                    }

                    return (float)(e.posY - (double)a - 0.5D) < 0.0F ? 0.0F : (float)(e.posY - (double)a - 0.5D);
                }
            }

            return 0.0F;
        }
    }

    public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = (double)block.getX() + 0.5D - mc.thePlayer.posX + (double)face.getFrontOffsetX() / 2.0D;
        double z = (double)block.getZ() + 0.5D - mc.thePlayer.posZ + (double)face.getFrontOffsetZ() / 2.0D;
        double y = (double)block.getY() + 0.5D;
        double d1 = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - y;
        double d3 = (double)MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0D / 3.141592653589793D);
        if (yaw < 0.0F) {
            yaw += 360.0F;
        }

        return new float[]{yaw, pitch};
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3D, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ + 0.3D, mc.thePlayer.posX + 0.3D, mc.thePlayer.posY + 2.5D, mc.thePlayer.posZ - 0.3D);
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty();
    }

    public static boolean isCollidedH(double dist) {
        AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3D, mc.thePlayer.posY + 2.0D, mc.thePlayer.posZ + 0.3D, mc.thePlayer.posX + 0.3D, mc.thePlayer.posY + 3.0D, mc.thePlayer.posZ - 0.3D);
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.3D + dist, 0.0D, 0.0D)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(-0.3D - dist, 0.0D, 0.0D)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.0D, 0.0D, 0.3D + dist)).isEmpty()) {
            return true;
        } else {
            return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.0D, 0.0D, -0.3D - dist)).isEmpty();
        }
    }

    public static boolean isRealCollidedH(double dist) {
        AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3D, mc.thePlayer.posY + 0.5D, mc.thePlayer.posZ + 0.3D, mc.thePlayer.posX + 0.3D, mc.thePlayer.posY + 1.9D, mc.thePlayer.posZ - 0.3D);
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.3D + dist, 0.0D, 0.0D)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(-0.3D - dist, 0.0D, 0.0D)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.0D, 0.0D, 0.3D + dist)).isEmpty()) {
            return true;
        } else {
            return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.0D, 0.0D, -0.3D - dist)).isEmpty();
        }
    }

    public static boolean isMovingOnGround() {
        return isMoving() && mc.thePlayer.onGround;
    }

    public static float getRetarded() {
        return 0.2873F;
    }

    public static double getJumpHeight(double speed) {
        return mc.thePlayer.isPotionActive(Potion.jump) ? speed + 0.1D * (double)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) : speed;
    }

    public static void sendPosition(double x, double y, double z, boolean ground, boolean moving) {
        if (!moving) {
            mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + y, mc.thePlayer.posZ, ground));
        } else {
            mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, ground));
        }

    }

    public static Block getBlockAtPos(BlockPos inBlockPos) {
        IBlockState s = mc.theWorld.getBlockState(inBlockPos);
        return s.getBlock();
    }

    public static float getDirection() {
        float var1 = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0F) {
            var1 += 180.0F;
        }

        float forward = 1.0F;
        if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0.0F) {
            var1 -= 90.0F * forward;
        }

        if (mc.thePlayer.moveStrafing < 0.0F) {
            var1 += 90.0F * forward;
        }

        var1 *= 0.017453292F;
        return var1;
    }

    public static void damagePlayer() {
        mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.0001D, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, Minecraft.getMinecraft().thePlayer.rotationYaw, (double)Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe, (double)Minecraft.getMinecraft().thePlayer.movementInput.moveForward);
    }

    public static void setSpeed(double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (pseudoForward != 0.0D) {
            if (pseudoStrafe > 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? -45 : 45);
            } else if (pseudoStrafe < 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (pseudoForward > 0.0D) {
                forward = 1.0D;
            } else if (pseudoForward < 0.0D) {
                forward = -1.0D;
            }
        }

        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }

        double offsetX = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double offsetZ = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        mc.thePlayer.motionX = forward * moveSpeed * offsetX + strafe * moveSpeed * offsetZ;
        mc.thePlayer.motionZ = forward * moveSpeed * offsetZ - strafe * moveSpeed * offsetX;
    }

    public static void strafe(float speed) {
        if (isMoving()) {
            double yaw = (double)getDirection();
            Minecraft.getMinecraft().thePlayer.motionX = -Math.sin(yaw) * (double)speed;
            Minecraft.getMinecraft().thePlayer.motionZ = Math.cos(yaw) * (double)speed;
        }

    }

    public static void strafe() {
        strafe(getSpeed());
    }


    public static void damageVerus() {
        double val1 = 0.0D;

        for(int i = 0; i <= 6; ++i) {
            val1 += 0.5D;
            mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + val1, mc.thePlayer.posZ, true));
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
        }

        double val2 = mc.thePlayer.posY + val1;
        ArrayList<Float> vals = new ArrayList();
        vals.add(0.0784F);
        vals.add(0.0784F);
        vals.add(0.23052737F);
        vals.add(0.30431682F);
        vals.add(0.37663049F);
        vals.add(0.4474979F);
        vals.add(0.5169479F);
        vals.add(0.585009F);
        vals.add(0.65170884F);
        vals.add(0.15372962F);
        Iterator var5 = vals.iterator();

        while(var5.hasNext()) {
            float value = (Float)var5.next();
            val2 -= (double)value;
            mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, val2, mc.thePlayer.posZ, false));
        }

        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0D + 0.2D * (double)(mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F;
    }

    public static double defaultMoveSpeed() {
        return mc.thePlayer.isSprinting() ? 0.28700000047683716D : 0.22300000488758087D;
    }

    public static double getLastDistance() {
        return Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static double jumpHeight() {
        return mc.thePlayer.isPotionActive(Potion.jump) ? 0.419999986886978D + 0.1D * (double)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) : 0.419999986886978D;
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (double)((float)(amplifier + 1) * 0.1F);
        }

        return baseJumpHeight;
    }

    public static float getSpeed() {
        return (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public double getTickDist() {
        double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(zDist, 2.0D));
    }

    public static void setSpeed2(final EventMove moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0F;
            if (forward > 0.0) {
                forward = 1F;
            } else if (forward < 0.0) {
                forward = -1F;
            }
        }

        if (strafe > 0.0) {
            strafe = 1F;
        } else if (strafe < 0.0) {
            strafe = -1F;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.x = (forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.z = (forward * moveSpeed * mz - strafe * moveSpeed * mx);

    }

}
