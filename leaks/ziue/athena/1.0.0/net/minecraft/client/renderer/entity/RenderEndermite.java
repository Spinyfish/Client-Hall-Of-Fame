package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class RenderEndermite extends RenderLiving<EntityEndermite>
{
    private static final ResourceLocation ENDERMITE_TEXTURES;
    
    public RenderEndermite(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelEnderMite(), 0.3f);
    }
    
    @Override
    protected float getDeathMaxRotation(final EntityEndermite entityLivingBaseIn) {
        return 180.0f;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityEndermite entity) {
        return RenderEndermite.ENDERMITE_TEXTURES;
    }
    
    static {
        ENDERMITE_TEXTURES = new ResourceLocation("textures/entity/endermite.png");
    }
}
