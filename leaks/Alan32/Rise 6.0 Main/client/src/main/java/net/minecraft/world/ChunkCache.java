package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
    protected int chunkX;
    protected int chunkZ;
    protected Chunk[][] chunkArray;

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    protected boolean hasExtendedLevels;

    /**
     * Reference to the World object.
     */
    protected World worldObj;

    public ChunkCache(final World worldIn, final BlockPos posFromIn, final BlockPos posToIn, final int subIn) {
        this.worldObj = worldIn;
        this.chunkX = posFromIn.getX() - subIn >> 4;
        this.chunkZ = posFromIn.getZ() - subIn >> 4;
        final int i = posToIn.getX() + subIn >> 4;
        final int j = posToIn.getZ() + subIn >> 4;
        this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
        this.hasExtendedLevels = true;

        for (int k = this.chunkX; k <= i; ++k) {
            for (int l = this.chunkZ; l <= j; ++l) {
                this.chunkArray[k - this.chunkX][l - this.chunkZ] = worldIn.getChunkFromChunkCoords(k, l);
            }
        }

        for (int i1 = posFromIn.getX() >> 4; i1 <= posToIn.getX() >> 4; ++i1) {
            for (int j1 = posFromIn.getZ() >> 4; j1 <= posToIn.getZ() >> 4; ++j1) {
                final Chunk chunk = this.chunkArray[i1 - this.chunkX][j1 - this.chunkZ];

                if (chunk != null && !chunk.getAreLevelsEmpty(posFromIn.getY(), posToIn.getY())) {
                    this.hasExtendedLevels = false;
                }
            }
        }
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache() {
        return this.hasExtendedLevels;
    }

    public TileEntity getTileEntity(final BlockPos pos) {
        final int i = (pos.getX() >> 4) - this.chunkX;
        final int j = (pos.getZ() >> 4) - this.chunkZ;
        return this.chunkArray[i][j].getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
    }

    public int getCombinedLight(final BlockPos pos, final int lightValue) {
        final int i = this.getLightForExt(EnumSkyBlock.SKY, pos);
        int j = this.getLightForExt(EnumSkyBlock.BLOCK, pos);

        if (j < lightValue) {
            j = lightValue;
        }

        return i << 20 | j << 4;
    }

    public IBlockState getBlockState(final BlockPos pos) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            final int i = (pos.getX() >> 4) - this.chunkX;
            final int j = (pos.getZ() >> 4) - this.chunkZ;

            if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
                final Chunk chunk = this.chunkArray[i][j];

                if (chunk != null) {
                    return chunk.getBlockState(pos);
                }
            }
        }

        return Blocks.air.getDefaultState();
    }

    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
        return this.worldObj.getBiomeGenForCoords(pos);
    }

    private int getLightForExt(final EnumSkyBlock p_175629_1_, final BlockPos pos) {
        if (p_175629_1_ == EnumSkyBlock.SKY && this.worldObj.provider.getHasNoSky()) {
            return 0;
        } else if (pos.getY() >= 0 && pos.getY() < 256) {
            if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
                int l = 0;

                for (final EnumFacing enumfacing : EnumFacing.values()) {
                    final int k = this.getLightFor(p_175629_1_, pos.offset(enumfacing));

                    if (k > l) {
                        l = k;
                    }

                    if (l >= 15) {
                        return l;
                    }
                }

                return l;
            } else {
                final int i = (pos.getX() >> 4) - this.chunkX;
                final int j = (pos.getZ() >> 4) - this.chunkZ;
                return this.chunkArray[i][j].getLightFor(p_175629_1_, pos);
            }
        } else {
            return p_175629_1_.defaultLightValue;
        }
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     *
     * @param pos The position of the block being checked.
     */
    public boolean isAirBlock(final BlockPos pos) {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
    }

    public int getLightFor(final EnumSkyBlock p_175628_1_, final BlockPos pos) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            final int i = (pos.getX() >> 4) - this.chunkX;
            final int j = (pos.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[i][j].getLightFor(p_175628_1_, pos);
        } else {
            return p_175628_1_.defaultLightValue;
        }
    }

    public int getStrongPower(final BlockPos pos, final EnumFacing direction) {
        final IBlockState iblockstate = this.getBlockState(pos);
        return iblockstate.getBlock().isProvidingStrongPower(this, pos, iblockstate, direction);
    }

    public WorldType getWorldType() {
        return this.worldObj.getWorldType();
    }
}
