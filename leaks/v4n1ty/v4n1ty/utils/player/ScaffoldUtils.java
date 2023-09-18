package v4n1ty.utils.player;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import v4n1ty.utils.player.MoveUtil;

public class ScaffoldUtils {

    public static class BlockCache {
        public BlockPos position;
        public EnumFacing facing;

        public BlockCache(final BlockPos position, final EnumFacing facing) {
            this.position = position;
            this.facing = facing;
        }

        public BlockPos getPosition() {
            return this.position;
        }

        private EnumFacing getFacing() {
            return this.facing;
        }

    }

    public static BlockCache grab() {
        final EnumFacing[] invert = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH,
                EnumFacing.EAST, EnumFacing.WEST};
        BlockPos position = new BlockPos(0, 0, 0);
        if (MoveUtil.isMoving() && !Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
            for (double n2 = 0 + 0.0001, n3 = 0.0; n3 <= n2; n3 += n2 / (Math.floor(n2))) {
                final BlockPos blockPos2 = new BlockPos(
                        Minecraft.getMinecraft().thePlayer.posX - MathHelper.sin(RotUtils.clampRotation()) * n3,
                        Minecraft.getMinecraft().thePlayer.posY - 1.0,
                        Minecraft.getMinecraft().thePlayer.posZ + MathHelper.cos(RotUtils.clampRotation()) * n3);
                final IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(blockPos2);
                if (blockState != null && blockState.getBlock() == Blocks.air) {
                    position = blockPos2;
                    break;
                }
            }
        } else {
            position = new BlockPos(new BlockPos(Minecraft.getMinecraft().thePlayer.getPositionVector().xCoord,
                    Minecraft.getMinecraft().thePlayer.getPositionVector().yCoord, Minecraft.getMinecraft().thePlayer.getPositionVector().zCoord))
                    .offset(EnumFacing.DOWN);
        }

