package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPosition;
import net.minecraft.world.World;

public class ListChunkFactory implements IRenderChunkFactory
{
    public RenderChunk makeRenderChunk(World worldIn, RenderGlobal globalRenderer, BlockPosition pos, int index)
    {
        return new ListedRenderChunk(worldIn, globalRenderer, pos, index);
    }
}
