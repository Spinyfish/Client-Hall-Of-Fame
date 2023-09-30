package dev.africa.pandaware.utils.player.block;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class BlockUtils implements MinecraftInstance {
    public void placeBlock(BlockPos blockPos, EnumFacing face, Vec3 vec, ItemStack stack) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, blockPos, face, vec);
        }
    }

    public final List<Block> INVALID_BLOCKS = Arrays.asList(
            Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.web,
            Blocks.torch, Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser,
            Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt,
            Blocks.standing_banner, Blocks.cactus, Blocks.wall_banner, Blocks.redstone_torch, Blocks.air, Blocks.beacon,
            Blocks.red_flower, Blocks.yellow_flower, Blocks.double_plant, Blocks.carpet, Blocks.tripwire_hook
    );

    public static Block getBlockAtPos(BlockPos pos) {
        IBlockState blockState = getBlockStateAtPos(pos);
        if (blockState == null)
            return null;
        return blockState.getBlock();
    }

    public static IBlockState getBlockStateAtPos(BlockPos pos) {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null)
            return null;
        return Minecraft.getMinecraft().theWorld.getBlockState(pos);
    }
}
