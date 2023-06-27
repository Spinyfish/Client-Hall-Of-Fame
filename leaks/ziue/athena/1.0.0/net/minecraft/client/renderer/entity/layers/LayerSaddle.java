package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.passive.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class LayerSaddle implements LayerRenderer<EntityPig>
{
    private static final ResourceLocation TEXTURE;
    private final RenderPig pigRenderer;
    private final ModelPig pigModel;
    
    public LayerSaddle(final RenderPig pigRendererIn) {
        this.pigModel = new ModelPig(0.5f);
        this.pigRenderer = pigRendererIn;
    }
    
    @Override
    public void doRenderLayer(final EntityPig entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        if (entitylivingbaseIn.getSaddled()) {
            this.pigRenderer.bindTexture(LayerSaddle.TEXTURE);
            this.pigModel.setModelAttributes(this.pigRenderer.getMainModel());
            this.pigModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
    
    static {
        TEXTURE = new ResourceLocation("textures/entity/pig/pig_saddle.png");
    }
}
