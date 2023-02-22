package net.optifine.shaders;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ShadowUtils {
    public static Iterator<RenderChunk> makeShadowChunkIterator(final WorldClient world, final double partialTicks, final Entity viewEntity, final int renderDistanceChunks, final ViewFrustum viewFrustum) {
        final float f = Shaders.getShadowRenderDistance();

        if (f > 0.0F && f < (float) ((renderDistanceChunks - 1) * 16)) {
            final int i = MathHelper.ceiling_float_int(f / 16.0F) + 1;
            final float f6 = world.getCelestialAngleRadians((float) partialTicks);
            final float f1 = Shaders.sunPathRotation * MathHelper.deg2Rad;
            final float f2 = f6 > MathHelper.PId2 && f6 < 3.0F * MathHelper.PId2 ? f6 + MathHelper.PI : f6;
            final float f3 = -MathHelper.sin(f2);
            final float f4 = MathHelper.cos(f2) * MathHelper.cos(f1);
            final float f5 = -MathHelper.cos(f2) * MathHelper.sin(f1);
            final BlockPos blockpos = new BlockPos(MathHelper.floor_double(viewEntity.posX) >> 4, MathHelper.floor_double(viewEntity.posY) >> 4, MathHelper.floor_double(viewEntity.posZ) >> 4);
            final BlockPos blockpos1 = blockpos.add(-f3 * (float) i, -f4 * (float) i, -f5 * (float) i);
            final BlockPos blockpos2 = blockpos.add(f3 * (float) renderDistanceChunks, f4 * (float) renderDistanceChunks, f5 * (float) renderDistanceChunks);
            final IteratorRenderChunks iteratorrenderchunks = new IteratorRenderChunks(viewFrustum, blockpos1, blockpos2, i, i);
            return iteratorrenderchunks;
        } else {
            final List<RenderChunk> list = Arrays.asList(viewFrustum.renderChunks);
            final Iterator<RenderChunk> iterator = list.iterator();
            return iterator;
        }
    }
}
