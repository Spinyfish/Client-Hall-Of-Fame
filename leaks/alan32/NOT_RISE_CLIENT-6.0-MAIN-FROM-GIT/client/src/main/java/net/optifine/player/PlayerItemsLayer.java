package net.optifine.player;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.src.Config;

import java.util.Map;
import java.util.Set;

public class PlayerItemsLayer implements LayerRenderer {
    private RenderPlayer renderPlayer = null;

    public PlayerItemsLayer(final RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    public void doRenderLayer(final EntityLivingBase entityLiving, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ticksExisted, final float headYaw, final float rotationPitch, final float scale) {
        this.renderEquippedItems(entityLiving, scale, partialTicks);
    }

    protected void renderEquippedItems(final EntityLivingBase entityLiving, final float scale, final float partialTicks) {
        if (Config.isShowCapes()) {
            if (entityLiving instanceof AbstractClientPlayer) {
                final AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) entityLiving;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableRescaleNormal();
                GlStateManager.enableCull();
                final ModelBiped modelbiped = this.renderPlayer.getMainModel();
                PlayerConfigurations.renderPlayerItems(modelbiped, abstractclientplayer, scale, partialTicks);
                GlStateManager.disableCull();
            }
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    public static void register(final Map renderPlayerMap) {
        final Set set = renderPlayerMap.keySet();
        boolean flag = false;

        for (final Object object : set) {
            final Object object1 = renderPlayerMap.get(object);

            if (object1 instanceof RenderPlayer) {
                final RenderPlayer renderplayer = (RenderPlayer) object1;
                renderplayer.addLayer(new PlayerItemsLayer(renderplayer));
                flag = true;
            }
        }

        if (!flag) {
            Config.warn("PlayerItemsLayer not registered");
        }
    }
}
