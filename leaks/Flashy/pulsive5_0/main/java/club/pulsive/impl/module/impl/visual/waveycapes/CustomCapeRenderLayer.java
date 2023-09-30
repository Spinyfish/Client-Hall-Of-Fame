package club.pulsive.impl.module.impl.visual.waveycapes;

import club.pulsive.impl.module.impl.visual.WaveyCapes;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.secondary.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.optifine.player.CapeUtils;

public class CustomCapeRenderLayer implements LayerRenderer<AbstractClientPlayer> {

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


        if (!WaveyCapes.getInstance().isToggled() || abstractClientPlayer.isInvisible() || !(abstractClientPlayer instanceof EntityPlayerSP)) {
            return;
        }

        if (!abstractClientPlayer.hasPlayerInfo() || abstractClientPlayer.isInvisible()
                || !abstractClientPlayer.isWearing(EnumPlayerModelParts.CAPE)
                || !CapeUtils.shouldRenderCape(abstractClientPlayer)) {
            return;
        }

        WaveyCapes waveyCapesModule = WaveyCapes.getInstance();

        abstractClientPlayer.updateSimulation(abstractClientPlayer, partCount);

        if (waveyCapesModule.movementRotation.getValue()) {
            double d0 = abstractClientPlayer.prevChasingPosX + (abstractClientPlayer.chasingPosX - abstractClientPlayer.prevChasingPosX) * (double) Minecraft.getMinecraft().timer.renderPartialTicks - (abstractClientPlayer.prevPosX + (abstractClientPlayer.posX - abstractClientPlayer.prevPosX) * (double) Minecraft.getMinecraft().timer.renderPartialTicks);
            double d2 = abstractClientPlayer.prevChasingPosZ + (abstractClientPlayer.chasingPosZ - abstractClientPlayer.prevChasingPosZ) * (double) Minecraft.getMinecraft().timer.renderPartialTicks - (abstractClientPlayer.prevPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.prevPosZ) * (double) Minecraft.getMinecraft().timer.renderPartialTicks);

            float f = abstractClientPlayer.prevRenderYawOffset + (abstractClientPlayer.renderYawOffset
                    - abstractClientPlayer.prevRenderYawOffset) * Minecraft.getMinecraft().timer.renderPartialTicks;

            if (abstractClientPlayer instanceof EntityPlayerSP) {
                if (Minecraft.getMinecraft().thePlayer.currentEvent.isRotating()) {
                    f = (float) RenderUtil.interpolate(abstractClientPlayer.rotationYaw, abstractClientPlayer.prevRotationYaw, Minecraft.getMinecraft().timer.renderPartialTicks);
                }
            }

            double d3 = MathHelper.sin(f * (float) ApacheMath.PI / 180.0F);
            double d4 = -MathHelper.cos(f * (float) ApacheMath.PI / 180.0F);
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
        }

        CapeUtils.bindTexture(abstractClientPlayer, this.playerRenderer);

        Minecraft.getMinecraft().entityRenderer.disableLightmap();

        this.smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick);

        Minecraft.getMinecraft().entityRenderer.enableLightmap();
    }

    float getNatrualWindSwing(int part) {
        long highlightedPart = (System.currentTimeMillis() / 3) % 360;
        float relativePart = (float) (part + 1) / partCount;
        return (float) ApacheMath.sin(ApacheMath.toRadians((relativePart) * 360 - (highlightedPart))) * 3;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}