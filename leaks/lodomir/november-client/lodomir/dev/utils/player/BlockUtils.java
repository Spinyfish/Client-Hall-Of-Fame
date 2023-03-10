/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.player;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BlockUtils {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static final List<Block> BlackList = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch, Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner, Blocks.wall_banner, Blocks.redstone_torch);

    public static void placeBlock(BlockPos blockPos, EnumFacing face, Vec3 vec, ItemStack stack) {
        if (BlockUtils.mc.thePlayer != null && BlockUtils.mc.theWorld != null) {
            BlockUtils.mc.playerController.onPlayerRightClick(BlockUtils.mc.thePlayer, BlockUtils.mc.theWorld, stack, blockPos, face, vec);
        }
    }

    public static Block getBlockAtPos(BlockPos pos) {
        IBlockState blockState = BlockUtils.getBlockStateAtPos(pos);
        if (blockState == null) {
            return null;
        }
        return blockState.getBlock();
    }

    public static IBlockState getBlockStateAtPos(BlockPos pos) {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null) {
            return null;
        }
        return Minecraft.getMinecraft().theWorld.getBlockState(pos);
    }

    public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
        EntityEgg var4 = new EntityEgg(BlockUtils.mc.theWorld);
        var4.posX = (double)var0 + 0.5;
        var4.posY = (double)var1 + 0.5;
        var4.posZ = (double)var2 + 0.5;
        var4.posX += (double)var3.getDirectionVec().getX() * 0.25;
        var4.posY += (double)var3.getDirectionVec().getY() * 0.25;
        var4.posZ += (double)var3.getDirectionVec().getZ() * 0.25;
        return BlockUtils.getDirectionToEntity(var4);
    }

    private static float[] getDirectionToEntity(Entity var0) {
        return new float[]{BlockUtils.getYaw(var0) + BlockUtils.mc.thePlayer.rotationYaw, BlockUtils.getPitch(var0) + BlockUtils.mc.thePlayer.rotationPitch};
    }

    public static float[] getRotationNeededForBlock(EntityPlayer paramEntityPlayer, BlockPos pos) {
        double d1 = (double)pos.getX() - paramEntityPlayer.posX;
        double d2 = (double)pos.getX() + 0.5 - (paramEntityPlayer.posY + (double)paramEntityPlayer.getEyeHeight());
        double d3 = (double)pos.getZ() - paramEntityPlayer.posZ;
        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
        float f1 = (float)(Math.atan2(d3, d1) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI));
        return new float[]{f1, f2};
    }

    public static float getYaw(Entity var0) {
        double var1 = var0.posX - BlockUtils.mc.thePlayer.posX;
        double var3 = var0.posZ - BlockUtils.mc.thePlayer.posZ;
        double var5 = var3 < 0.0 && var1 < 0.0 ? 90.0 + Math.toDegrees(Math.atan(var3 / var1)) : (var3 < 0.0 && var1 > 0.0 ? -90.0 + Math.toDegrees(Math.atan(var3 / var1)) : Math.toDegrees(-Math.atan(var1 / var3)));
        return MathHelper.wrapAngleTo180_float(-(BlockUtils.mc.thePlayer.rotationYaw - (float)var5));
    }

    public static float getPitch(Entity var0) {
        double var1 = var0.posX - BlockUtils.mc.thePlayer.posX;
        double var3 = var0.posZ - BlockUtils.mc.thePlayer.posZ;
        double var5 = var0.posY - 1.6 + (double)var0.getEyeHeight() - BlockUtils.mc.thePlayer.posY;
        double var7 = MathHelper.sqrt_double(var1 * var1 + var3 * var3);
        double var9 = -Math.toDegrees(Math.atan(var5 / var7));
        return -MathHelper.wrapAngleTo180_float(BlockUtils.mc.thePlayer.rotationPitch - (float)var9);
    }

    public static int findBlock() {
        for (int i = 36; i < 45; ++i) {
            ItemBlock itemBlock;
            Block block;
            ItemStack itemStack = BlockUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || itemStack.stackSize <= 0 || !(block = (itemBlock = (ItemBlock)itemStack.getItem()).getBlock()).isFullCube() || BlackList.contains(block)) continue;
            return i;
        }
        return -1;
    }
}

