package net.minecraft.client.renderer.tileentity;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.entity.model.IEntityRenderer;

public abstract class TileEntitySpecialRenderer<T extends TileEntity> implements IEntityRenderer {
    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};
    protected TileEntityRendererDispatcher rendererDispatcher;
    private Class tileEntityClass = null;
    private ResourceLocation locationTextureCustom = null;

    public abstract void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage);

    public abstract void renderBasicTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage);


    protected void bindTexture(final ResourceLocation location) {
        final TextureManager texturemanager = this.rendererDispatcher.renderEngine;

        if (texturemanager != null) {
            texturemanager.bindTexture(location);
        }
    }

    protected World getWorld() {
        return this.rendererDispatcher.worldObj;
    }

    public void setRendererDispatcher(final TileEntityRendererDispatcher rendererDispatcherIn) {
        this.rendererDispatcher = rendererDispatcherIn;
    }

    public FontRenderer getFontRenderer() {
        return this.rendererDispatcher.getFontRenderer();
    }

    public boolean func_181055_a() {
        return false;
    }

    public void renderTileEntityFast(final T p_renderTileEntityFast_1_, final double p_renderTileEntityFast_2_, final double p_renderTileEntityFast_4_, final double p_renderTileEntityFast_6_, final float p_renderTileEntityFast_8_, final int p_renderTileEntityFast_9_, final WorldRenderer p_renderTileEntityFast_10_) {
    }

    public Class getEntityClass() {
        return this.tileEntityClass;
    }

    public void setEntityClass(final Class p_setEntityClass_1_) {
        this.tileEntityClass = p_setEntityClass_1_;
    }

    public ResourceLocation getLocationTextureCustom() {
        return this.locationTextureCustom;
    }

    public void setLocationTextureCustom(final ResourceLocation p_setLocationTextureCustom_1_) {
        this.locationTextureCustom = p_setLocationTextureCustom_1_;
    }
}
