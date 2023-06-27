package net.minecraft.item;

import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import com.google.common.collect.*;
import java.util.*;
import net.minecraft.nbt.*;

public class ItemFirework extends Item
{
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!worldIn.isRemote) {
            final EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
            worldIn.spawnEntityInWorld(entityfireworkrocket);
            if (!playerIn.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        if (stack.hasTagCompound()) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Fireworks");
            if (nbttagcompound != null) {
                if (nbttagcompound.hasKey("Flight", 99)) {
                    tooltip.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + nbttagcompound.getByte("Flight"));
                }
                final NBTTagList nbttaglist = nbttagcompound.getTagList("Explosions", 10);
                if (nbttaglist != null && nbttaglist.tagCount() > 0) {
                    for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                        final NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(i);
                        final List<String> list = (List<String>)Lists.newArrayList();
                        ItemFireworkCharge.addExplosionInfo(nbttagcompound2, list);
                        if (list.size() > 0) {
                            for (int j = 1; j < list.size(); ++j) {
                                list.set(j, "  " + list.get(j));
                            }
                            tooltip.addAll(list);
                        }
                    }
                }
            }
        }
    }
}
