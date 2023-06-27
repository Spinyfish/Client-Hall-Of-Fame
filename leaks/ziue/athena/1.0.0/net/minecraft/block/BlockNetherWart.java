package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.state.*;

public class BlockNetherWart extends BlockBush
{
    public static final PropertyInteger AGE;
    
    protected BlockNetherWart() {
        super(Material.plants, MapColor.redColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockNetherWart.AGE, 0));
        this.setTickRandomly(true);
        final float f = 0.5f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.25f, 0.5f + f);
        this.setCreativeTab(null);
    }
    
    @Override
    protected boolean canPlaceBlockOn(final Block ground) {
        return ground == Blocks.soul_sand;
    }
    
    @Override
    public boolean canBlockStay(final World worldIn, final BlockPos pos, final IBlockState state) {
        return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }
    
    @Override
    public void updateTick(final World worldIn, final BlockPos pos, IBlockState state, final Random rand) {
        final int i = state.getValue((IProperty<Integer>)BlockNetherWart.AGE);
        if (i < 3 && rand.nextInt(10) == 0) {
            state = state.withProperty((IProperty<Comparable>)BlockNetherWart.AGE, i + 1);
            worldIn.setBlockState(pos, state, 2);
        }
        super.updateTick(worldIn, pos, state, rand);
    }
    
    @Override
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        if (!worldIn.isRemote) {
            int i = 1;
            if (state.getValue((IProperty<Integer>)BlockNetherWart.AGE) >= 3) {
                i = 2 + worldIn.rand.nextInt(3);
                if (fortune > 0) {
                    i += worldIn.rand.nextInt(fortune + 1);
                }
            }
            for (int j = 0; j < i; ++j) {
                Block.spawnAsEntity(worldIn, pos, new ItemStack(Items.nether_wart));
            }
        }
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return null;
    }
    
    @Override
    public int quantityDropped(final Random random) {
        return 0;
    }
    
    @Override
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.nether_wart;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockNetherWart.AGE, meta);
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue((IProperty<Integer>)BlockNetherWart.AGE);
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockNetherWart.AGE });
    }
    
    static {
        AGE = PropertyInteger.create("age", 0, 3);
    }
}
