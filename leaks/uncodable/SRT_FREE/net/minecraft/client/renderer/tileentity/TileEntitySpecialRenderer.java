package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.entity.model.IEntityRenderer;

public abstract class TileEntitySpecialRenderer<T extends TileEntity> implements IEntityRenderer {
   protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{
      new ResourceLocation("textures/blocks/destroy_stage_0.png"),
      new ResourceLocation("textures/blocks/destroy_stage_1.png"),
      new ResourceLocation("textures/blocks/destroy_stage_2.png"),
      new ResourceLocation("textures/blocks/destroy_stage_3.png"),
      new ResourceLocation("textures/blocks/destroy_stage_4.png"),
      new ResourceLocation("textures/blocks/destroy_stage_5.png"),
      new ResourceLocation("textures/blocks/destroy_stage_6.png"),
      new ResourceLocation("textures/blocks/destroy_stage_7.png"),
      new ResourceLocation("textures/blocks/destroy_stage_8.png"),
      new ResourceLocation("textures/blocks/destroy_stage_9.png")
   };
   protected TileEntityRendererDispatcher rendererDispatcher;
   private Class tileEntityClass = null;
   private ResourceLocation locationTextureCustom = null;

   public abstract void renderTileEntityAt(T var1, double var2, double var4, double var6, float var8, int var9);

   protected void bindTexture(ResourceLocation location) {
      TextureManager texturemanager = this.rendererDispatcher.renderEngine;
      if (texturemanager != null) {
         texturemanager.bindTexture(location);
      }
   }

   protected World getWorld() {
      return this.rendererDispatcher.worldObj;
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      this.rendererDispatcher = rendererDispatcherIn;
   }

   public FontRenderer getFontRenderer() {
      return this.rendererDispatcher.getFontRenderer();
   }

   public boolean func_181055_a() {
      return false;
   }

   @Override
   public Class getEntityClass() {
      return this.tileEntityClass;
   }

   @Override
   public void setEntityClass(Class p_setEntityClass_1_) {
      this.tileEntityClass = p_setEntityClass_1_;
   }

   @Override
   public ResourceLocation getLocationTextureCustom() {
      return this.locationTextureCustom;
   }

   @Override
   public void setLocationTextureCustom(ResourceLocation p_setLocationTextureCustom_1_) {
      this.locationTextureCustom = p_setLocationTextureCustom_1_;
   }
}
