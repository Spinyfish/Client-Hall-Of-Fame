package net.minecraft.item;

import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;

public class ItemHangingEntity extends Item
{
    private final Class<? extends EntityHanging> hangingEntityClass;
    
    public ItemHangingEntity(final Class<? extends EntityHanging> entityClass) {
        this.hangingEntityClass = entityClass;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }
    
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (side == EnumFacing.DOWN) {
            return false;
        }
        if (side == EnumFacing.UP) {
            return false;
        }
        final BlockPos blockpos = pos.offset(side);
        if (!playerIn.canPlayerEdit(blockpos, side, stack)) {
            return false;
        }
        final EntityHanging entityhanging = this.createEntity(worldIn, blockpos, side);
        if (entityhanging != null && entityhanging.onValidSurface()) {
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(entityhanging);
            }
            --stack.stackSize;
        }
        return true;
    }
    
    private EntityHanging createEntity(final World worldIn, final BlockPos pos, final EnumFacing clickedSide) {
        return (this.hangingEntityClass == EntityPainting.class) ? new EntityPainting(worldIn, pos, clickedSide) : ((this.hangingEntityClass == EntityItemFrame.class) ? new EntityItemFrame(worldIn, pos, clickedSide) : null);
    }
}
