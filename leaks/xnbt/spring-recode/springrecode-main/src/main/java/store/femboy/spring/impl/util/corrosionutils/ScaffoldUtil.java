package store.femboy.spring.impl.util.corrosionutils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import store.femboy.spring.impl.util.Rotation;

import java.util.Arrays;
import java.util.List;

public class ScaffoldUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final List<Block> INVALID_PLACING_BLOCKS = Arrays.asList(
            Blocks.air,
            Blocks.water,
            Blocks.chest,
            Blocks.flowing_water,
            Blocks.lava,
            Blocks.flowing_lava,
            Blocks.enchanting_table,
            Blocks.carpet,
            Blocks.snow_layer,
            Blocks.torch,
            Blocks.anvil,
            Blocks.trapped_chest,
            Blocks.stone_button,
            Blocks.wooden_button,
            Blocks.lever,
            Blocks.red_flower,
            Blocks.double_plant,
            Blocks.yellow_flower,
            Blocks.bed,
            Blocks.stone_slab,
            Blocks.wooden_slab,
            Blocks.heavy_weighted_pressure_plate,
            Blocks.light_weighted_pressure_plate,
            Blocks.stone_pressure_plate,
            Blocks.wooden_pressure_plate,
            Blocks.stone_slab2,
            Blocks.tripwire,
            Blocks.tripwire_hook,
            Blocks.tallgrass,
            Blocks.dispenser,
            Blocks.command_block);

    private static final List<Block> INVALID_BLOCKS = Arrays.asList(
            Blocks.air,
            Blocks.water,
            Blocks.tnt,
            Blocks.flowing_water,
            Blocks.lava,
            Blocks.flowing_lava,
            Blocks.enchanting_table,
            Blocks.carpet,
            Blocks.glass_pane,
            Blocks.stained_glass_pane,
            Blocks.iron_bars,
            Blocks.snow_layer,
            Blocks.chest,
            Blocks.torch,
            Blocks.anvil,
            Blocks.trapped_chest,
            Blocks.noteblock,
            Blocks.jukebox,
            Blocks.sand,
            Blocks.stone_button,
            Blocks.wooden_button,
            Blocks.lever,
            Blocks.red_flower,
            Blocks.double_plant,
            Blocks.yellow_flower,
            Blocks.bed,
            Blocks.ladder,
            Blocks.waterlily,
            Blocks.heavy_weighted_pressure_plate,
            Blocks.light_weighted_pressure_plate,
            Blocks.stone_pressure_plate,
            Blocks.wooden_pressure_plate,
            Blocks.tripwire,
            Blocks.tripwire_hook,
            Blocks.tallgrass,
            Blocks.dispenser,
            Blocks.command_block,
            Blocks.web);

    public static boolean isStackValid(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) {
            return false;
        }

        return isBlockValid(((ItemBlock) itemStack.getItem()).getBlock());
    }

    public static int getSlot() {
        for (int i = 36; i < 45; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock && stack.stackSize > 0 && isBlockValid(((ItemBlock) stack.getItem()).getBlock()))
                return i - 36;
        }
        return -1;
    }

    public static boolean isBlockValid(Block block) {
        return !INVALID_BLOCKS.contains(block);
    }

    public void getBlocks() {
        if (invCheck()) {
            for (int i = 9; i < 36; i++) {
                if (!ScaffoldUtil.isStackValid(mc.thePlayer.inventoryContainer.getSlot(i).getStack()))
                    continue;

                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 2, mc.thePlayer);
                break;
            }
        }
    }

    public Material getMaterial(BlockPos pos) {
        return getBlock(pos).getMaterial();
    }

    public boolean isReplacable(BlockPos blockPos) {
        return getMaterial(blockPos).isReplaceable();
    }

    public boolean canBeClicked(BlockPos blockPos) {
        return getBlock(blockPos).canCollideCheck(blockState(blockPos), false) && mc.theWorld.getWorldBorder().contains(blockPos);
    }

    public Block getBlock(BlockPos pos) {
        return blockState(pos).getBlock();
    }

    public IBlockState blockState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    public static boolean invCheck() {
        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !isStackValid(mc.thePlayer.inventoryContainer.getSlot(i).getStack()))
                continue;
            return false;
        }
        return true;
    }

    public Vec3 getVectorForRotation(Rotation rotation) {
        float yawCos = (float)Math.cos(-rotation.getRotationYaw() * 0.017453292F - 3.1415927F);
        float yawSin = (float)Math.sin(-rotation.getRotationYaw() * 0.017453292F - 3.1415927F);

        float pitchCos = (float)(-Math.cos(-rotation.getRotationPitch() * 0.017453292F));
        float pitchSin = (float)Math.sin(-rotation.getRotationPitch() * 0.017453292F);

        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
}
