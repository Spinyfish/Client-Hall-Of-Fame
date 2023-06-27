package net.minecraft.client.renderer.chunk;

import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;

public class ListChunkFactory implements IRenderChunkFactory
{
    @Override
    public RenderChunk makeRenderChunk(final World worldIn, final RenderGlobal globalRenderer, final BlockPos pos, final int index) {
        return new ListedRenderChunk(worldIn, globalRenderer, pos, index);
    }
}
