package com.alan.clients.component.impl.render.espcomponent.impl;

import com.alan.clients.Client;
import com.alan.clients.component.impl.render.espcomponent.api.ESP;
import com.alan.clients.component.impl.render.espcomponent.api.ESPColor;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class PlayerGlow extends ESP implements InstanceAccess {

    public PlayerGlow(ESPColor espColor) {
        super(espColor);
    }

    @Override
    public void render3D() {
        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
            final float partialTicks = mc.timer.renderPartialTicks;
            for (final Entity player : Client.INSTANCE.getTargetManager().getTargets(100)) {
                final Render<EntityPlayer> render = mc.getRenderManager().getEntityRenderObject(player);

                if (mc.getRenderManager() == null || !(player instanceof EntityPlayer) || render == null || (player == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) || !RenderUtil.isInViewFrustrum(player)) {
                    continue;
                }

                final Color color = ((EntityPlayer)player).hurtTime > 0 ? Color.RED : this.getColor((EntityPlayer)player);

                if (color.getAlpha() <= 0) {
                    continue;
                }

                final double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
                final double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
                final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
                final float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

                RendererLivingEntity.setShaderBrightness(color);
                render.doRender((EntityPlayer)player, x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY, z - mc.getRenderManager().renderPosZ, yaw, partialTicks);
                RendererLivingEntity.unsetShaderBrightness();
            }

            RenderHelper.disableStandardItemLighting();
            mc.entityRenderer.disableLightmap();
        });
    }
}
