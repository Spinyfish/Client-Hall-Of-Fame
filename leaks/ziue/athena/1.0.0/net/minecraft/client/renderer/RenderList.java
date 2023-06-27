package net.minecraft.client.renderer;

import net.minecraft.util.*;
import net.minecraft.client.renderer.chunk.*;
import org.lwjgl.opengl.*;
import optifine.*;
import java.util.*;

public class RenderList extends ChunkRenderContainer
{
    private static final String __OBFID = "CL_00000957";
    
    @Override
    public void renderChunkLayer(final EnumWorldBlockLayer layer) {
        if (this.initialized) {
            if (this.renderChunks.size() == 0) {
                return;
            }
            for (final RenderChunk renderchunk : this.renderChunks) {
                final ListedRenderChunk listedrenderchunk = (ListedRenderChunk)renderchunk;
                GlStateManager.pushMatrix();
                this.preRenderChunk(renderchunk);
                GL11.glCallList(listedrenderchunk.getDisplayList(layer, listedrenderchunk.getCompiledChunk()));
                GlStateManager.popMatrix();
            }
            if (Config.isMultiTexture()) {
                GlStateManager.bindCurrentTexture();
            }
            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }
}
