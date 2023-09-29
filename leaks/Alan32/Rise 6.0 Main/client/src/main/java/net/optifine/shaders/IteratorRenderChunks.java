package net.optifine.shaders;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.optifine.BlockPosM;

import java.util.Iterator;

public class IteratorRenderChunks implements Iterator<RenderChunk> {
    private final ViewFrustum viewFrustum;
    private final Iterator3d Iterator3d;
    private final BlockPosM posBlock = new BlockPosM(0, 0, 0);

    public IteratorRenderChunks(final ViewFrustum viewFrustum, final BlockPos posStart, final BlockPos posEnd, final int width, final int height) {
        this.viewFrustum = viewFrustum;
        this.Iterator3d = new Iterator3d(posStart, posEnd, width, height);
    }

    public boolean hasNext() {
        return this.Iterator3d.hasNext();
    }

    public RenderChunk next() {
        final BlockPos blockpos = this.Iterator3d.next();
        this.posBlock.setXyz(blockpos.getX() << 4, blockpos.getY() << 4, blockpos.getZ() << 4);
        final RenderChunk renderchunk = this.viewFrustum.getRenderChunk(this.posBlock);
        return renderchunk;
    }

    public void remove() {
        throw new RuntimeException("Not implemented");
    }
}
