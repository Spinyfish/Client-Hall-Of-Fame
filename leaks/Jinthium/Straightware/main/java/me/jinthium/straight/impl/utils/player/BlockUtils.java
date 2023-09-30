package me.jinthium.straight.impl.utils.player;

import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.List;

public class BlockUtils implements MinecraftInstance {

    public static boolean isValidBlock(BlockPos pos) {
        return isValidBlock(mc.theWorld.getBlockState(pos).getBlock(), false);
    }

    public static final List<Block> INVALID_BLOCKS = Arrays.asList(
            Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.web,
            Blocks.torch, Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser,
            Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt,
            Blocks.standing_banner, Blocks.cactus, Blocks.wall_banner, Blocks.redstone_torch, Blocks.air, Blocks.beacon,
            Blocks.red_flower, Blocks.yellow_flower, Blocks.double_plant, Blocks.carpet, Blocks.tripwire_hook
    );



    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static Block getBlock(double x, double y, double z) {
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    public static boolean isValidBlock(Block block, boolean placing) {
        if (block instanceof BlockCarpet
                || block instanceof BlockSnow
                || block instanceof BlockContainer
                || block instanceof BlockBasePressurePlate
                || block.getMaterial().isLiquid()) {
            return false;
        }
        if (placing && (block instanceof BlockSlab
                || block instanceof BlockStairs
                || block instanceof BlockLadder
                || block instanceof BlockStainedGlassPane
                || block instanceof BlockWall
                || block instanceof BlockWeb
                || block instanceof BlockCactus
                || block instanceof BlockFalling
                || block == Blocks.glass_pane
                || block == Blocks.iron_bars)) {
            return false;
        }
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isFullBlock());
    }
}
