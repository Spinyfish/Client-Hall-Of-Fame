package net.minecraft.client.renderer.chunk;

import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;

public class ListedRenderChunk extends RenderChunk
{
    private final int baseDisplayList;
    
    public ListedRenderChunk(final World worldIn, final RenderGlobal renderGlobalIn, final BlockPos pos, final int indexIn) {
        super(worldIn, renderGlobalIn, pos, indexIn);
        this.baseDisplayList = GLAllocation.generateDisplayLists(EnumWorldBlockLayer.values().length);
    }
    
    public int getDisplayList(final EnumWorldBlockLayer layer, final CompiledChunk p_178600_2_) {
        return p_178600_2_.isLayerEmpty(layer) ? -1 : (this.baseDisplayList + layer.ordinal());
    }
    
    @Override
    public void deleteGlResources() {
        super.deleteGlResources();
        GLAllocation.deleteDisplayLists(this.baseDisplayList, EnumWorldBlockLayer.values().length);
    }
}
