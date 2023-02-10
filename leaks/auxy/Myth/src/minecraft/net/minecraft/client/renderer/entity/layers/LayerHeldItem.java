package net.minecraft.client.renderer.entity.layers;

import dev.myth.features.combat.KillAuraFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase> {
    private final RendererLivingEntity<?> livingEntityRenderer;

    private KillAuraFeature killAuraFeature;

    public LayerHeldItem(RendererLivingEntity<?> livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ItemStack itemstack = entitylivingbaseIn.getHeldItem();

        if (itemstack != null) {
            GlStateManager.pushMatrix();

            if (this.livingEntityRenderer.getMainModel().isChild) {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }
            Minecraft minecraft = Minecraft.getMinecraft();

//            ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625f);

            if(killAuraFeature == null) {
                killAuraFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(KillAuraFeature.class);
            }

            if (entitylivingbaseIn instanceof EntityPlayerSP && (minecraft.thePlayer.isBlocking() || killAuraFeature.shouldRenderBlocking())) {
                ((ModelBiped) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0325F);
                if (entitylivingbaseIn.isSneaking()) {
                    GlStateManager.translate(-0.56F, 0.3F, -0.1F);
                    GlStateManager.rotate(-24399.0F, 137290.0F, -1402900.0F, -2054900.0F);
                } else {
                    GlStateManager.translate(-0.46F, 0.2F, -0.1F);
                    GlStateManager.rotate(-24399.0F, 137290.0F, -1402900.0F, -2054900.0F);
                }
            } else {
                ((ModelBiped) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F);
            }

            GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);

//            if(entitylivingbaseIn instanceof EntityPlayerSP && minecraft.thePlayer.isBlocking()) {
//                GlStateManager.translate(0.05F, 0.0F, -0.1F);
//                GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
//                GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
//                GlStateManager.rotate(-60.0F, 0.0F, 0.0F, 1.0F);
//                GlStateManager.translate(-0.1875F, 0.0F, 0.1875F);
//                if (entitylivingbaseIn.isSneaking()) {
//                    GlStateManager.translate(0, 0, 0.06F);
//                }
//            }

            if (entitylivingbaseIn instanceof EntityPlayer && ((EntityPlayer) entitylivingbaseIn).fishEntity != null) {
                itemstack = new ItemStack(Items.fishing_rod, 0);
            }

            Item item = itemstack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item).getRenderType() == 2) {
                GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
                GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float f1 = 0.375F;
                GlStateManager.scale(-f1, -f1, f1);
            }

            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.203125F, 0.0F);
            }

            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
