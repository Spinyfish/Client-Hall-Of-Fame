/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import shadersmod.client.IteratorRenderChunks;
import shadersmod.client.Shaders;

public class ShadowUtils {
    public static Iterator<RenderChunk> makeShadowChunkIterator(WorldClient world, double partialTicks, Entity viewEntity, int renderDistanceChunks, ViewFrustum viewFrustum) {
        float f = Shaders.getShadowRenderDistance();
        if (f > 0.0f && f < (float)((renderDistanceChunks - 1) * 16)) {
            int i = MathHelper.ceiling_float_int(f / 16.0f) + 1;
            float f6 = world.getCelestialAngleRadians((float)partialTicks);
            float f1 = Shaders.sunPathRotation * ((float)Math.PI / 180);
            float f2 = f6 > 1.5707964f && f6 < 4.712389f ? f6 + (float)Math.PI : f6;
            float f3 = -MathHelper.sin(f2);
            float f4 = MathHelper.cos(f2) * MathHelper.cos(f1);
            float f5 = -MathHelper.cos(f2) * MathHelper.sin(f1);
            BlockPos blockpos = new BlockPos(MathHelper.floor_double(viewEntity.posX) >> 4, MathHelper.floor_double(viewEntity.posY) >> 4, MathHelper.floor_double(viewEntity.posZ) >> 4);
            BlockPos blockpos1 = blockpos.add(-f3 * (float)i, -f4 * (float)i, -f5 * (float)i);
            BlockPos blockpos2 = blockpos.add(f3 * (float)renderDistanceChunks, f4 * (float)renderDistanceChunks, f5 * (float)renderDistanceChunks);
            IteratorRenderChunks iteratorrenderchunks = new IteratorRenderChunks(viewFrustum, blockpos1, blockpos2, i, i);
            return iteratorrenderchunks;
        }
        List<RenderChunk> list = Arrays.asList(viewFrustum.renderChunks);
        Iterator<RenderChunk> iterator = list.iterator();
        return iterator;
    }
}

