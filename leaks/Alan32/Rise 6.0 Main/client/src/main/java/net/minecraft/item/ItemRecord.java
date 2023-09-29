package net.minecraft.item;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ItemRecord extends Item {
    private static final Map<String, ItemRecord> RECORDS = Maps.newHashMap();

    /**
     * The name of the record.
     */
    public final String recordName;

    protected ItemRecord(final String name) {
        this.recordName = name;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabMisc);
        RECORDS.put("records." + name, this);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == Blocks.jukebox && !iblockstate.getValue(BlockJukebox.HAS_RECORD).booleanValue()) {
            if (worldIn.isRemote) {
                return true;
            } else {
                ((BlockJukebox) Blocks.jukebox).insertRecord(worldIn, pos, iblockstate, stack);
                worldIn.playAuxSFXAtEntity(null, 1005, pos, Item.getIdFromItem(this));
                --stack.stackSize;
                playerIn.triggerAchievement(StatList.field_181740_X);
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param tooltip  All lines to display in the Item's tooltip. This is a List of Strings.
     * @param advanced Whether the setting "Advanced tooltips" is enabled
     */
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        tooltip.add(this.getRecordNameLocal());
    }

    public String getRecordNameLocal() {
        return StatCollector.translateToLocal("item.record." + this.recordName + ".desc");
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(final ItemStack stack) {
        return EnumRarity.RARE;
    }

    /**
     * Return the record item corresponding to the given name.
     */
    public static ItemRecord getRecord(final String name) {
        return RECORDS.get(name);
    }
}
