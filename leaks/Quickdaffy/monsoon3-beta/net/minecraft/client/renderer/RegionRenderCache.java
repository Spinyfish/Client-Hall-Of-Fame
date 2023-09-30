/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.util.ArrayDeque;
import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.DynamicLights;

public class RegionRenderCache
extends ChunkCache {
    private static final IBlockState DEFAULT_STATE = Blocks.air.getDefaultState();
    private final BlockPos position;
    private int[] combinedLights;
    private IBlockState[] blockStates;
    private static ArrayDeque<int[]> cacheLights = new ArrayDeque();
    private static ArrayDeque<IBlockState[]> cacheStates = new ArrayDeque();
    private static int maxCacheSize = Config.limit(Runtime.getRuntime().availableProcessors(), 1, 32);

    public RegionRenderCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
        super(worldIn, posFromIn, posToIn, subIn);
        this.position = posFromIn.subtract(new Vec3i(subIn, subIn, subIn));
        int i = 8000;
        this.combinedLights = RegionRenderCache.allocateLights(8000);
        Arrays.fill(this.combinedLights, -1);
        this.blockStates = RegionRenderCache.allocateStates(8000);
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        int i = (pos.getX() >> 4) - this.chunkX;
        int j = (pos.getZ() >> 4) - this.chunkZ;
        return this.chunkArray[i][j].getTileEntity(pos, Chunk.EnumCreateEntityType.QUEUED);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        int i = this.getPositionIndex(pos);
        int j = this.combinedLights[i];
        if (j == -1) {
            j = super.getCombinedLight(pos, lightValue);
            if (Config.isDynamicLights() && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
                j = DynamicLights.getCombinedLight(pos, j);
            }
            this.combinedLights[i] = j;
        }
        return j;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        int i = this.getPositionIndex(pos);
        IBlockState iblockstate = this.blockStates[i];
        if (iblockstate == null) {
            this.blockStates[i] = iblockstate = this.getBlockStateRaw(pos);
        }
        return iblockstate;
    }

    private IBlockState getBlockStateRaw(BlockPos pos) {
        return super.getBlockState(pos);
    }

    private int getPositionIndex(BlockPos p_175630_1_) {
        int i = p_175630_1_.getX() - this.position.getX();
        int j = p_175630_1_.getY() - this.position.getY();
        int k = p_175630_1_.getZ() - this.position.getZ();
        return i * 400 + k * 20 + j;
    }

    public void freeBuffers() {
        RegionRenderCache.freeLights(this.combinedLights);
        RegionRenderCache.freeStates(this.blockStates);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int[] allocateLights(int p_allocateLights_0_) {
        ArrayDeque<int[]> arrayDeque = cacheLights;
        synchronized (arrayDeque) {
            int[] aint = cacheLights.pollLast();
            if (aint == null || aint.length < p_allocateLights_0_) {
                aint = new int[p_allocateLights_0_];
            }
            return aint;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void freeLights(int[] p_freeLights_0_) {
        ArrayDeque<int[]> arrayDeque = cacheLights;
        synchronized (arrayDeque) {
            if (cacheLights.size() < maxCacheSize) {
                cacheLights.add(p_freeLights_0_);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static IBlockState[] allocateStates(int p_allocateStates_0_) {
        ArrayDeque<IBlockState[]> arrayDeque = cacheStates;
        synchronized (arrayDeque) {
            Object[] aiblockstate = cacheStates.pollLast();
            if (aiblockstate != null && aiblockstate.length >= p_allocateStates_0_) {
                Arrays.fill(aiblockstate, null);
            } else {
                aiblockstate = new IBlockState[p_allocateStates_0_];
            }
            return aiblockstate;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void freeStates(IBlockState[] p_freeStates_0_) {
        ArrayDeque<IBlockState[]> arrayDeque = cacheStates;
        synchronized (arrayDeque) {
            if (cacheStates.size() < maxCacheSize) {
                cacheStates.add(p_freeStates_0_);
            }
        }
    }
}

