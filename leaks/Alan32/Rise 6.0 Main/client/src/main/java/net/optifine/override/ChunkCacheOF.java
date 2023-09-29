package net.optifine.override;

import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.util.ArrayCache;

import java.util.Arrays;

public class ChunkCacheOF implements IBlockAccess {
    private final ChunkCache chunkCache;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int sizeXY;
    private int[] combinedLights;
    private IBlockState[] blockStates;
    private final int arraySize;
    private final boolean dynamicLights = Config.isDynamicLights();
    private static final ArrayCache cacheCombinedLights = new ArrayCache(Integer.TYPE, 16);
    private static final ArrayCache cacheBlockStates = new ArrayCache(IBlockState.class, 16);

    public ChunkCacheOF(final ChunkCache chunkCache, final BlockPos posFromIn, final BlockPos posToIn, final int subIn) {
        this.chunkCache = chunkCache;
        final int i = posFromIn.getX() - subIn >> 4;
        final int j = posFromIn.getY() - subIn >> 4;
        final int k = posFromIn.getZ() - subIn >> 4;
        final int l = posToIn.getX() + subIn >> 4;
        final int i1 = posToIn.getY() + subIn >> 4;
        final int j1 = posToIn.getZ() + subIn >> 4;
        this.sizeX = l - i + 1 << 4;
        this.sizeY = i1 - j + 1 << 4;
        this.sizeZ = j1 - k + 1 << 4;
        this.sizeXY = this.sizeX * this.sizeY;
        this.arraySize = this.sizeX * this.sizeY * this.sizeZ;
        this.posX = i << 4;
        this.posY = j << 4;
        this.posZ = k << 4;
    }

    private int getPositionIndex(final BlockPos pos) {
        final int i = pos.getX() - this.posX;

        if (i >= 0 && i < this.sizeX) {
            final int j = pos.getY() - this.posY;

            if (j >= 0 && j < this.sizeY) {
                final int k = pos.getZ() - this.posZ;
                return k >= 0 && k < this.sizeZ ? k * this.sizeXY + j * this.sizeX + i : -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public int getCombinedLight(final BlockPos pos, final int lightValue) {
        final int i = this.getPositionIndex(pos);

        if (i >= 0 && i < this.arraySize && this.combinedLights != null) {
            int j = this.combinedLights[i];

            if (j == -1) {
                j = this.getCombinedLightRaw(pos, lightValue);
                this.combinedLights[i] = j;
            }

            return j;
        } else {
            return this.getCombinedLightRaw(pos, lightValue);
        }
    }

    private int getCombinedLightRaw(final BlockPos pos, final int lightValue) {
        int i = this.chunkCache.getCombinedLight(pos, lightValue);

        if (this.dynamicLights && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
            i = DynamicLights.getCombinedLight(pos, i);
        }

        return i;
    }

    public IBlockState getBlockState(final BlockPos pos) {
        final int i = this.getPositionIndex(pos);

        if (i >= 0 && i < this.arraySize && this.blockStates != null) {
            IBlockState iblockstate = this.blockStates[i];

            if (iblockstate == null) {
                iblockstate = this.chunkCache.getBlockState(pos);
                this.blockStates[i] = iblockstate;
            }

            return iblockstate;
        } else {
            return this.chunkCache.getBlockState(pos);
        }
    }

    public void renderStart() {
        if (this.combinedLights == null) {
            this.combinedLights = (int[]) cacheCombinedLights.allocate(this.arraySize);
        }

        Arrays.fill(this.combinedLights, -1);

        if (this.blockStates == null) {
            this.blockStates = (IBlockState[]) cacheBlockStates.allocate(this.arraySize);
        }

        Arrays.fill(this.blockStates, null);
    }

    public void renderFinish() {
        cacheCombinedLights.free(this.combinedLights);
        this.combinedLights = null;
        cacheBlockStates.free(this.blockStates);
        this.blockStates = null;
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache() {
        return this.chunkCache.extendedLevelsInChunkCache();
    }

    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
        return this.chunkCache.getBiomeGenForCoords(pos);
    }

    public int getStrongPower(final BlockPos pos, final EnumFacing direction) {
        return this.chunkCache.getStrongPower(pos, direction);
    }

    public TileEntity getTileEntity(final BlockPos pos) {
        return this.chunkCache.getTileEntity(pos);
    }

    public WorldType getWorldType() {
        return this.chunkCache.getWorldType();
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     *
     * @param pos The position of the block being checked.
     */
    public boolean isAirBlock(final BlockPos pos) {
        return this.chunkCache.isAirBlock(pos);
    }

    public boolean isSideSolid(final BlockPos pos, final EnumFacing side, final boolean _default) {
        return Reflector.callBoolean(this.chunkCache, Reflector.ForgeChunkCache_isSideSolid, pos, side, Boolean.valueOf(_default));
    }
}
