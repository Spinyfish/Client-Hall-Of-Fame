package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.monster.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class LayerSlimeGel implements LayerRenderer<EntitySlime>
{
    private final RenderSlime slimeRenderer;
    private final ModelBase slimeModel;
    
    public LayerSlimeGel(final RenderSlime slimeRendererIn) {
        this.slimeModel = new ModelSlime(0);
        this.slimeRenderer = slimeRendererIn;
    }
    
    @Override
    public void doRenderLayer(final EntitySlime entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        if (!entitylivingbaseIn.isInvisible()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
            this.slimeModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
