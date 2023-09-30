package dev.africa.pandaware.impl.cape.layer;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.module.render.WaveyCapesModule;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.render.CapeUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class CustomCapeRenderLayer implements LayerRenderer<AbstractClientPlayer>, MinecraftInstance {

    static final int partCount = 16;
    private final RenderPlayer playerRenderer;
    private final SmoothCapeRenderer smoothCapeRenderer = new SmoothCapeRenderer();

    public CustomCapeRenderLayer(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer abstractClientPlayer, float paramFloat1,
                              float paramFloat2, float deltaTick, float animationTick,
                              float paramFloat5, float paramFloat6, float paramFloat7) {

        if (!UISettings.WAVEY_CAPES || abstractClientPlayer.isInvisible()) {
            return;
        }

        if (!abstractClientPlayer.hasPlayerInfo() || abstractClientPlayer.isInvisible()
                || !abstractClientPlayer.isWearing(EnumPlayerModelParts.CAPE)
                || !CapeUtils.shouldRenderCape(abstractClientPlayer)) {
            return;
        }

        WaveyCapesModule waveyCapesModule = Client.getInstance().getModuleManager().getByClass(WaveyCapesModule.class);

        abstractClientPlayer.updateSimulation(abstractClientPlayer, partCount);

        if (waveyCapesModule.getMovementRotation().getValue()) {
            double d0 = abstractClientPlayer.prevChasingPosX + (abstractClientPlayer.chasingPosX - abstractClientPlayer.prevChasingPosX) * (double) mc.timer.renderPartialTicks - (abstractClientPlayer.prevPosX + (abstractClientPlayer.posX - abstractClientPlayer.prevPosX) * (double) mc.timer.renderPartialTicks);
            double d2 = abstractClientPlayer.prevChasingPosZ + (abstractClientPlayer.chasingPosZ - abstractClientPlayer.prevChasingPosZ) * (double) mc.timer.renderPartialTicks - (abstractClientPlayer.prevPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.prevPosZ) * (double) mc.timer.renderPartialTicks);

            float f = abstractClientPlayer.prevRenderYawOffset + (abstractClientPlayer.renderYawOffset
                    - abstractClientPlayer.prevRenderYawOffset) * Minecraft.getMinecraft().timer.renderPartialTicks;

            if (abstractClientPlayer instanceof EntityPlayerSP) {
                if (Minecraft.getMinecraft().thePlayer.isRotationDifferent()) {
                    f = MathUtils.interpolate(abstractClientPlayer.prevRotationYaw, abstractClientPlayer.rotationYaw, Minecraft.getMinecraft().timer.renderPartialTicks);
                }
            }

            double d3 = MathHelper.sin(f * (float) Math.PI / 180.0F);
            double d4 = -MathHelper.cos(f * (float) Math.PI / 180.0F);
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
        }

        CapeUtils.bindTexture(abstractClientPlayer, this.playerRenderer);

        RenderUtils.preLight();

        this.smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick);

        RenderUtils.postLight();
    }

    float getNatrualWindSwing(int part) {
        WaveyCapesModule waveyCapesModule = Client.getInstance().getModuleManager().getByClass(WaveyCapesModule.class);

        if (waveyCapesModule.getWind().getValue()) {
            long highlightedPart = (System.currentTimeMillis() / 3) % 360;
            float relativePart = (float) (part + 1) / partCount;
            return (float) Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3;
        }

        return 0;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}