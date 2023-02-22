package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
    private final Class<? extends EntityHanging> hangingEntityClass;

    public ItemHangingEntity(final Class<? extends EntityHanging> entityClass) {
        this.hangingEntityClass = entityClass;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (side == EnumFacing.DOWN) {
            return false;
        } else if (side == EnumFacing.UP) {
            return false;
        } else {
            final BlockPos blockpos = pos.offset(side);

            if (!playerIn.canPlayerEdit(blockpos, side, stack)) {
                return false;
            } else {
                final EntityHanging entityhanging = this.createEntity(worldIn, blockpos, side);

                if (entityhanging != null && entityhanging.onValidSurface()) {
                    if (!worldIn.isRemote) {
                        worldIn.spawnEntityInWorld(entityhanging);
                    }

                    --stack.stackSize;
                }

                return true;
            }
        }
    }

    private EntityHanging createEntity(final World worldIn, final BlockPos pos, final EnumFacing clickedSide) {
        return this.hangingEntityClass == EntityPainting.class ? new EntityPainting(worldIn, pos, clickedSide) : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(worldIn, pos, clickedSide) : null);
    }
}
