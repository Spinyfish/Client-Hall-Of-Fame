package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.*;

public class RenderEnderman extends RenderLiving<EntityEnderman>
{
    private static final ResourceLocation endermanTextures;
    private ModelEnderman endermanModel;
    private Random rnd;
    
    public RenderEnderman(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelEnderman(0.0f), 0.5f);
        this.rnd = new Random();
        this.endermanModel = (ModelEnderman)super.mainModel;
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerEndermanEyes(this));
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerHeldBlock(this));
    }
    
    @Override
    public void doRender(final EntityEnderman entity, double x, final double y, double z, final float entityYaw, final float partialTicks) {
        this.endermanModel.isCarrying = (entity.getHeldBlockState().getBlock().getMaterial() != Material.air);
        this.endermanModel.isAttacking = entity.isScreaming();
        if (entity.isScreaming()) {
            final double d0 = 0.02;
            x += this.rnd.nextGaussian() * d0;
            z += this.rnd.nextGaussian() * d0;
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityEnderman entity) {
        return RenderEnderman.endermanTextures;
    }
    
    static {
        endermanTextures = new ResourceLocation("textures/entity/enderman/enderman.png");
    }
}
