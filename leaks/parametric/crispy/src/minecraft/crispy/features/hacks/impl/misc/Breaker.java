package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.util.ArrayList;

@HackInfo(name = "BedFlipper", category = Category.MISC)
public class Breaker extends Hack {


    private BlockPos blockCake;
    private BlockPos blockBed;
    NumberValue<Integer> range = new NumberValue<Integer>("Range", 4, 4, 6);
    BooleanValue swing = new BooleanValue("Swing", true);
    BooleanValue cake = new BooleanValue("Cake", true);
    BooleanValue bed = new BooleanValue("Bed", true);
    BooleanValue walls = new BooleanValue("Mineplex through walls", true);

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            int radius = range.getObject();
            if (blockCake != null) {

                EnumFacing direction = getFacingDirection(blockCake);
                if (direction != null) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, Minecraft.theWorld, mc.thePlayer.inventory.getCurrentItem(), blockCake, direction, new Vec3(0, 0, 0))) {
                        if (!swing.getObject()) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        } else {
                            mc.thePlayer.swingItem();
                        }
                    } else {
                        if (!swing.getObject()) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        } else {
                            mc.thePlayer.swingItem();
                        }
                    }
                }
            }
            if (blockBed != null) {

                EnumFacing direction = getFacingDirection(blockBed);
                if (direction != null) {

                    mc.playerController.onPlayerDamageBlock(blockBed, direction);

                }
            }

            for (int x = -radius; x < radius; x++) {
                for (int y = radius; y > -radius; y--) {
                    for (int z = -radius; z < radius; z++) {
                        int xPos = (int) (mc.thePlayer.posX + x);
                        int yPos = (int) (mc.thePlayer.posY + y);
                        int zPos = (int) (mc.thePlayer.posZ + z);

                        BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                        Block block = Minecraft.theWorld.getBlockState(blockPos).getBlock();
                        if (block instanceof BlockBed && bed.getObject()) {
                            blockBed = blockPos;
                            return;

                        }
                        if (block instanceof BlockCake && cake.getObject()) {
                            BlockPos above = new BlockPos(xPos, yPos + 1, zPos);
                            Block aboveBlock = Minecraft.theWorld.getBlockState(above).getBlock();
                            if(!(aboveBlock instanceof BlockAir) && walls.getObject()) {
                                blockBed = above;
                                blockCake = null;

                                return;
                            }
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(blockPos, 1, mc.thePlayer.inventory.getCurrentItem(), 1f, 1f,1f));

                            blockCake = blockPos;
                            return;
                        }

                    }
                }
            }
            blockBed = null;
            blockCake = null;
        }
    }

    private float[] getFacingRotations(int paramInt1, int paramInt2, int paramInt3, EnumFacing paramEnumFacing) {
        EntityPig localEntityPig = new EntityPig(Minecraft.theWorld);
        localEntityPig.posX = (double) paramInt1 + 0.5;
        localEntityPig.posY = (double) paramInt2 + 0.5;
        localEntityPig.posZ = (double) paramInt3 + 0.5;
        localEntityPig.posX += (double) paramEnumFacing.getDirectionVec().getX() * 0.25;
        localEntityPig.posY += (double) paramEnumFacing.getDirectionVec().getY() * 0.25;
        localEntityPig.posZ += (double) paramEnumFacing.getDirectionVec().getZ() * 0.25;
        return jdMethod_double(localEntityPig);
    }

    float[] jdMethod_double(EntityLivingBase paramEntityLivingBase) {
        double d1 = paramEntityLivingBase.posX - mc.thePlayer.posX;
        double d2 = paramEntityLivingBase.posY + (double) paramEntityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double d3 = paramEntityLivingBase.posZ - mc.thePlayer.posZ;
        double d4 = MathHelper.sqrt_double(d1 * d1 + d3 * d3);
        float f1 = (float) (Math.atan2(d3, d1) * 180.0 / 3.141592653589793) - 90.0f;
        float f2 = (float) (-Math.atan2(d2, d4) * 180.0 / 3.141592653589793);
        return new float[]{f1, f2};
    }

    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;
        if (!Minecraft.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.UP;
        } else if (!Minecraft.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.DOWN;
        } else if (!Minecraft.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.EAST;
        } else if (!Minecraft.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.WEST;
        } else if (!Minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isFullCube()) {
            direction = EnumFacing.SOUTH;
        } else if (!Minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isFullCube()) {
            direction = EnumFacing.NORTH;
        }
        MovingObjectPosition rayResult = Minecraft.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null && rayResult.getBlockPos() == pos) {
            return rayResult.sideHit;
        }
        return direction;
    }


}
