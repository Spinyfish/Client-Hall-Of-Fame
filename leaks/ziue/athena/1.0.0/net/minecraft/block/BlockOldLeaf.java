package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.stats.*;
import com.google.common.base.*;

public class BlockOldLeaf extends BlockLeaves
{
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT;
    
    public BlockOldLeaf() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty((IProperty<Comparable>)BlockOldLeaf.CHECK_DECAY, true).withProperty((IProperty<Comparable>)BlockOldLeaf.DECAYABLE, true));
    }
    
    @Override
    public int getRenderColor(final IBlockState state) {
        if (state.getBlock() != this) {
            return super.getRenderColor(state);
        }
        final BlockPlanks.EnumType blockplanks$enumtype = state.getValue(BlockOldLeaf.VARIANT);
        return (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE) ? ColorizerFoliage.getFoliageColorPine() : ((blockplanks$enumtype == BlockPlanks.EnumType.BIRCH) ? ColorizerFoliage.getFoliageColorBirch() : super.getRenderColor(state));
    }
    
    @Override
    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() == this) {
            final BlockPlanks.EnumType blockplanks$enumtype = iblockstate.getValue(BlockOldLeaf.VARIANT);
            if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE) {
                return ColorizerFoliage.getFoliageColorPine();
            }
            if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH) {
                return ColorizerFoliage.getFoliageColorBirch();
            }
        }
        return super.colorMultiplier(worldIn, pos, renderPass);
    }
    
    @Override
    protected void dropApple(final World worldIn, final BlockPos pos, final IBlockState state, final int chance) {
        if (state.getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.OAK && worldIn.rand.nextInt(chance) == 0) {
            Block.spawnAsEntity(worldIn, pos, new ItemStack(Items.apple, 1, 0));
        }
    }
    
    @Override
    protected int getSaplingDropChance(final IBlockState state) {
        return (state.getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.JUNGLE) ? 40 : super.getSaplingDropChance(state);
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.OAK.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.BIRCH.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()));
    }
    
    @Override
    protected ItemStack createStackedBlock(final IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(BlockOldLeaf.VARIANT).getMetadata());
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockOldLeaf.VARIANT, this.getWoodType(meta)).withProperty((IProperty<Comparable>)BlockOldLeaf.DECAYABLE, (meta & 0x4) == 0x0).withProperty((IProperty<Comparable>)BlockOldLeaf.CHECK_DECAY, (meta & 0x8) > 0);
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue(BlockOldLeaf.VARIANT).getMetadata();
        if (!state.getValue((IProperty<Boolean>)BlockOldLeaf.DECAYABLE)) {
            i |= 0x4;
        }
        if (state.getValue((IProperty<Boolean>)BlockOldLeaf.CHECK_DECAY)) {
            i |= 0x8;
        }
        return i;
    }
    
    @Override
    public BlockPlanks.EnumType getWoodType(final int meta) {
        return BlockPlanks.EnumType.byMetadata((meta & 0x3) % 4);
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockOldLeaf.VARIANT, BlockOldLeaf.CHECK_DECAY, BlockOldLeaf.DECAYABLE });
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockOldLeaf.VARIANT).getMetadata();
    }
    
    @Override
    public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, final TileEntity te) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            Block.spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(BlockOldLeaf.VARIANT).getMetadata()));
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, te);
        }
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, (com.google.common.base.Predicate<BlockPlanks.EnumType>)new Predicate<BlockPlanks.EnumType>() {
            public boolean apply(final BlockPlanks.EnumType p_apply_1_) {
                return p_apply_1_.getMetadata() < 4;
            }
        });
    }
}
