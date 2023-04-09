package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

import java.util.List;

public class BlockStainedGlassPane extends BlockPane {
    public static final PropertyEnum field_176245_a = PropertyEnum.create("color", EnumDyeColor.class);
    // private static final String __OBFID = "CL_00000313";

    public BlockStainedGlassPane() {
        super(Material.glass, false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(field_176245_a, EnumDyeColor.WHITE));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Get the damage value that this Block should drop
     */
    public int damageDropped(IBlockState state) {
        return ((EnumDyeColor) state.getValue(field_176245_a)).func_176765_a();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (int var4 = 0; var4 < EnumDyeColor.values().length; ++var4) {
            list.add(new ItemStack(itemIn, 1, var4));
        }
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(field_176245_a, EnumDyeColor.func_176764_b(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return ((EnumDyeColor) state.getValue(field_176245_a)).func_176765_a();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, NORTH, EAST, WEST, SOUTH, field_176245_a);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            BlockBeacon.func_176450_d(worldIn, pos);
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            BlockBeacon.func_176450_d(worldIn, pos);
        }
    }
}