        if (!(Minecraft.getMinecraft().theWorld.getBlockState(position).getBlock() instanceof BlockAir)
                && !(Minecraft.getMinecraft().theWorld.getBlockState(position).getBlock() instanceof BlockLiquid)) {
            return null;
        }
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing facing = values[i];
            final BlockPos offset = position.offset(facing);
            if (!(Minecraft.getMinecraft().theWorld.getBlockState(offset).getBlock() instanceof BlockAir)
                    && !(Minecraft.getMinecraft().theWorld.getBlockState(position).getBlock() instanceof BlockLiquid)) {
                return new BlockCache(offset, invert[facing.ordinal()]);
            }
        }
        final BlockPos[] offsets = {new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1),
                new BlockPos(0, 0, 1)};
        BlockPos[] array;
        for (int length2 = (array = offsets).length, j = 0; j < length2; ++j) {
            final BlockPos offset2 = array[j];
            final BlockPos offsetPos = position.add(offset2.getX(), 0, offset2.getZ());
            if (Minecraft.getMinecraft().theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
                EnumFacing[] values2;
                for (int length3 = (values2 = EnumFacing.values()).length, k = 0; k < length3; ++k) {
                    final EnumFacing facing2 = values2[k];
                    final BlockPos offset3 = offsetPos.offset(facing2);
                    if (!(Minecraft.getMinecraft().theWorld.getBlockState(offset3).getBlock() instanceof BlockAir)) {
                        return new BlockCache(offset3, invert[facing2.ordinal()]);
                    }
                }
            }

        }
        return null;
    }

    public static int grabBlockSlot() {
        int slot = -1;
        int highestStack = -1;
        boolean didGetHotbar = false;
        for (int i = 0; i < 9; ++i) {
            final ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && canItemBePlaced(itemStack) && itemStack.stackSize > 0) {
                if (Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i].stackSize > highestStack && i < 9) {
                    highestStack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i].stackSize;
                    slot = i;
                    if (slot == getLastHotbarSlot()) {
                        didGetHotbar = true;
                    }
                }
                if (i > 8 && !didGetHotbar) {
                    int hotbarNum = getFreeHotbarSlot();
                    if (hotbarNum != -1 && Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i].stackSize > highestStack) {
                        highestStack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i].stackSize;
                        slot = i;
                    }
                }
            }
        }
        if (slot > 8) {
            int hotbarNum = getFreeHotbarSlot();
            if (hotbarNum != -1) {
                Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, Minecraft.getMinecraft().thePlayer);
            } else {
                return -1;
            }
        }
        return slot;
    }

    public static int getLastHotbarSlot() {
        int hotbarNum = -1;
        for (int k = 0; k < 9; k++) {
            final ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[k];
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && canItemBePlaced(itemStack) && itemStack.stackSize > 1) {
                hotbarNum = k;
                continue;
            }
        }
        return hotbarNum;
    }

    public static int getFreeHotbarSlot() {
        int hotbarNum = -1;
        for (int k = 0; k < 9; k++) {
            if (Minecraft.getMinecraft().thePlayer.inventory.mainInventory[k] == null) {
                hotbarNum = k;
                continue;
            } else {
                hotbarNum = 7;
            }
        }
        return hotbarNum;
    }


    public static boolean canIItemBePlaced(Item item) {
        if (Item.getIdFromItem(item) == 116)
            return false;
        if (Item.getIdFromItem(item) == 30)
            return false;
        if (Item.getIdFromItem(item) == 31)
            return false;
        if (Item.getIdFromItem(item) == 175)
            return false;
        if (Item.getIdFromItem(item) == 28)
            return false;
        if (Item.getIdFromItem(item) == 27)
            return false;
        if (Item.getIdFromItem(item) == 66)
            return false;
        if (Item.getIdFromItem(item) == 157)
            return false;
        if (Item.getIdFromItem(item) == 31)
            return false;
        if (Item.getIdFromItem(item) == 6)
            return false;
        if (Item.getIdFromItem(item) == 31)
            return false;
        if (Item.getIdFromItem(item) == 32)
            return false;
        if (Item.getIdFromItem(item) == 140)
            return false;
        if (Item.getIdFromItem(item) == 390)
            return false;
        if (Item.getIdFromItem(item) == 37)
            return false;
        if (Item.getIdFromItem(item) == 38)
            return false;
        if (Item.getIdFromItem(item) == 39)
            return false;
        if (Item.getIdFromItem(item) == 40)
            return false;
        if (Item.getIdFromItem(item) == 69)
            return false;
        if (Item.getIdFromItem(item) == 50)
            return false;
        if (Item.getIdFromItem(item) == 75)
            return false;
        if (Item.getIdFromItem(item) == 76)
            return false;
        if (Item.getIdFromItem(item) == 54)
            return false;
        if (Item.getIdFromItem(item) == 130)
            return false;
        if (Item.getIdFromItem(item) == 146)
            return false;
        if (Item.getIdFromItem(item) == 342)
            return false;
        if (Item.getIdFromItem(item) == 12)
            return false;
        if (Item.getIdFromItem(item) == 77)
            return false;
        if (Item.getIdFromItem(item) == 143)
            return false;
        if (Item.getIdFromItem(item) == 46)
            return false;
        return true;
    }

    public static Vec3 getHypixelVec3(BlockCache data) {
        BlockPos pos = data.position;
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

    public static boolean canItemBePlaced(ItemStack item) {
        if (Item.getIdFromItem(item.getItem()) == 116)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 30)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 31)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 175)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 28)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 27)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 66)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 157)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 31)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 6)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 31)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 32)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 140)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 390)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 37)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 38)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 39)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 40)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 69)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 50)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 75)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 76)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 54)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 130)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 146)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 342)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 12)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 77)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 143)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 46)
            return false;
        if (Item.getIdFromItem(item.getItem()) == 145)
            return false;

        return true;
    }

}
