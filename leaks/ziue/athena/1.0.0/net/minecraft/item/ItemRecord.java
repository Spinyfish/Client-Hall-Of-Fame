package net.minecraft.item;

import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.stats.*;
import net.minecraft.block.state.*;
import java.util.*;
import net.minecraft.util.*;
import com.google.common.collect.*;

public class ItemRecord extends Item
{
    private static final Map<String, ItemRecord> RECORDS;
    public final String recordName;
    
    protected ItemRecord(final String name) {
        this.recordName = name;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabMisc);
        ItemRecord.RECORDS.put("records." + name, this);
    }
    
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() != Blocks.jukebox || iblockstate.getValue((IProperty<Boolean>)BlockJukebox.HAS_RECORD)) {
            return false;
        }
        if (worldIn.isRemote) {
            return true;
        }
        ((BlockJukebox)Blocks.jukebox).insertRecord(worldIn, pos, iblockstate, stack);
        worldIn.playAuxSFXAtEntity(null, 1005, pos, Item.getIdFromItem(this));
        --stack.stackSize;
        playerIn.triggerAchievement(StatList.field_181740_X);
        return true;
    }
    
    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        tooltip.add(this.getRecordNameLocal());
    }
    
    public String getRecordNameLocal() {
        return StatCollector.translateToLocal("item.record." + this.recordName + ".desc");
    }
    
    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.RARE;
    }
    
    public static ItemRecord getRecord(final String name) {
        return ItemRecord.RECORDS.get(name);
    }
    
    static {
        RECORDS = Maps.newHashMap();
    }
}
