package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSkull extends BlockContainer {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool NODROP = PropertyBool.create("nodrop");
    private static final Predicate<BlockWorldState> IS_WITHER_SKELETON = new Predicate<BlockWorldState>() {
        public boolean apply(final BlockWorldState p_apply_1_) {
            return p_apply_1_.getBlockState() != null && p_apply_1_.getBlockState().getBlock() == Blocks.skull && p_apply_1_.getTileEntity() instanceof TileEntitySkull && ((TileEntitySkull) p_apply_1_.getTileEntity()).getSkullType() == 1;
        }
    };
    private BlockPattern witherBasePattern;
    private BlockPattern witherPattern;

    protected BlockSkull() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, Boolean.valueOf(false)));
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName() {
        return StatCollector.translateToLocal("tile.skull.skeleton.name");
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        switch (worldIn.getBlockState(pos).getValue(FACING)) {
            case UP:
            default:
                this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;

            case NORTH:
                this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;

            case SOUTH:
                this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;

            case WEST:
                this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;

            case EAST:
                this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(NODROP, Boolean.valueOf(false));
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntitySkull();
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.skull;
    }

    public int getDamageValue(final World worldIn, final BlockPos pos) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntitySkull ? ((TileEntitySkull) tileentity).getSkullType() : super.getDamageValue(worldIn, pos);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
    }

    public void onBlockHarvested(final World worldIn, final BlockPos pos, IBlockState state, final EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            state = state.withProperty(NODROP, Boolean.valueOf(true));
            worldIn.setBlockState(pos, state, 4);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isRemote) {
            if (!state.getValue(NODROP).booleanValue()) {
                final TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity instanceof TileEntitySkull) {
                    final TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;
                    final ItemStack itemstack = new ItemStack(Items.skull, 1, this.getDamageValue(worldIn, pos));

                    if (tileentityskull.getSkullType() == 3 && tileentityskull.getPlayerProfile() != null) {
                        itemstack.setTagCompound(new NBTTagCompound());
                        final NBTTagCompound nbttagcompound = new NBTTagCompound();
                        NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
                        itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
                    }

                    spawnAsEntity(worldIn, pos, itemstack);
                }
            }

            super.breakBlock(worldIn, pos, state);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.skull;
    }

    public boolean canDispenserPlace(final World worldIn, final BlockPos pos, final ItemStack stack) {
        return stack.getMetadata() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote && this.getWitherBasePattern().match(worldIn, pos) != null;
    }

    public void checkWitherSpawn(final World worldIn, final BlockPos pos, final TileEntitySkull te) {
        if (te.getSkullType() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote) {
            final BlockPattern blockpattern = this.getWitherPattern();
            final BlockPattern.PatternHelper blockpattern$patternhelper = blockpattern.match(worldIn, pos);

            if (blockpattern$patternhelper != null) {
                for (int i = 0; i < 3; ++i) {
                    final BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, 0, 0);
                    worldIn.setBlockState(blockworldstate.getPos(), blockworldstate.getBlockState().withProperty(NODROP, Boolean.valueOf(true)), 2);
                }

                for (int j = 0; j < blockpattern.getPalmLength(); ++j) {
                    for (int k = 0; k < blockpattern.getThumbLength(); ++k) {
                        final BlockWorldState blockworldstate1 = blockpattern$patternhelper.translateOffset(j, k, 0);
                        worldIn.setBlockState(blockworldstate1.getPos(), Blocks.air.getDefaultState(), 2);
                    }
                }

                final BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 0, 0).getPos();
                final EntityWither entitywither = new EntityWither(worldIn);
                final BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
                entitywither.setLocationAndAngles((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.55D, (double) blockpos1.getZ() + 0.5D, blockpattern$patternhelper.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
                entitywither.renderYawOffset = blockpattern$patternhelper.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F;
                entitywither.func_82206_m();

                for (final EntityPlayer entityplayer : worldIn.getEntitiesWithinAABB(EntityPlayer.class, entitywither.getEntityBoundingBox().expand(50.0D, 50.0D, 50.0D))) {
                    entityplayer.triggerAchievement(AchievementList.spawnWither);
                }

                worldIn.spawnEntityInWorld(entitywither);

                for (int l = 0; l < 120; ++l) {
                    worldIn.spawnParticle(EnumParticleTypes.SNOWBALL, (double) blockpos.getX() + worldIn.rand.nextDouble(), (double) (blockpos.getY() - 2) + worldIn.rand.nextDouble() * 3.9D, (double) blockpos.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                for (int i1 = 0; i1 < blockpattern.getPalmLength(); ++i1) {
                    for (int j1 = 0; j1 < blockpattern.getThumbLength(); ++j1) {
                        final BlockWorldState blockworldstate2 = blockpattern$patternhelper.translateOffset(i1, j1, 0);
                        worldIn.notifyNeighborsRespectDebug(blockworldstate2.getPos(), Blocks.air);
                    }
                }
            }
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(NODROP, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(NODROP).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, NODROP);
    }

    protected BlockPattern getWitherBasePattern() {
        if (this.witherBasePattern == null) {
            this.witherBasePattern = FactoryBlockPattern.start().aisle("   ", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.witherBasePattern;
    }

    protected BlockPattern getWitherPattern() {
        if (this.witherPattern == null) {
            this.witherPattern = FactoryBlockPattern.start().aisle("^^^", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('^', IS_WITHER_SKELETON).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.witherPattern;
    }
}
