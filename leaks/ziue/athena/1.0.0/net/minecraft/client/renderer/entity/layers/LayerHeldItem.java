package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.modules.impl.mods.*;
import rip.athena.client.*;
import rip.athena.client.modules.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.model.*;
import net.minecraft.init.*;
import net.minecraft.client.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.block.model.*;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase>
{
    private final RendererLivingEntity<?> livingEntityRenderer;
    
    public LayerHeldItem(final RendererLivingEntity<?> livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }
    
    @Override
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        ItemStack itemstack = entitylivingbaseIn.getHeldItem();
        if (itemstack != null) {
            GlStateManager.pushMatrix();
            if (this.livingEntityRenderer.getMainModel().isChild) {
                final float f = 0.5f;
                GlStateManager.translate(0.0f, 0.625f, 0.0f);
                GlStateManager.rotate(-20.0f, -1.0f, 0.0f, 0.0f);
                GlStateManager.scale(f, f, f);
            }
            final OldAnimations mod = (OldAnimations)Athena.INSTANCE.getModuleManager().get(OldAnimations.class);
            if (entitylivingbaseIn instanceof EntityPlayer) {
                if (Athena.INSTANCE.getModuleManager().get(OldAnimations.class).isToggled() && mod.OLD_BLOCKING) {
                    if (((EntityPlayer)entitylivingbaseIn).isBlocking()) {
                        if (entitylivingbaseIn.isSneaking()) {
                            ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0325f);
                            GlStateManager.scale(1.05f, 1.05f, 1.05f);
                            GlStateManager.translate(-0.58f, 0.32f, -0.07f);
                            GlStateManager.rotate(-24405.0f, 137290.0f, -2009900.0f, -2654900.0f);
                        }
                        else {
                            ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0325f);
                            GlStateManager.scale(1.05f, 1.05f, 1.05f);
                            GlStateManager.translate(-0.45f, 0.25f, -0.07f);
                            GlStateManager.rotate(-24405.0f, 137290.0f, -2009900.0f, -2654900.0f);
                        }
                    }
                    else {
                        ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625f);
                    }
                }
                else {
                    ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625f);
                }
                if (Athena.INSTANCE.getModuleManager().get(OldAnimations.class).isToggled() && mod.OLD_ITEM_HELD) {
                    if (!((EntityPlayer)entitylivingbaseIn).isBlocking()) {
                        GlStateManager.translate(-0.0855f, 0.4775f, 0.1585f);
                        GlStateManager.rotate(-19.0f, 20.0f, 0.0f, -6.0f);
                    }
                    else if (((EntityPlayer)entitylivingbaseIn).isBlocking()) {
                        GlStateManager.translate(-0.0625f, 0.4375f, 0.0625f);
                    }
                }
                else {
                    GlStateManager.translate(-0.0625f, 0.4375f, 0.0625f);
                }
            }
            else {
                ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625f);
                GlStateManager.translate(-0.0625f, 0.4375f, 0.0625f);
            }
            if (entitylivingbaseIn instanceof EntityPlayer && ((EntityPlayer)entitylivingbaseIn).fishEntity != null) {
                itemstack = new ItemStack(Items.fishing_rod, 0);
            }
            final Item item = itemstack.getItem();
            final Minecraft minecraft = Minecraft.getMinecraft();
            if (item instanceof ItemBlock && Block.getBlockFromItem(item).getRenderType() == 2) {
                GlStateManager.translate(0.0f, 0.1875f, -0.3125f);
                GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
                final float f2 = 0.375f;
                GlStateManager.scale(-f2, -f2, f2);
            }
            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0f, 0.203125f, 0.0f);
            }
            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
