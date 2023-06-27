package net.minecraft.client.renderer.chunk;

import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;

public interface IRenderChunkFactory
{
    RenderChunk makeRenderChunk(final World p0, final RenderGlobal p1, final BlockPos p2, final int p3);
}
