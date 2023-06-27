package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.block.state.*;

public abstract class BlockWoodSlab extends BlockSlab
{
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT;
    
    public BlockWoodSlab() {
        super(Material.wood);
        IBlockState iblockstate = this.blockState.getBaseState();
        if (!this.isDouble()) {
            iblockstate = iblockstate.withProperty(BlockWoodSlab.HALF, EnumBlockHalf.BOTTOM);
        }
        this.setDefaultState(iblockstate.withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return state.getValue(BlockWoodSlab.VARIANT).func_181070_c();
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(Blocks.wooden_slab);
    }
    
    @Override
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Item.getItemFromBlock(Blocks.wooden_slab);
    }
    
    @Override
    public String getUnlocalizedName(final int meta) {
        return super.getUnlocalizedName() + "." + BlockPlanks.EnumType.byMetadata(meta).getUnlocalizedName();
    }
    
    @Override
    public IProperty<?> getVariantProperty() {
        return BlockWoodSlab.VARIANT;
    }
    
    @Override
    public Object getVariant(final ItemStack stack) {
        return BlockPlanks.EnumType.byMetadata(stack.getMetadata() & 0x7);
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        if (itemIn != Item.getItemFromBlock(Blocks.double_wooden_slab)) {
            for (final BlockPlanks.EnumType blockplanks$enumtype : BlockPlanks.EnumType.values()) {
                list.add(new ItemStack(itemIn, 1, blockplanks$enumtype.getMetadata()));
            }
        }
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.byMetadata(meta & 0x7));
        if (!this.isDouble()) {
            iblockstate = iblockstate.withProperty(BlockWoodSlab.HALF, ((meta & 0x8) == 0x0) ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }
        return iblockstate;
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue(BlockWoodSlab.VARIANT).getMetadata();
        if (!this.isDouble() && state.getValue(BlockWoodSlab.HALF) == EnumBlockHalf.TOP) {
            i |= 0x8;
        }
        return i;
    }
    
    @Override
    protected BlockState createBlockState() {
        return this.isDouble() ? new BlockState(this, new IProperty[] { BlockWoodSlab.VARIANT }) : new BlockState(this, new IProperty[] { BlockWoodSlab.HALF, BlockWoodSlab.VARIANT });
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockWoodSlab.VARIANT).getMetadata();
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);
    }
}
