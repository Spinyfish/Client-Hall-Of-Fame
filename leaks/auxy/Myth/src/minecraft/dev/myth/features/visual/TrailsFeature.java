/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 25.09.22, 18:26
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.events.Render3DEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.display.HUDFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Feature.Info(name = "Trails", category = Feature.Category.VISUAL)
public class TrailsFeature extends Feature {

    /** Settings */
    public final EnumSetting<ColorMode> colorMode = new EnumSetting<>("Color", ColorMode.SYNC);
    public final ColorSetting customColor = new ColorSetting("Custom Color", Color.CYAN).addDependency(() -> colorMode.is(ColorMode.CUSTOM));
    public final NumberSetting rainbowDelay = new NumberSetting("Color Delay", 200, 50, 500, 1).addDependency(() -> colorMode.is(ColorMode.RAINBOW));
    public final NumberSetting startAlpha = new NumberSetting("Start Alpha", 200, 30, 255, 1);

    /** Values */
    public final List<Trail> trails = new ArrayList<>();

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        final double minPosY = getPlayer().getEntityBoundingBox().minY;
        final double maxPosY = getPlayer().getEntityBoundingBox().maxY;

        if (MovementUtil.isMoving())
            trails.add(new Trail(getPlayer().posX, getPlayer().posZ, minPosY, maxPosY, startAlpha.getValue().intValue()));


        for (Trail trail : trails)
            if (System.currentTimeMillis() - trail.lastTime > 170)
                trail.hidden = true;

        trails.removeIf(breadcrumb -> getDistanceToTrail(breadcrumb) > 25 || breadcrumb.alpha == 0); // krasser check
    };

    @Handler
    public final Listener<Render3DEvent> render3DEventListener = event -> {
        final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);
        final double rX = -MC.getRenderManager().viewerPosX;
        final double rY = -MC.getRenderManager().viewerPosY;
        final double rZ = -MC.getRenderManager().viewerPosZ;

        int offset = 0;
        Color color = null;

        switch (colorMode.getValue()) {
            case SYNC:
                color = hudFeature.arrayListColor.getValue();
                break;
            case CUSTOM:
                color = customColor.getValue();
                break;
            case RAINBOW:
                color = ColorUtil.rainbow(rainbowDelay.getValue().intValue());
                break;
        }


        GL11.glPushMatrix();

        MC.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);


        for (Trail trail : trails) {
            if (!trail.isHidden()) {
                if (trail.alpha > 0)
                    trail.alpha -= 1;
                trail.setLastTime(System.currentTimeMillis());
            }

            if (offset > 1) {
                if (offset >= trails.size() - 4 && MC.gameSettings.thirdPersonView == 0)
                    continue;

                final Trail lastTrail = trails.get(offset - 1);
                final double trailX = trail.x - lastTrail.x;
                final double trailY = (trail.maxYPos - trail.minYPos) - (lastTrail.maxYPos - lastTrail.minYPos);
                final double trailZ = trail.z - lastTrail.z;
                final double trailDist = Math.sqrt(trailX * trailX + trailY * trailY + trailZ * trailZ);

                GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) (trail.alpha - (trailDist / 25 * 2)) / 255f);
                GL11.glVertex3d(rX + trail.x, rY + trail.maxYPos, rZ + trail.z);
                GL11.glVertex3d(rX + lastTrail.x, rY + lastTrail.maxYPos, rZ + lastTrail.z);
                GL11.glVertex3d(rX + lastTrail.x, rY + lastTrail.minYPos, rZ + lastTrail.z);
                GL11.glVertex3d(rX + trail.x, rY + trail.minYPos, rZ + trail.z);
            }
            offset++;
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    };

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        trails.clear();
        super.onDisable();
    }

    public double getDistanceToTrail(final Trail trail) {
        final double x = Math.abs(trail.x - getPlayer().posX);
        final double z = Math.abs(trail.z - getPlayer().posZ);
        return MathHelper.sqrt_double(x * x + z * z);
    }

    public enum ColorMode {
        SYNC("Sync"),
        CUSTOM("Custom"),
        RAINBOW("Rainbow");

        private final String name;

        ColorMode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /** ja uhm ich möchte mir das leben nehmen */
    @Getter
    @Setter
    static class Trail {
        private double x, z, minYPos, maxYPos;
        private float alpha;
        private long lastTime;
        private boolean hidden;

        public Trail(final double x, final double z, final double minYPos, final double maxYPos, final float alpha) {
            this.x = x;
            this.z = z;
            this.minYPos = minYPos;
            this.maxYPos = maxYPos;
            this.alpha = alpha;
            this.lastTime = System.currentTimeMillis();
            this.hidden = false;
        }

    }
}
