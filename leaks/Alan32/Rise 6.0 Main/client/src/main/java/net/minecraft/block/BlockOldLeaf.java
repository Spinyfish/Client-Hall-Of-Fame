package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockOldLeaf extends BlockLeaves {
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
        public boolean apply(final BlockPlanks.EnumType p_apply_1_) {
            return p_apply_1_.getMetadata() < 4;
        }
    });

    public BlockOldLeaf() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK).withProperty(CHECK_DECAY, Boolean.valueOf(true)).withProperty(DECAYABLE, Boolean.valueOf(true)));
    }

    public int getRenderColor(final IBlockState state) {
        if (state.getBlock() != this) {
            return super.getRenderColor(state);
        } else {
            final BlockPlanks.EnumType blockplanks$enumtype = state.getValue(VARIANT);
            return blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE ? ColorizerFoliage.getFoliageColorPine() : (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH ? ColorizerFoliage.getFoliageColorBirch() : super.getRenderColor(state));
        }
    }

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this) {
            final BlockPlanks.EnumType blockplanks$enumtype = iblockstate.getValue(VARIANT);

            if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE) {
                return ColorizerFoliage.getFoliageColorPine();
            }

            if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH) {
                return ColorizerFoliage.getFoliageColorBirch();
            }
        }

        return super.colorMultiplier(worldIn, pos, renderPass);
    }

    protected void dropApple(final World worldIn, final BlockPos pos, final IBlockState state, final int chance) {
        if (state.getValue(VARIANT) == BlockPlanks.EnumType.OAK && worldIn.rand.nextInt(chance) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(Items.apple, 1, 0));
        }
    }

    protected int getSaplingDropChance(final IBlockState state) {
        return state.getValue(VARIANT) == BlockPlanks.EnumType.JUNGLE ? 40 : super.getSaplingDropChance(state);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.OAK.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.BIRCH.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()));
    }

    protected ItemStack createStackedBlock(final IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(VARIANT, this.getWoodType(meta)).withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(VARIANT).getMetadata();

        if (!state.getValue(DECAYABLE).booleanValue()) {
            i |= 4;
        }

        if (state.getValue(CHECK_DECAY).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public BlockPlanks.EnumType getWoodType(final int meta) {
        return BlockPlanks.EnumType.byMetadata((meta & 3) % 4);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, VARIANT, CHECK_DECAY, DECAYABLE);
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(final IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, final TileEntity te) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata()));
        } else {
            super.harvestBlock(worldIn, player, pos, state, te);
        }
    }
}
