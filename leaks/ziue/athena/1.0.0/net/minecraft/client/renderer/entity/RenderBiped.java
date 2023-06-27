package net.minecraft.client.renderer.entity;

import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class RenderBiped<T extends EntityLiving> extends RenderLiving<T>
{
    private static final ResourceLocation DEFAULT_RES_LOC;
    protected ModelBiped modelBipedMain;
    protected float field_77070_b;
    
    public RenderBiped(final RenderManager renderManagerIn, final ModelBiped modelBipedIn, final float shadowSize) {
        this(renderManagerIn, modelBipedIn, shadowSize, 1.0f);
        this.addLayer(new LayerHeldItem(this));
    }
    
    public RenderBiped(final RenderManager renderManagerIn, final ModelBiped modelBipedIn, final float shadowSize, final float p_i46169_4_) {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.modelBipedMain = modelBipedIn;
        this.field_77070_b = p_i46169_4_;
        this.addLayer(new LayerCustomHead(modelBipedIn.bipedHead));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final T entity) {
        return RenderBiped.DEFAULT_RES_LOC;
    }
    
    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0f, 0.1875f, 0.0f);
    }
    
    static {
        DEFAULT_RES_LOC = new ResourceLocation("textures/entity/steve.png");
    }
}
