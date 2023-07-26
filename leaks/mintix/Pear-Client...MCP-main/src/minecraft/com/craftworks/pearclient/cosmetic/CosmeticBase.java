package com.craftworks.pearclient.cosmetic;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public abstract class CosmeticBase implements LayerRenderer<AbstractClientPlayer> {
    protected final RenderPlayer playerRendner;

    public CosmeticBase(RenderPlayer playerRendner) {
        this.playerRendner = playerRendner;
    }

    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicl, float netHeadYaw, float headPitch, float scale) {
        if (player.hasPlayerInfo() && !player.isInvisible()) {
            this.render(player, limbSwing, limbSwingAmount, partialTicks, ageInTicl, netHeadYaw, headPitch, scale);
        }

    }

    public abstract void render(AbstractClientPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

    public boolean shouldCombineTextures() {
        return false;
    }

    public ModelRenderer bindTextureAndColor(Color color, ResourceLocation resourceLocation, ModelRenderer colorModel, ModelRenderer playerSkinModel) {
        boolean flag = false;
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        return colorModel;
    }
}
