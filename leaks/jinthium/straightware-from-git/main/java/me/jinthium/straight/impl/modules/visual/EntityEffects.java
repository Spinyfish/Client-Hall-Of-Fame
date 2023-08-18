package me.jinthium.straight.impl.modules.visual;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.api.shader.ShaderUtil;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.event.render.RenderModelEvent;
import me.jinthium.straight.impl.event.render.ShaderEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.MultiBoolSetting;
import me.jinthium.straight.impl.utils.render.GLUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntityEffects extends Module {

    private final MultiBoolSetting validEntities = new MultiBoolSetting("Valid Entities",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Mobs", true));

    private final BooleanSetting blur = new BooleanSetting("Blur", true);
    private final BooleanSetting bloom = new BooleanSetting("Bloom", true);
    private final BooleanSetting blackBloom = new BooleanSetting("Dark Bloom", true);


    private Framebuffer entityFramebuffer = new Framebuffer(1, 1, false);

    public EntityEffects() {
        super("Entity Effects", Category.VISUALS);
        blackBloom.addParent(bloom, ParentAttribute.BOOLEAN_CONDITION);
        addSettings(validEntities, blur, bloom, blackBloom);
    }

    @Override
    public void onEnable() {
        if (Client.INSTANCE.getModuleManager().getModule(PostProcessing.class).isEnabled()) {
            super.onEnable();
        } else {
            Client.INSTANCE.getNotificationManager().post("Error", "PP not enabled.", NotificationType.ERROR);
            toggle();
        }
    }

    private final List<Entity> entities = new ArrayList<>();

    @Callback
    final EventCallback<Render3DEvent> render3DEventEventCallback = event -> {
        entities.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && RenderUtil.isInView(entity)) {
                entities.add(entity);
            }
        }
    };


    @Callback
    final EventCallback<RenderModelEvent> renderModelEventEventCallback = event -> {
        if (event.isPost() && entities.contains(event.getEntity())) {
            entityFramebuffer.bindFramebuffer(false);
            RenderUtil.resetColor();
            GlStateManager.enableCull();
            GlowESP.renderGlint = false;
            event.drawModel();

            //Needed to add the other layers to the entity
            LayerHeldItem.stopRenderering = true;
            event.drawLayers();
            LayerHeldItem.stopRenderering = false;
            GlowESP.renderGlint = true;
            GlStateManager.disableCull();

            mc.getFramebuffer().bindFramebuffer(false);
        }
    };

    @Callback
    final EventCallback<ShaderEvent> shaderEventEventCallback = event -> {
        if (event.isBloom() ? bloom.isEnabled() : blur.isEnabled()) {
            Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
            RenderUtil.setAlphaLimit(0);
            RenderUtil.resetColor();
            GLUtil.startBlend();

            if (event.isBloom() && blackBloom.isEnabled()) {
                RenderUtil.color(Color.BLACK.getRGB());
            }else if(event.isBloom() && !blackBloom.isEnabled()){
                RenderUtil.color(hud.getHudColor((float) System.currentTimeMillis() / 600).getRGB());
            }

            RenderUtil.bindTexture(entityFramebuffer.framebufferTexture);
            ShaderUtil.drawQuads();

        }
    };

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        entityFramebuffer = RenderUtil.createFrameBuffer(entityFramebuffer);
        entityFramebuffer.framebufferClear();
        mc.getFramebuffer().bindFramebuffer(true);
    };

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (validEntities.getSetting("Players").isEnabled() && entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }
        if (validEntities.getSetting("Animals").isEnabled() && entity instanceof EntityAnimal) {
            return true;
        }

        return validEntities.getSetting("mobs").isEnabled() && entity instanceof EntityMob;
    }

}
