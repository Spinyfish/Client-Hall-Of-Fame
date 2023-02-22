package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSeedFood extends ItemFood {
    private final Block crops;

    /**
     * Block ID of the soil this seed food should be planted on.
     */
    private final Block soilId;

    public ItemSeedFood(final int healAmount, final float saturation, final Block crops, final Block soil) {
        super(healAmount, saturation, false);
        this.crops = crops;
        this.soilId = soil;
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (side != EnumFacing.UP) {
            return false;
        } else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
            return false;
        } else if (worldIn.getBlockState(pos).getBlock() == this.soilId && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), this.crops.getDefaultState());
            --stack.stackSize;
            return true;
        } else {
            return false;
        }
    }
}
