package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public class BlockBed extends BlockDirectional {
    public static final PropertyEnum<BlockBed.EnumPartType> PART = PropertyEnum.create("part", BlockBed.EnumPartType.class);
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");

    public BlockBed() {
        super(Material.cloth);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(OCCUPIED, Boolean.valueOf(false)));
        this.setBedBounds();
    }

    public boolean onBlockActivated(final World worldIn, BlockPos pos, IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            if (state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
                pos = pos.offset(state.getValue(FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (worldIn.provider.canRespawnHere() && worldIn.getBiomeGenForCoords(pos) != BiomeGenBase.hell) {
                if (state.getValue(OCCUPIED).booleanValue()) {
                    final EntityPlayer entityplayer = this.getPlayerInBed(worldIn, pos);

                    if (entityplayer != null) {
                        playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));
                        return true;
                    }

                    state = state.withProperty(OCCUPIED, Boolean.valueOf(false));
                    worldIn.setBlockState(pos, state, 4);
                }

                final EntityPlayer.EnumStatus entityplayer$enumstatus = playerIn.trySleep(pos);

                if (entityplayer$enumstatus == EntityPlayer.EnumStatus.OK) {
                    state = state.withProperty(OCCUPIED, Boolean.valueOf(true));
                    worldIn.setBlockState(pos, state, 4);
                    return true;
                } else {
                    if (entityplayer$enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
                        playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
                    } else if (entityplayer$enumstatus == EntityPlayer.EnumStatus.NOT_SAFE) {
                        playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
                    }

                    return true;
                }
            } else {
                worldIn.setBlockToAir(pos);
                final BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());

                if (worldIn.getBlockState(blockpos).getBlock() == this) {
                    worldIn.setBlockToAir(blockpos);
                }

                worldIn.newExplosion(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, true);
                return true;
            }
        }
    }

    private EntityPlayer getPlayerInBed(final World worldIn, final BlockPos pos) {
        for (final EntityPlayer entityplayer : worldIn.playerEntities) {
            if (entityplayer.isPlayerSleeping() && entityplayer.playerLocation.equals(pos)) {
                return entityplayer;
            }
        }

        return null;
    }

    public boolean isFullCube() {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        this.setBedBounds();
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        final EnumFacing enumfacing = state.getValue(FACING);

        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
                worldIn.setBlockToAir(pos);
            }
        } else if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
            worldIn.setBlockToAir(pos);

            if (!worldIn.isRemote) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : Items.bed;
    }

    private void setBedBounds() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    /**
     * Returns a safe BlockPos to disembark the bed
     */
    public static BlockPos getSafeExitLocation(final World worldIn, final BlockPos pos, int tries) {
        final EnumFacing enumfacing = worldIn.getBlockState(pos).getValue(FACING);
        final int i = pos.getX();
        final int j = pos.getY();
        final int k = pos.getZ();

        for (int l = 0; l <= 1; ++l) {
            final int i1 = i - enumfacing.getFrontOffsetX() * l - 1;
            final int j1 = k - enumfacing.getFrontOffsetZ() * l - 1;
            final int k1 = i1 + 2;
            final int l1 = j1 + 2;

            for (int i2 = i1; i2 <= k1; ++i2) {
                for (int j2 = j1; j2 <= l1; ++j2) {
                    final BlockPos blockpos = new BlockPos(i2, j, j2);

                    if (hasRoomForPlayer(worldIn, blockpos)) {
                        if (tries <= 0) {
                            return blockpos;
                        }

                        --tries;
                    }
                }
            }
        }

        return null;
    }

    protected static boolean hasRoomForPlayer(final World worldIn, final BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !worldIn.getBlockState(pos).getBlock().getMaterial().isSolid() && !worldIn.getBlockState(pos.up()).getBlock().getMaterial().isSolid();
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    public int getMobilityFlag() {
        return 1;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.bed;
    }

    public void onBlockHarvested(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            final BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());

            if (worldIn.getBlockState(blockpos).getBlock() == this) {
                worldIn.setBlockToAir(blockpos);
            }
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        final EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, Boolean.valueOf((meta & 4) > 0)) : this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(FACING, enumfacing);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
            final IBlockState iblockstate = worldIn.getBlockState(pos.offset(state.getValue(FACING)));

            if (iblockstate.getBlock() == this) {
                state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
            }
        }

        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            i |= 8;

            if (state.getValue(OCCUPIED).booleanValue()) {
                i |= 4;
            }
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, PART, OCCUPIED);
    }

    public enum EnumPartType implements IStringSerializable {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        EnumPartType(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
