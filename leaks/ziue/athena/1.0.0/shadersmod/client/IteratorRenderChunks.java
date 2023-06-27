package shadersmod.client;

import java.util.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.client.renderer.*;
import optifine.*;
import net.minecraft.util.*;

public class IteratorRenderChunks implements Iterator<RenderChunk>
{
    private ViewFrustum viewFrustum;
    private Iterator3d Iterator3d;
    private BlockPosM posBlock;
    
    public IteratorRenderChunks(final ViewFrustum viewFrustum, final BlockPos posStart, final BlockPos posEnd, final int width, final int height) {
        this.posBlock = new BlockPosM(0, 0, 0);
        this.viewFrustum = viewFrustum;
        this.Iterator3d = new Iterator3d(posStart, posEnd, width, height);
    }
    
    @Override
    public boolean hasNext() {
        return this.Iterator3d.hasNext();
    }
    
    @Override
    public RenderChunk next() {
        final BlockPos blockpos = this.Iterator3d.next();
        this.posBlock.setXyz(blockpos.getX() << 4, blockpos.getY() << 4, blockpos.getZ() << 4);
        final RenderChunk renderchunk = this.viewFrustum.getRenderChunk(this.posBlock);
        return renderchunk;
    }
    
    @Override
    public void remove() {
        throw new RuntimeException("Not implemented");
    }
}
