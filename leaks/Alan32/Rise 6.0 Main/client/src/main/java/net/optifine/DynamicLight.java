package net.optifine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class DynamicLight {
    private Entity entity = null;
    private double offsetY = 0.0D;
    private double lastPosX = -2.147483648E9D;
    private double lastPosY = -2.147483648E9D;
    private double lastPosZ = -2.147483648E9D;
    private int lastLightLevel = 0;
    private boolean underwater = false;
    private long timeCheckMs = 0L;
    private Set<BlockPos> setLitChunkPos = new HashSet();
    private final BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();

    public DynamicLight(final Entity entity) {
        this.entity = entity;
        this.offsetY = entity.getEyeHeight();
    }

    public void update(final RenderGlobal renderGlobal) {
        if (Config.isDynamicLightsFast()) {
            final long i = System.currentTimeMillis();

            if (i < this.timeCheckMs + 500L) {
                return;
            }

            this.timeCheckMs = i;
        }

        final double d6 = this.entity.posX - 0.5D;
        final double d0 = this.entity.posY - 0.5D + this.offsetY;
        final double d1 = this.entity.posZ - 0.5D;
        final int j = DynamicLights.getLightLevel(this.entity);
        final double d2 = d6 - this.lastPosX;
        final double d3 = d0 - this.lastPosY;
        final double d4 = d1 - this.lastPosZ;
        final double d5 = 0.1D;

        if (Math.abs(d2) > d5 || Math.abs(d3) > d5 || Math.abs(d4) > d5 || this.lastLightLevel != j) {
            this.lastPosX = d6;
            this.lastPosY = d0;
            this.lastPosZ = d1;
            this.lastLightLevel = j;
            this.underwater = false;
            final World world = renderGlobal.getWorld();

            if (world != null) {
                this.blockPosMutable.func_181079_c(MathHelper.floor_double(d6), MathHelper.floor_double(d0), MathHelper.floor_double(d1));
                final IBlockState iblockstate = world.getBlockState(this.blockPosMutable);
                final Block block = iblockstate.getBlock();
                this.underwater = block == Blocks.water;
            }

            final Set<BlockPos> set = new HashSet();

            if (j > 0) {
                final EnumFacing enumfacing2 = (MathHelper.floor_double(d6) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
                final EnumFacing enumfacing = (MathHelper.floor_double(d0) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
                final EnumFacing enumfacing1 = (MathHelper.floor_double(d1) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;
                final BlockPos blockpos = new BlockPos(d6, d0, d1);
                final RenderChunk renderchunk = renderGlobal.getRenderChunk(blockpos);
                final BlockPos blockpos1 = this.getChunkPos(renderchunk, blockpos, enumfacing2);
                final RenderChunk renderchunk1 = renderGlobal.getRenderChunk(blockpos1);
                final BlockPos blockpos2 = this.getChunkPos(renderchunk, blockpos, enumfacing1);
                final RenderChunk renderchunk2 = renderGlobal.getRenderChunk(blockpos2);
                final BlockPos blockpos3 = this.getChunkPos(renderchunk1, blockpos1, enumfacing1);
                final RenderChunk renderchunk3 = renderGlobal.getRenderChunk(blockpos3);
                final BlockPos blockpos4 = this.getChunkPos(renderchunk, blockpos, enumfacing);
                final RenderChunk renderchunk4 = renderGlobal.getRenderChunk(blockpos4);
                final BlockPos blockpos5 = this.getChunkPos(renderchunk4, blockpos4, enumfacing2);
                final RenderChunk renderchunk5 = renderGlobal.getRenderChunk(blockpos5);
                final BlockPos blockpos6 = this.getChunkPos(renderchunk4, blockpos4, enumfacing1);
                final RenderChunk renderchunk6 = renderGlobal.getRenderChunk(blockpos6);
                final BlockPos blockpos7 = this.getChunkPos(renderchunk5, blockpos5, enumfacing1);
                final RenderChunk renderchunk7 = renderGlobal.getRenderChunk(blockpos7);
                this.updateChunkLight(renderchunk, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk1, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk2, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk3, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk4, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk5, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk6, this.setLitChunkPos, set);
                this.updateChunkLight(renderchunk7, this.setLitChunkPos, set);
            }

            this.updateLitChunks(renderGlobal);
            this.setLitChunkPos = set;
        }
    }

    private BlockPos getChunkPos(final RenderChunk renderChunk, final BlockPos pos, final EnumFacing facing) {
        return renderChunk != null ? renderChunk.func_181701_a(facing) : pos.offset(facing, 16);
    }

    private void updateChunkLight(final RenderChunk renderChunk, final Set<BlockPos> setPrevPos, final Set<BlockPos> setNewPos) {
        if (renderChunk != null) {
            final CompiledChunk compiledchunk = renderChunk.getCompiledChunk();

            if (compiledchunk != null && !compiledchunk.isEmpty()) {
                renderChunk.setNeedsUpdate(true);
            }

            final BlockPos blockpos = renderChunk.getPosition();

            if (setPrevPos != null) {
                setPrevPos.remove(blockpos);
            }

            if (setNewPos != null) {
                setNewPos.add(blockpos);
            }
        }
    }

    public void updateLitChunks(final RenderGlobal renderGlobal) {
        for (final BlockPos blockpos : this.setLitChunkPos) {
            final RenderChunk renderchunk = renderGlobal.getRenderChunk(blockpos);
            this.updateChunkLight(renderchunk, null, null);
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public double getLastPosX() {
        return this.lastPosX;
    }

    public double getLastPosY() {
        return this.lastPosY;
    }

    public double getLastPosZ() {
        return this.lastPosZ;
    }

    public int getLastLightLevel() {
        return this.lastLightLevel;
    }

    public boolean isUnderwater() {
        return this.underwater;
    }

    public double getOffsetY() {
        return this.offsetY;
    }

    public String toString() {
        return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
    }
}
