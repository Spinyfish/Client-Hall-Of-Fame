package net.minecraft.client.renderer.chunk;

import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;

public class VboChunkFactory implements IRenderChunkFactory
{
    @Override
    public RenderChunk makeRenderChunk(final World worldIn, final RenderGlobal globalRenderer, final BlockPos pos, final int index) {
        return new RenderChunk(worldIn, globalRenderer, pos, index);
    }
}
