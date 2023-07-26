package com.craftworks.pearclient.cosmetic;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class Cosmetic {
    public Cosmetic(RenderPlayer renderPlayer) {
    }

    protected ModelRenderer bindTextureAndColor(Color color, ResourceLocation resourceLocation, ModelRenderer colorModel, ModelRenderer playerSkinModel) {
        boolean flag = false;
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        return colorModel;
    }

    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
    }
}
