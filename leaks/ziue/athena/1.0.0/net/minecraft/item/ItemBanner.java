package net.minecraft.item;

import net.minecraft.init.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.nbt.*;

public class ItemBanner extends ItemBlock
{
    public ItemBanner() {
        super(Blocks.standing_banner);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }
    
    @Override
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (side == EnumFacing.DOWN) {
            return false;
        }
        if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
            return false;
        }
        pos = pos.offset(side);
        if (!playerIn.canPlayerEdit(pos, side, stack)) {
            return false;
        }
        if (!Blocks.standing_banner.canPlaceBlockAt(worldIn, pos)) {
            return false;
        }
        if (worldIn.isRemote) {
            return true;
        }
        if (side == EnumFacing.UP) {
            final int i = MathHelper.floor_double((playerIn.rotationYaw + 180.0f) * 16.0f / 360.0f + 0.5) & 0xF;
            worldIn.setBlockState(pos, Blocks.standing_banner.getDefaultState().withProperty((IProperty<Comparable>)BlockStandingSign.ROTATION, i), 3);
        }
        else {
            worldIn.setBlockState(pos, Blocks.wall_banner.getDefaultState().withProperty((IProperty<Comparable>)BlockWallSign.FACING, side), 3);
        }
        --stack.stackSize;
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityBanner) {
            ((TileEntityBanner)tileentity).setItemValues(stack);
        }
        return true;
    }
    
    @Override
    public String getItemStackDisplayName(final ItemStack stack) {
        String s = "item.banner.";
        final EnumDyeColor enumdyecolor = this.getBaseColor(stack);
        s = s + enumdyecolor.getUnlocalizedName() + ".name";
        return StatCollector.translateToLocal(s);
    }
    
    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        final NBTTagCompound nbttagcompound = stack.getSubCompound("BlockEntityTag", false);
        if (nbttagcompound != null && nbttagcompound.hasKey("Patterns")) {
            final NBTTagList nbttaglist = nbttagcompound.getTagList("Patterns", 10);
            for (int i = 0; i < nbttaglist.tagCount() && i < 6; ++i) {
                final NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(i);
                final EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(nbttagcompound2.getInteger("Color"));
                final TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern = TileEntityBanner.EnumBannerPattern.getPatternByID(nbttagcompound2.getString("Pattern"));
                if (tileentitybanner$enumbannerpattern != null) {
                    tooltip.add(StatCollector.translateToLocal("item.banner." + tileentitybanner$enumbannerpattern.getPatternName() + "." + enumdyecolor.getUnlocalizedName()));
                }
            }
        }
    }
    
    @Override
    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        if (renderPass == 0) {
            return 16777215;
        }
        final EnumDyeColor enumdyecolor = this.getBaseColor(stack);
        return enumdyecolor.getMapColor().colorValue;
    }
    
    @Override
    public void getSubItems(final Item itemIn, final CreativeTabs tab, final List<ItemStack> subItems) {
        for (final EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            TileEntityBanner.func_181020_a(nbttagcompound, enumdyecolor.getDyeDamage(), null);
            final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.setTag("BlockEntityTag", nbttagcompound);
            final ItemStack itemstack = new ItemStack(itemIn, 1, enumdyecolor.getDyeDamage());
            itemstack.setTagCompound(nbttagcompound2);
            subItems.add(itemstack);
        }
    }
    
    @Override
    public CreativeTabs getCreativeTab() {
        return CreativeTabs.tabDecorations;
    }
    
    private EnumDyeColor getBaseColor(final ItemStack stack) {
        final NBTTagCompound nbttagcompound = stack.getSubCompound("BlockEntityTag", false);
        EnumDyeColor enumdyecolor = null;
        if (nbttagcompound != null && nbttagcompound.hasKey("Base")) {
            enumdyecolor = EnumDyeColor.byDyeDamage(nbttagcompound.getInteger("Base"));
        }
        else {
            enumdyecolor = EnumDyeColor.byDyeDamage(stack.getMetadata());
        }
        return enumdyecolor;
    }
}
