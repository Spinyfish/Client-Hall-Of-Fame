package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockVine extends Block {
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool[] ALL_FACES = new PropertyBool[]{UP, NORTH, SOUTH, WEST, EAST};

    public BlockVine() {
        super(Material.vine);
        this.setDefaultState(this.blockState.getBaseState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return state.withProperty(UP, Boolean.valueOf(worldIn.getBlockState(pos.up()).getBlock().isBlockNormalCube()));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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

    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(final World worldIn, final BlockPos pos) {
        return true;
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        final float f = 0.0625F;
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag = false;

        if (worldIn.getBlockState(pos).getValue(WEST).booleanValue()) {
            f4 = Math.max(f4, 0.0625F);
            f1 = 0.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if (worldIn.getBlockState(pos).getValue(EAST).booleanValue()) {
            f1 = Math.min(f1, 0.9375F);
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if (worldIn.getBlockState(pos).getValue(NORTH).booleanValue()) {
            f6 = Math.max(f6, 0.0625F);
            f3 = 0.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (worldIn.getBlockState(pos).getValue(SOUTH).booleanValue()) {
            f3 = Math.min(f3, 0.9375F);
            f6 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (!flag && this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock())) {
            f2 = Math.min(f2, 0.9375F);
            f5 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
        }

        this.setBlockBounds(f1, f2, f3, f4, f5, f6);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return null;
    }

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        switch (side) {
            case UP:
                return this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock());

            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                return this.canPlaceOn(worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock());

            default:
                return false;
        }
    }

    private boolean canPlaceOn(final Block blockIn) {
        return blockIn.isFullCube() && blockIn.blockMaterial.blocksMovement();
    }

    private boolean recheckGrownSides(final World worldIn, final BlockPos pos, IBlockState state) {
        final IBlockState iblockstate = state;

        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            final PropertyBool propertybool = getPropertyFor(enumfacing);

            if (state.getValue(propertybool).booleanValue() && !this.canPlaceOn(worldIn.getBlockState(pos.offset(enumfacing)).getBlock())) {
                final IBlockState iblockstate1 = worldIn.getBlockState(pos.up());

                if (iblockstate1.getBlock() != this || !iblockstate1.getValue(propertybool).booleanValue()) {
                    state = state.withProperty(propertybool, Boolean.valueOf(false));
                }
            }
        }

        if (getNumGrownFaces(state) == 0) {
            return false;
        } else {
            if (iblockstate != state) {
                worldIn.setBlockState(pos, state, 2);
            }

            return true;
        }
    }

    public int getBlockColor() {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int getRenderColor(final IBlockState state) {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        return worldIn.getBiomeGenForCoords(pos).getFoliageColorAtPos(pos);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!worldIn.isRemote && !this.recheckGrownSides(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote) {
            if (worldIn.rand.nextInt(4) == 0) {
                final int i = 4;
                int j = 5;
                boolean flag = false;
                label62:

                for (int k = -i; k <= i; ++k) {
                    for (int l = -i; l <= i; ++l) {
                        for (int i1 = -1; i1 <= 1; ++i1) {
                            if (worldIn.getBlockState(pos.add(k, i1, l)).getBlock() == this) {
                                --j;

                                if (j <= 0) {
                                    flag = true;
                                    break label62;
                                }
                            }
                        }
                    }
                }

                final EnumFacing enumfacing1 = EnumFacing.random(rand);
                final BlockPos blockpos1 = pos.up();

                if (enumfacing1 == EnumFacing.UP && pos.getY() < 255 && worldIn.isAirBlock(blockpos1)) {
                    if (!flag) {
                        IBlockState iblockstate2 = state;

                        for (final EnumFacing enumfacing3 : EnumFacing.Plane.HORIZONTAL) {
                            if (rand.nextBoolean() || !this.canPlaceOn(worldIn.getBlockState(blockpos1.offset(enumfacing3)).getBlock())) {
                                iblockstate2 = iblockstate2.withProperty(getPropertyFor(enumfacing3), Boolean.valueOf(false));
                            }
                        }

                        if (iblockstate2.getValue(NORTH).booleanValue() || iblockstate2.getValue(EAST).booleanValue() || iblockstate2.getValue(SOUTH).booleanValue() || iblockstate2.getValue(WEST).booleanValue()) {
                            worldIn.setBlockState(blockpos1, iblockstate2, 2);
                        }
                    }
                } else if (enumfacing1.getAxis().isHorizontal() && !state.getValue(getPropertyFor(enumfacing1)).booleanValue()) {
                    if (!flag) {
                        final BlockPos blockpos3 = pos.offset(enumfacing1);
                        final Block block1 = worldIn.getBlockState(blockpos3).getBlock();

                        if (block1.blockMaterial == Material.air) {
                            final EnumFacing enumfacing2 = enumfacing1.rotateY();
                            final EnumFacing enumfacing4 = enumfacing1.rotateYCCW();
                            final boolean flag1 = state.getValue(getPropertyFor(enumfacing2)).booleanValue();
                            final boolean flag2 = state.getValue(getPropertyFor(enumfacing4)).booleanValue();
                            final BlockPos blockpos4 = blockpos3.offset(enumfacing2);
                            final BlockPos blockpos = blockpos3.offset(enumfacing4);

                            if (flag1 && this.canPlaceOn(worldIn.getBlockState(blockpos4).getBlock())) {
                                worldIn.setBlockState(blockpos3, this.getDefaultState().withProperty(getPropertyFor(enumfacing2), Boolean.valueOf(true)), 2);
                            } else if (flag2 && this.canPlaceOn(worldIn.getBlockState(blockpos).getBlock())) {
                                worldIn.setBlockState(blockpos3, this.getDefaultState().withProperty(getPropertyFor(enumfacing4), Boolean.valueOf(true)), 2);
                            } else if (flag1 && worldIn.isAirBlock(blockpos4) && this.canPlaceOn(worldIn.getBlockState(pos.offset(enumfacing2)).getBlock())) {
                                worldIn.setBlockState(blockpos4, this.getDefaultState().withProperty(getPropertyFor(enumfacing1.getOpposite()), Boolean.valueOf(true)), 2);
                            } else if (flag2 && worldIn.isAirBlock(blockpos) && this.canPlaceOn(worldIn.getBlockState(pos.offset(enumfacing4)).getBlock())) {
                                worldIn.setBlockState(blockpos, this.getDefaultState().withProperty(getPropertyFor(enumfacing1.getOpposite()), Boolean.valueOf(true)), 2);
                            } else if (this.canPlaceOn(worldIn.getBlockState(blockpos3.up()).getBlock())) {
                                worldIn.setBlockState(blockpos3, this.getDefaultState(), 2);
                            }
                        } else if (block1.blockMaterial.isOpaque() && block1.isFullCube()) {
                            worldIn.setBlockState(pos, state.withProperty(getPropertyFor(enumfacing1), Boolean.valueOf(true)), 2);
                        }
                    }
                } else {
                    if (pos.getY() > 1) {
                        final BlockPos blockpos2 = pos.down();
                        final IBlockState iblockstate = worldIn.getBlockState(blockpos2);
                        final Block block = iblockstate.getBlock();

                        if (block.blockMaterial == Material.air) {
                            IBlockState iblockstate1 = state;

                            for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                                if (rand.nextBoolean()) {
                                    iblockstate1 = iblockstate1.withProperty(getPropertyFor(enumfacing), Boolean.valueOf(false));
                                }
                            }

                            if (iblockstate1.getValue(NORTH).booleanValue() || iblockstate1.getValue(EAST).booleanValue() || iblockstate1.getValue(SOUTH).booleanValue() || iblockstate1.getValue(WEST).booleanValue()) {
                                worldIn.setBlockState(blockpos2, iblockstate1, 2);
                            }
                        } else if (block == this) {
                            IBlockState iblockstate3 = iblockstate;

                            for (final EnumFacing enumfacing5 : EnumFacing.Plane.HORIZONTAL) {
                                final PropertyBool propertybool = getPropertyFor(enumfacing5);

                                if (rand.nextBoolean() && state.getValue(propertybool).booleanValue()) {
                                    iblockstate3 = iblockstate3.withProperty(propertybool, Boolean.valueOf(true));
                                }
                            }

                            if (iblockstate3.getValue(NORTH).booleanValue() || iblockstate3.getValue(EAST).booleanValue() || iblockstate3.getValue(SOUTH).booleanValue() || iblockstate3.getValue(WEST).booleanValue()) {
                                worldIn.setBlockState(blockpos2, iblockstate3, 2);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        final IBlockState iblockstate = this.getDefaultState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false));
        return facing.getAxis().isHorizontal() ? iblockstate.withProperty(getPropertyFor(facing.getOpposite()), Boolean.valueOf(true)) : iblockstate;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random random) {
        return 0;
    }

    public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, final TileEntity te) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            spawnAsEntity(worldIn, pos, new ItemStack(Blocks.vine, 1, 0));
        } else {
            super.harvestBlock(worldIn, player, pos, state, te);
        }
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(SOUTH, Boolean.valueOf((meta & 1) > 0)).withProperty(WEST, Boolean.valueOf((meta & 2) > 0)).withProperty(NORTH, Boolean.valueOf((meta & 4) > 0)).withProperty(EAST, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;

        if (state.getValue(SOUTH).booleanValue()) {
            i |= 1;
        }

        if (state.getValue(WEST).booleanValue()) {
            i |= 2;
        }

        if (state.getValue(NORTH).booleanValue()) {
            i |= 4;
        }

        if (state.getValue(EAST).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, UP, NORTH, EAST, SOUTH, WEST);
    }

    public static PropertyBool getPropertyFor(final EnumFacing side) {
        switch (side) {
            case UP:
                return UP;

            case NORTH:
                return NORTH;

            case SOUTH:
                return SOUTH;

            case EAST:
                return EAST;

            case WEST:
                return WEST;

            default:
                throw new IllegalArgumentException(side + " is an invalid choice");
        }
    }

    public static int getNumGrownFaces(final IBlockState state) {
        int i = 0;

        for (final PropertyBool propertybool : ALL_FACES) {
            if (state.getValue(propertybool).booleanValue()) {
                ++i;
            }
        }

        return i;
    }
}
