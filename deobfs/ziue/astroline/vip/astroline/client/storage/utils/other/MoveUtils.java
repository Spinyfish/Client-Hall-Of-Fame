/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockCactus
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.BlockGlass
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.BlockPistonBase
 *  net.minecraft.block.BlockPistonExtension
 *  net.minecraft.block.BlockPistonMoving
 *  net.minecraft.block.BlockSkull
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockStainedGlass
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.BlockTrapDoor
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C03PacketPlayer$C04PacketPlayerPosition
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 *  vip.astroline.client.service.event.impl.move.EventMove
 */
package vip.astroline.client.storage.utils.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import vip.astroline.client.service.event.impl.move.EventMove;

public class MoveUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final List<Double> frictionValues = new ArrayList<Double>();

    public static boolean isInLiquid() {
        if (MoveUtils.mc.thePlayer.isInWater()) {
            return true;
        }
        boolean inLiquid = false;
        int y = (int)MoveUtils.mc.thePlayer.getEntityBoundingBox().minY;
        int x = MathHelper.floor_double((double)MoveUtils.mc.thePlayer.getEntityBoundingBox().minX);
        while (x < MathHelper.floor_double((double)MoveUtils.mc.thePlayer.getEntityBoundingBox().maxX) + 1) {
            for (int z = MathHelper.floor_double((double)MoveUtils.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double((double)MoveUtils.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                Block block = MoveUtils.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block == null || block.getMaterial() == Material.air) continue;
                if (!(block instanceof BlockLiquid)) {
                    return false;
                }
                inLiquid = true;
            }
            ++x;
        }
        return inLiquid;
    }

    public static boolean isMoving2() {
        return MoveUtils.mc.thePlayer.moveForward != 0.0f || MoveUtils.mc.thePlayer.moveStrafing != 0.0f;
    }

    public static void hypixelTeleport(double[] startPos, BlockPos endPos) {
        double dist = Math.sqrt(MoveUtils.mc.thePlayer.getDistanceSq(endPos));
        double distanceEntreLesPackets = 0.31 + (double)(MoveUtils.getSpeedEffect() / 20);
        double ztp = 0.0;
        if (!(dist > distanceEntreLesPackets)) {
            MoveUtils.mc.thePlayer.setPosition((double)endPos.getX(), (double)endPos.getY(), (double)endPos.getZ());
        } else {
            double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1L;
            double xtp = MoveUtils.mc.thePlayer.posX;
            double ytp = MoveUtils.mc.thePlayer.posY;
            ztp = MoveUtils.mc.thePlayer.posZ;
            double count = 0.0;
            int i = 1;
            while ((double)i < nbPackets) {
                double xdi = ((double)endPos.getX() - MoveUtils.mc.thePlayer.posX) / nbPackets;
                double zdi = ((double)endPos.getZ() - MoveUtils.mc.thePlayer.posZ) / nbPackets;
                double ydi = ((double)endPos.getY() - MoveUtils.mc.thePlayer.posY) / nbPackets;
                count += 1.0;
                if (!MoveUtils.mc.theWorld.getBlockState(new BlockPos(xtp += xdi, (ytp += ydi) - 1.0, ztp += zdi)).getBlock().isFullBlock()) {
                    if (count <= 2.0) {
                        ytp += 2.0E-8;
                    } else if (count >= 4.0) {
                        count = 0.0;
                    }
                }
                C03PacketPlayer.C04PacketPlayerPosition Packet2 = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, false);
                mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)Packet2);
                ++i;
            }
            MoveUtils.mc.thePlayer.setPosition((double)endPos.getX() + 0.5, (double)endPos.getY(), (double)endPos.getZ() + 0.5);
        }
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (!MoveUtils.mc.thePlayer.isPotionActive(Potion.jump)) return baseJumpHeight;
        int amplifier = MoveUtils.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
        baseJumpHeight += (double)((float)(amplifier + 1) * 0.1f);
        return baseJumpHeight;
    }

    public static double defaultSpeed() {
        return MoveUtils.defaultSpeed((EntityLivingBase)MoveUtils.mc.thePlayer);
    }

    public static double defaultSpeed(EntityLivingBase entity) {
        return MoveUtils.defaultSpeed(entity, 0.2);
    }

    public static double defaultSpeed(EntityLivingBase entity, double effectBoost) {
        double baseSpeed = 0.2873;
        if (!entity.isPotionActive(Potion.moveSpeed)) return baseSpeed;
        int amplifier = entity.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
        baseSpeed *= 1.0 + effectBoost * (double)(amplifier + 1);
        return baseSpeed;
    }

    public static void strafe() {
        MoveUtils.strafe(MoveUtils.getSpeed());
    }

    public static void strafe(EventMove e) {
        MoveUtils.strafe(e, MoveUtils.getSpeed());
    }

    public static void strafe(double d) {
        if (!MoveUtils.isMoving()) {
            return;
        }
        double yaw = MoveUtils.getDirection();
        MoveUtils.mc.thePlayer.motionX = -Math.sin(yaw) * d;
        MoveUtils.mc.thePlayer.motionZ = Math.cos(yaw) * d;
    }

    public static void strafe(EventMove e, double d) {
        if (!MoveUtils.isMoving()) {
            return;
        }
        double yaw = MoveUtils.getDirection();
        MoveUtils.mc.thePlayer.motionX = -Math.sin(yaw) * d;
        e.setX(MoveUtils.mc.thePlayer.motionX);
        MoveUtils.mc.thePlayer.motionZ = Math.cos(yaw) * d;
        e.setZ(MoveUtils.mc.thePlayer.motionZ);
    }

    public static final void doStrafe(double speed) {
        if (!MoveUtils.isMoving()) {
            return;
        }
        double yaw = MoveUtils.getYaw(true);
        MoveUtils.mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        MoveUtils.mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public final void doStrafe(double speed, double yaw) {
        MoveUtils.mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        MoveUtils.mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static double getDirection() {
        float rotationYaw = MoveUtils.mc.thePlayer.rotationYaw;
        if (MoveUtils.mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MoveUtils.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (MoveUtils.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MoveUtils.mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (!(MoveUtils.mc.thePlayer.moveStrafing < 0.0f)) return Math.toRadians(rotationYaw);
        rotationYaw += 90.0f * forward;
        return Math.toRadians(rotationYaw);
    }

    public static float getMovementDirection(float forward, float strafing, float yaw) {
        boolean reversed;
        if (forward == 0.0f && strafing == 0.0f) {
            return yaw;
        }
        boolean bl = reversed = forward < 0.0f;
        float strafingYaw = 90.0f * (forward > 0.0f ? 0.5f : (reversed ? -0.5f : 1.0f));
        if (reversed) {
            yaw += 180.0f;
        }
        if (strafing > 0.0f) {
            yaw -= strafingYaw;
        } else {
            if (!(strafing < 0.0f)) return yaw;
            yaw += strafingYaw;
        }
        return yaw;
    }

    public static boolean isOverVoid() {
        double posY = MoveUtils.mc.thePlayer.posY;
        while (posY > 0.0) {
            if (!(MoveUtils.mc.theWorld.getBlockState(new BlockPos(MoveUtils.mc.thePlayer.posX, posY, MoveUtils.mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
            posY -= 1.0;
        }
        return true;
    }

    public final void doStrafe() {
        MoveUtils.doStrafe(MoveUtils.getSpeed());
    }

    public static boolean isMoving() {
        return MoveUtils.mc.thePlayer != null && (MoveUtils.mc.thePlayer.movementInput.moveForward != 0.0f || MoveUtils.mc.thePlayer.movementInput.moveStrafe != 0.0f);
    }

    public static float getSpeed() {
        return (float)Math.sqrt(MoveUtils.mc.thePlayer.motionX * MoveUtils.mc.thePlayer.motionX + MoveUtils.mc.thePlayer.motionZ * MoveUtils.mc.thePlayer.motionZ);
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875;
        if (!MoveUtils.mc.thePlayer.isPotionActive(Potion.moveSpeed)) return baseSpeed;
        baseSpeed *= 1.0 + 0.2 * (double)(MoveUtils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        return baseSpeed;
    }

    public static double calculateFriction(double moveSpeed, double lastDist, double baseMoveSpeedRef) {
        frictionValues.clear();
        frictionValues.add(lastDist - lastDist / 159.9999985);
        frictionValues.add(lastDist - (moveSpeed - lastDist) / 33.3);
        double materialFriction = MoveUtils.mc.thePlayer.isInWater() ? (double)0.89f : (MoveUtils.mc.thePlayer.isInLava() ? (double)0.535f : (double)0.98f);
        frictionValues.add(lastDist - baseMoveSpeedRef * (1.0 - materialFriction));
        return Collections.min(frictionValues);
    }

    public static final double getYaw(boolean strafing) {
        float rotationYaw = MoveUtils.mc.thePlayer.rotationYawHead;
        float forward = 1.0f;
        double moveForward = MoveUtils.mc.thePlayer.movementInput.moveForward;
        double moveStrafing = MoveUtils.mc.thePlayer.movementInput.moveStrafe;
        float yaw = MoveUtils.mc.thePlayer.rotationYaw;
        if (moveForward < 0.0) {
            rotationYaw += 180.0f;
        }
        if (moveForward < 0.0) {
            forward = -0.5f;
        } else if (moveForward > 0.0) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0f * forward;
        } else {
            if (!(moveStrafing < 0.0)) return Math.toRadians(rotationYaw);
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static boolean isBlockNearBy(double distance) {
        double smallX = Math.min(MoveUtils.mc.thePlayer.posX - distance, MoveUtils.mc.thePlayer.posX + distance);
        double smallY = Math.min(MoveUtils.mc.thePlayer.posY, MoveUtils.mc.thePlayer.posY);
        double smallZ = Math.min(MoveUtils.mc.thePlayer.posZ - distance, MoveUtils.mc.thePlayer.posZ + distance);
        double bigX = Math.max(MoveUtils.mc.thePlayer.posX - distance, MoveUtils.mc.thePlayer.posX + distance);
        double bigY = Math.max(MoveUtils.mc.thePlayer.posY, MoveUtils.mc.thePlayer.posY);
        double bigZ = Math.max(MoveUtils.mc.thePlayer.posZ - distance, MoveUtils.mc.thePlayer.posZ + distance);
        int x = (int)smallX;
        block0: while ((double)x <= bigX) {
            int y = (int)smallY;
            while (true) {
                int z;
                if ((double)y <= bigY) {
                    z = (int)smallZ;
                } else {
                    ++x;
                    continue block0;
                }
                while ((double)z <= bigZ) {
                    if (!MoveUtils.checkPositionValidity(new Vec3((double)x, (double)y, (double)z)) && MoveUtils.checkPositionValidity(new Vec3((double)x, (double)(y + 1), (double)z))) {
                        return true;
                    }
                    ++z;
                }
                ++y;
            }
            break;
        }
        return false;
    }

    public static boolean checkPositionValidity(Vec3 vec3) {
        BlockPos pos = new BlockPos(vec3);
        if (MoveUtils.isBlockSolid(pos)) return false;
        if (!MoveUtils.isBlockSolid(pos.add(0, 1, 0))) return MoveUtils.isSafeToWalkOn(pos.add(0, -1, 0));
        return false;
    }

    private static boolean isBlockSolid(BlockPos pos) {
        Block block = MoveUtils.mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockSlab || block instanceof BlockStairs || block instanceof BlockCactus || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSkull || block instanceof BlockPane || block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockGlass || block instanceof BlockPistonBase || block instanceof BlockPistonExtension || block instanceof BlockPistonMoving || block instanceof BlockStainedGlass || block instanceof BlockTrapDoor;
    }

    private static boolean isSafeToWalkOn(BlockPos pos) {
        Block block = MoveUtils.mc.theWorld.getBlockState(pos).getBlock();
        return !(block instanceof BlockFence) && !(block instanceof BlockWall);
    }

    public static void setMotion(double speed) {
        MoveUtils.setMotion(speed, MoveUtils.mc.thePlayer.rotationYaw);
    }

    public static void setMotion(EventMove e, double speed, float yaw) {
        double forward = MoveUtils.mc.thePlayer.movementInput.moveForward;
        double strafe = MoveUtils.mc.thePlayer.movementInput.moveStrafe;
        if (forward == 0.0 && strafe == 0.0) {
            e.x = 0.0;
            MoveUtils.mc.thePlayer.motionX = 0.0;
            e.z = 0.0;
            MoveUtils.mc.thePlayer.motionZ = 0.0;
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
            MoveUtils.mc.thePlayer.motionX = e.x = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            MoveUtils.mc.thePlayer.motionZ = e.z = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }

    public static void setMotion(EventMove e, double speed) {
        MoveUtils.setMotion(e, speed, MoveUtils.mc.thePlayer.rotationYaw);
    }

    public static void setMotion(double speed, float yaw) {
        double forward = MoveUtils.mc.thePlayer.movementInput.moveForward;
        double strafe = MoveUtils.mc.thePlayer.movementInput.moveStrafe;
        if (forward == 0.0 && strafe == 0.0) {
            MoveUtils.mc.thePlayer.motionX = 0.0;
            MoveUtils.mc.thePlayer.motionZ = 0.0;
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
            MoveUtils.mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            MoveUtils.mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }

    public static boolean checkTeleport(double x, double y, double z, double distBetweenPackets) {
        double distx = MoveUtils.mc.thePlayer.posX - x;
        double disty = MoveUtils.mc.thePlayer.posY - y;
        double distz = MoveUtils.mc.thePlayer.posZ - z;
        double dist = Math.sqrt(MoveUtils.mc.thePlayer.getDistanceSq(x, y, z));
        double distanceEntreLesPackets = distBetweenPackets;
        double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1L;
        double xtp = MoveUtils.mc.thePlayer.posX;
        double ytp = MoveUtils.mc.thePlayer.posY;
        double ztp = MoveUtils.mc.thePlayer.posZ;
        int i = 1;
        while ((double)i < nbPackets) {
            AxisAlignedBB bb;
            double xdi = (x - MoveUtils.mc.thePlayer.posX) / nbPackets;
            double ydi = (y - MoveUtils.mc.thePlayer.posY) / nbPackets;
            double zdi = (z - MoveUtils.mc.thePlayer.posZ) / nbPackets;
            if (!MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb = new AxisAlignedBB((xtp += xdi) - 0.3, ytp += ydi, (ztp += zdi) - 0.3, xtp + 0.3, ytp + 1.8, ztp + 0.3)).isEmpty()) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static boolean isOnGround(double height) {
        if (MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, MoveUtils.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty()) return false;
        return true;
    }

    public static boolean isOnGround(Entity entity, double height) {
        if (MoveUtils.mc.theWorld.getCollidingBoundingBoxes(entity, entity.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty()) return false;
        return true;
    }

    public static int getJumpEffect() {
        if (!MoveUtils.mc.thePlayer.isPotionActive(Potion.jump)) return 0;
        return MoveUtils.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
    }

    public static int getSpeedEffect() {
        if (!MoveUtils.mc.thePlayer.isPotionActive(Potion.moveSpeed)) return 0;
        return MoveUtils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
    }

    public static int getSpeedEffect(EntityPlayer player) {
        if (!player.isPotionActive(Potion.moveSpeed)) return 0;
        return player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return MoveUtils.mc.theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }

    public static Block getBlockAtPosC(double x, double y, double z) {
        EntityPlayerSP inPlayer = MoveUtils.mc.thePlayer;
        return MoveUtils.mc.theWorld.getBlockState(new BlockPos(inPlayer.posX + x, inPlayer.posY + y, inPlayer.posZ + z)).getBlock();
    }

    /*
     * Enabled force condition propagation
     */
    public static float getDistanceToGround(Entity e) {
        float a;
        block13: {
            int id;
            int i;
            int j;
            int[] arrayOfInt1;
            Block block;
            int[] exemptIds;
            block14: {
                block12: {
                    block11: {
                        block10: {
                            if (MoveUtils.mc.thePlayer.isCollidedVertically && MoveUtils.mc.thePlayer.onGround) {
                                return 0.0f;
                            }
                            a = (float)e.posY;
                            while (a > 0.0f) {
                                int[] stairs = new int[]{53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180};
                                exemptIds = new int[]{6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157, 171, 175, 176, 177};
                                block = MoveUtils.mc.theWorld.getBlockState(new BlockPos(e.posX, (double)(a - 1.0f), e.posZ)).getBlock();
                                if (!(block instanceof BlockAir)) {
                                    if (Block.getIdFromBlock((Block)block) == 44 || Block.getIdFromBlock((Block)block) == 126) {
                                        if ((float)(e.posY - (double)a - 0.5) < 0.0f) {
                                            return 0.0f;
                                        }
                                        break block10;
                                    }
                                    arrayOfInt1 = stairs;
                                    j = stairs.length;
                                    break block11;
                                }
                                a -= 1.0f;
                            }
                            return 0.0f;
                        }
                        float f = (float)(e.posY - (double)a - 0.5);
                        return f;
                    }
                    for (i = 0; i < j; ++i) {
                        id = arrayOfInt1[i];
                        if (Block.getIdFromBlock((Block)block) != id) continue;
                        if ((float)(e.posY - (double)a - 1.0) < 0.0f) {
                            return 0.0f;
                        }
                        break block12;
                    }
                    break block14;
                }
                float f = (float)(e.posY - (double)a - 1.0);
                return f;
            }
            arrayOfInt1 = exemptIds;
            j = exemptIds.length;
            i = 0;
            while (i < j) {
                id = arrayOfInt1[i];
                if (Block.getIdFromBlock((Block)block) == id) {
                    if ((float)(e.posY - (double)a) < 0.0f) {
                        return 0.0f;
                    }
                    break block13;
                }
                ++i;
            }
            return (float)(e.posY - (double)a + block.getBlockBoundsMaxY() - 1.0);
        }
        float f = (float)(e.posY - (double)a);
        return f;
    }

    public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = (double)block.getX() + 0.5 - MoveUtils.mc.thePlayer.posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - MoveUtils.mc.thePlayer.posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y = (double)block.getY() + 0.5;
        double d1 = MoveUtils.mc.thePlayer.posY + (double)MoveUtils.mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double((double)(x * x + z * z));
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (!(yaw < 0.0f)) return new float[]{yaw, pitch};
        yaw += 360.0f;
        return new float[]{yaw, pitch};
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(MoveUtils.mc.thePlayer.posX - 0.3, MoveUtils.mc.thePlayer.posY + (double)MoveUtils.mc.thePlayer.getEyeHeight(), MoveUtils.mc.thePlayer.posZ + 0.3, MoveUtils.mc.thePlayer.posX + 0.3, MoveUtils.mc.thePlayer.posY + 2.5, MoveUtils.mc.thePlayer.posZ - 0.3);
        return !MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb).isEmpty();
    }

    public static boolean isCollidedH(double dist) {
        AxisAlignedBB bb = new AxisAlignedBB(MoveUtils.mc.thePlayer.posX - 0.3, MoveUtils.mc.thePlayer.posY + 2.0, MoveUtils.mc.thePlayer.posZ + 0.3, MoveUtils.mc.thePlayer.posX + 0.3, MoveUtils.mc.thePlayer.posY + 3.0, MoveUtils.mc.thePlayer.posZ - 0.3);
        if (!MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(0.3 + dist, 0.0, 0.0)).isEmpty()) {
            return true;
        }
        if (!MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(-0.3 - dist, 0.0, 0.0)).isEmpty()) {
            return true;
        }
        if (!MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(0.0, 0.0, 0.3 + dist)).isEmpty()) {
            return true;
        }
        if (MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(0.0, 0.0, -0.3 - dist)).isEmpty()) return false;
        return true;
    }

    public static boolean isRealCollidedH(double dist) {
        AxisAlignedBB bb = new AxisAlignedBB(MoveUtils.mc.thePlayer.posX - 0.3, MoveUtils.mc.thePlayer.posY + 0.5, MoveUtils.mc.thePlayer.posZ + 0.3, MoveUtils.mc.thePlayer.posX + 0.3, MoveUtils.mc.thePlayer.posY + 1.9, MoveUtils.mc.thePlayer.posZ - 0.3);
        if (!MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(0.3 + dist, 0.0, 0.0)).isEmpty()) {
            return true;
        }
        if (!MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(-0.3 - dist, 0.0, 0.0)).isEmpty()) {
            return true;
        }
        if (!MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(0.0, 0.0, 0.3 + dist)).isEmpty()) {
            return true;
        }
        if (MoveUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MoveUtils.mc.thePlayer, bb.offset(0.0, 0.0, -0.3 - dist)).isEmpty()) return false;
        return true;
    }

    public static boolean isOnLiquid() {
        if (MoveUtils.mc.thePlayer == null) {
            return false;
        }
        boolean onLiquid = false;
        int y = (int)MoveUtils.mc.thePlayer.boundingBox.offset((double)0.0, (double)-0.0, (double)0.0).minY;
        int x = MathHelper.floor_double((double)MoveUtils.mc.thePlayer.boundingBox.minX);
        while (x < MathHelper.floor_double((double)MoveUtils.mc.thePlayer.boundingBox.maxX) + 1) {
            for (int z = MathHelper.floor_double((double)MoveUtils.mc.thePlayer.boundingBox.minZ); z < MathHelper.floor_double((double)MoveUtils.mc.thePlayer.boundingBox.maxZ) + 1; ++z) {
                Block block = MoveUtils.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block == null || block instanceof BlockAir) continue;
                if (!(block instanceof BlockLiquid)) {
                    return false;
                }
                onLiquid = true;
            }
            ++x;
        }
        return onLiquid;
    }
}
