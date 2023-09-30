package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoor extends Item {
    private final Block block;

    public ItemDoor(final Block block) {
        this.block = block;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (side != EnumFacing.UP) {
            return false;
        } else {
            final IBlockState iblockstate = worldIn.getBlockState(pos);
            final Block block = iblockstate.getBlock();

            if (!block.isReplaceable(worldIn, pos)) {
                pos = pos.offset(side);
            }

            if (!playerIn.canPlayerEdit(pos, side, stack)) {
                return false;
            } else if (!this.block.canPlaceBlockAt(worldIn, pos)) {
                return false;
            } else {
                placeDoor(worldIn, pos, EnumFacing.fromAngle(playerIn.rotationYaw), this.block);
                --stack.stackSize;
                return true;
            }
        }
    }

    public static void placeDoor(final World worldIn, final BlockPos pos, final EnumFacing facing, final Block door) {
        final BlockPos blockpos = pos.offset(facing.rotateY());
        final BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
        final int i = (worldIn.getBlockState(blockpos1).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).getBlock().isNormalCube() ? 1 : 0);
        final int j = (worldIn.getBlockState(blockpos).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos.up()).getBlock().isNormalCube() ? 1 : 0);
        final boolean flag = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
        final boolean flag1 = worldIn.getBlockState(blockpos).getBlock() == door || worldIn.getBlockState(blockpos.up()).getBlock() == door;
        boolean flag2 = flag && !flag1 || j > i;

        final BlockPos blockpos2 = pos.up();
        final IBlockState iblockstate = door.getDefaultState().withProperty(BlockDoor.FACING, facing).withProperty(BlockDoor.HINGE, flag2 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT);
        worldIn.setBlockState(pos, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
        worldIn.setBlockState(blockpos2, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
        worldIn.notifyNeighborsOfStateChange(pos, door);
        worldIn.notifyNeighborsOfStateChange(blockpos2, door);
    }
}
