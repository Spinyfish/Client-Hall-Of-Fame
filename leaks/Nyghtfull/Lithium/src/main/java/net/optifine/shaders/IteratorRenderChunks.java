package net.optifine.shaders;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPosition;
import net.optifine.BlockPositionM;

import java.util.Iterator;

public class IteratorRenderChunks implements Iterator<RenderChunk>
{
    private ViewFrustum viewFrustum;
    private Iterator3d Iterator3d;
    private BlockPositionM posBlock = new BlockPositionM(0, 0, 0);

    public IteratorRenderChunks(ViewFrustum viewFrustum, BlockPosition posStart, BlockPosition posEnd, int width, int height)
    {
        this.viewFrustum = viewFrustum;
        this.Iterator3d = new Iterator3d(posStart, posEnd, width, height);
    }

    public boolean hasNext()
    {
        return this.Iterator3d.hasNext();
    }

    public RenderChunk next()
    {
        BlockPosition blockpos = this.Iterator3d.next();
        this.posBlock.setXyz(blockpos.getX() << 4, blockpos.getY() << 4, blockpos.getZ() << 4);
        RenderChunk renderchunk = this.viewFrustum.getRenderChunk(this.posBlock);
        return renderchunk;
    }

    public void remove()
    {
        throw new RuntimeException("Not implemented");
    }
}
