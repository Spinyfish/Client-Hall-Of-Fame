package dev.tenacity.utils.player;

import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.impl.movement.Speed;
import dev.tenacity.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class ScaffoldUtils implements Utils {

    public static class BlockCache {

        private final BlockPosition position;
        private final EnumFacing facing;

        public BlockCache(final BlockPosition position, final EnumFacing facing) {
            this.position = position;
            this.facing = facing;
        }

        public BlockPosition getPosition() {
            return this.position;
        }

        public EnumFacing getFacing() {
            return this.facing;
        }
    }

    public static double getYLevel() {
        if (!Scaffold.keepY.isEnabled() || Scaffold.keepYMode.is("Speed toggled") && !Tenacity.INSTANCE.isEnabled(Speed.class)) {
            return mc.thePlayer.posY - 1.0;
        }
        return mc.thePlayer.posY - 1.0 >= Scaffold.keepYCoord && Math.max(mc.thePlayer.posY, Scaffold.keepYCoord)
                - Math.min(mc.thePlayer.posY, Scaffold.keepYCoord) <= 3.0 && !mc.gameSettings.keyBindJump.isKeyDown()
                ? Scaffold.keepYCoord
                : mc.thePlayer.posY - 1.0;
    }

    public static BlockCache getBlockInfo() {
        final BlockPosition belowBlockPosition = new BlockPosition(mc.thePlayer.posX, getYLevel() - (Scaffold.isDownwards() ? 1 : 0), mc.thePlayer.posZ);
        if (mc.theWorld.getBlockState(belowBlockPosition).getBlock() instanceof BlockAir) {
            for (int x = 0; x < 4; x++) {
                for (int z = 0; z < 4; z++) {
                    for (int i = 1; i > -3; i -= 2) {
                        final BlockPosition blockPosition = belowBlockPosition.add(x * i, 0, z * i);
                        if (mc.theWorld.getBlockState(blockPosition).getBlock() instanceof BlockAir) {
                            for (EnumFacing direction : EnumFacing.values()) {
                                final BlockPosition block = blockPosition.offset(direction);
                                final Material material = mc.theWorld.getBlockState(block).getBlock().getMaterial();
                                if (material.isSolid() && !material.isLiquid()) {
                                    return new BlockCache(block, direction.getOpposite());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static int getBlockSlot() {
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
                final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                if (isBlockValid(itemBlock.getBlock())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getBlockCount() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
                final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                if (isBlockValid(itemBlock.getBlock())) {
                    count += itemStack.stackSize;
                }
            }
        }
        return count;
    }

    private static boolean isBlockValid(final Block block) {
        return (block.isFullBlock() || block == Blocks.glass) &&
                block != Blocks.sand &&
                block != Blocks.gravel &&
                block != Blocks.dispenser &&
                block != Blocks.command_block &&
                block != Blocks.noteblock &&
                block != Blocks.furnace &&
                block != Blocks.crafting_table &&
                block != Blocks.tnt &&
                block != Blocks.dropper &&
                block != Blocks.beacon;
    }

    public static Vec3 getHypixelVec3(BlockCache data) {
        BlockPosition pos = data.position;
        EnumFacing face = data.facing;

        double x = (double) pos.getX() + 0.5, y = (double) pos.getY() + 0.5, z = (double) pos.getZ() + 0.5;

        if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
            y += 0.5;
        } else {
            x += 0.3;
            z += 0.3;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += 0.15;
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += 0.15;
        }

        return new Vec3(x, y, z);
    }

}
