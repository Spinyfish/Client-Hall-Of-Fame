package net.minecraft.client.renderer.entity;

import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class RenderLeashKnot extends Render<EntityLeashKnot>
{
    private static final ResourceLocation leashKnotTextures;
    private ModelLeashKnot leashKnotModel;
    
    public RenderLeashKnot(final RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.leashKnotModel = new ModelLeashKnot();
    }
    
    @Override
    public void doRender(final EntityLeashKnot entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate((float)x, (float)y, (float)z);
        final float f = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlpha();
        this.bindEntityTexture(entity);
        this.leashKnotModel.render(entity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityLeashKnot entity) {
        return RenderLeashKnot.leashKnotTextures;
    }
    
    static {
        leashKnotTextures = new ResourceLocation("textures/entity/lead_knot.png");
    }
}
