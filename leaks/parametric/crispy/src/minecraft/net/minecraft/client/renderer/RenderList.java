package net.minecraft.client.renderer;

import java.util.Iterator;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;

public class RenderList extends ChunkRenderContainer
{

    public void renderChunkLayer(EnumWorldBlockLayer layer)
    {
        if (this.initialized)
        {
            if (this.renderChunks.size() == 0)
            {
                return;
            }

            Iterator var2 = this.renderChunks.iterator();

            while (var2.hasNext())
            {
                RenderChunk var3 = (RenderChunk)var2.next();
                ListedRenderChunk var4 = (ListedRenderChunk)var3;
                GlStateManager.pushMatrix();
                this.preRenderChunk(var3);
                GL11.glCallList(var4.getDisplayList(layer, var4.getCompiledChunk()));
                GlStateManager.popMatrix();
            }

            if (Config.isMultiTexture())
            {
                GlStateManager.bindCurrentTexture();
            }

            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }
}
