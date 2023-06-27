package net.minecraft.item;

import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.world.storage.*;
import net.minecraft.world.*;
import net.minecraft.stats.*;

public class ItemEmptyMap extends ItemMapBase
{
    protected ItemEmptyMap() {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }
    
    @Override
    public ItemStack onItemRightClick(final ItemStack itemStackIn, final World worldIn, final EntityPlayer playerIn) {
        final ItemStack itemstack = new ItemStack(Items.filled_map, 1, worldIn.getUniqueDataId("map"));
        final String s = "map_" + itemstack.getMetadata();
        final MapData mapdata = new MapData(s);
        worldIn.setItemData(s, mapdata);
        mapdata.scale = 0;
        mapdata.calculateMapCenter(playerIn.posX, playerIn.posZ, mapdata.scale);
        mapdata.dimension = (byte)worldIn.provider.getDimensionId();
        mapdata.markDirty();
        --itemStackIn.stackSize;
        if (itemStackIn.stackSize <= 0) {
            return itemstack;
        }
        if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
            playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
        }
        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        return itemStackIn;
    }
}
