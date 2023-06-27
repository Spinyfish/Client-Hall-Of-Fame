package shadersmod.client;

import net.minecraft.client.multiplayer.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.util.*;
import java.util.*;

public class ShadowUtils
{
    public static Iterator<RenderChunk> makeShadowChunkIterator(final WorldClient world, final double partialTicks, final Entity viewEntity, final int renderDistanceChunks, final ViewFrustum viewFrustum) {
        final float f = Shaders.getShadowRenderDistance();
        if (f > 0.0f && f < (renderDistanceChunks - 1) * 16) {
            final int i = MathHelper.ceiling_float_int(f / 16.0f) + 1;
            final float f2 = world.getCelestialAngleRadians((float)partialTicks);
            final float f3 = Shaders.sunPathRotation * 0.017453292f;
            final float f4 = (f2 > 1.5707964f && f2 < 4.712389f) ? (f2 + 3.1415927f) : f2;
            final float f5 = -MathHelper.sin(f4);
            final float f6 = MathHelper.cos(f4) * MathHelper.cos(f3);
            final float f7 = -MathHelper.cos(f4) * MathHelper.sin(f3);
            final BlockPos blockpos = new BlockPos(MathHelper.floor_double(viewEntity.posX) >> 4, MathHelper.floor_double(viewEntity.posY) >> 4, MathHelper.floor_double(viewEntity.posZ) >> 4);
            final BlockPos blockpos2 = blockpos.add(-f5 * i, -f6 * i, -f7 * i);
            final BlockPos blockpos3 = blockpos.add(f5 * renderDistanceChunks, f6 * renderDistanceChunks, f7 * renderDistanceChunks);
            final IteratorRenderChunks iteratorrenderchunks = new IteratorRenderChunks(viewFrustum, blockpos2, blockpos3, i, i);
            return iteratorrenderchunks;
        }
        final List<RenderChunk> list = Arrays.asList(viewFrustum.renderChunks);
        final Iterator<RenderChunk> iterator = list.iterator();
        return iterator;
    }
}
