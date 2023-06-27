package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.stats.*;
import com.google.common.base.*;

public class BlockNewLeaf extends BlockLeaves
{
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT;
    
    public BlockNewLeaf() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty((IProperty<Comparable>)BlockNewLeaf.CHECK_DECAY, true).withProperty((IProperty<Comparable>)BlockNewLeaf.DECAYABLE, true));
    }
    
    @Override
    protected void dropApple(final World worldIn, final BlockPos pos, final IBlockState state, final int chance) {
        if (state.getValue(BlockNewLeaf.VARIANT) == BlockPlanks.EnumType.DARK_OAK && worldIn.rand.nextInt(chance) == 0) {
            Block.spawnAsEntity(worldIn, pos, new ItemStack(Items.apple, 1, 0));
        }
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockNewLeaf.VARIANT).getMetadata();
    }
    
    @Override
    public int getDamageValue(final World worldIn, final BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock().getMetaFromState(iblockstate) & 0x3;
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }
    
    @Override
    protected ItemStack createStackedBlock(final IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(BlockNewLeaf.VARIANT).getMetadata() - 4);
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockNewLeaf.VARIANT, this.getWoodType(meta)).withProperty((IProperty<Comparable>)BlockNewLeaf.DECAYABLE, (meta & 0x4) == 0x0).withProperty((IProperty<Comparable>)BlockNewLeaf.CHECK_DECAY, (meta & 0x8) > 0);
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue(BlockNewLeaf.VARIANT).getMetadata() - 4;
        if (!state.getValue((IProperty<Boolean>)BlockNewLeaf.DECAYABLE)) {
            i |= 0x4;
        }
        if (state.getValue((IProperty<Boolean>)BlockNewLeaf.CHECK_DECAY)) {
            i |= 0x8;
        }
        return i;
    }
    
    @Override
    public BlockPlanks.EnumType getWoodType(final int meta) {
        return BlockPlanks.EnumType.byMetadata((meta & 0x3) + 4);
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockNewLeaf.VARIANT, BlockNewLeaf.CHECK_DECAY, BlockNewLeaf.DECAYABLE });
    }
    
    @Override
    public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, final TileEntity te) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            Block.spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(BlockNewLeaf.VARIANT).getMetadata() - 4));
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, te);
        }
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, (com.google.common.base.Predicate<BlockPlanks.EnumType>)new Predicate<BlockPlanks.EnumType>() {
            public boolean apply(final BlockPlanks.EnumType p_apply_1_) {
                return p_apply_1_.getMetadata() >= 4;
            }
        });
    }
}
