package net.minecraft.entity.item;

import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.block.properties.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

public class EntityMinecartChest extends EntityMinecartContainer
{
    public EntityMinecartChest(final World worldIn) {
        super(worldIn);
    }
    
    public EntityMinecartChest(final World worldIn, final double p_i1715_2_, final double p_i1715_4_, final double p_i1715_6_) {
        super(worldIn, p_i1715_2_, p_i1715_4_, p_i1715_6_);
    }
    
    @Override
    public void killMinecart(final DamageSource p_94095_1_) {
        super.killMinecart(p_94095_1_);
        if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.chest), 1, 0.0f);
        }
    }
    
    @Override
    public int getSizeInventory() {
        return 27;
    }
    
    @Override
    public EnumMinecartType getMinecartType() {
        return EnumMinecartType.CHEST;
    }
    
    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.chest.getDefaultState().withProperty((IProperty<Comparable>)BlockChest.FACING, EnumFacing.NORTH);
    }
    
    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }
    
    @Override
    public String getGuiID() {
        return "minecraft:chest";
    }
    
    @Override
    public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
        return new ContainerChest(playerInventory, this, playerIn);
    }
}
