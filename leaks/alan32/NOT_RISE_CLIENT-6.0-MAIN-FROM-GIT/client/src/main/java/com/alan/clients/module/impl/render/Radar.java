package com.alan.clients.module.impl.render;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Hazsi
 * @since 10/13/2022
 */
@Rise
@ModuleInfo(name = "module.render.radar.name", description = "module.render.radar.description", category = Category.RENDER)
public class Radar extends Module {

    private final ModeValue glowMode = new ModeValue("Glow Mode", this) {{
        add(new SubMode("Colored"));
        add(new SubMode("Shadow"));
        add(new SubMode("None"));
        setDefault("Colored");
    }};

    private final BooleanValue fade = new BooleanValue("Fade", this, true);
    private final BooleanValue scanner = new BooleanValue("Show Scanner", this, true);
    private final NumberValue opacity = new NumberValue("Background Opacity", this, 100, 64, 128, 1);
    private final DragValue position = new DragValue("", this, new Vector2d(200, 200), true);

    private final ResourceLocation radarBeamResource = new ResourceLocation("rise/icons/radar/radarbeam.png");
    private final ResourceLocation radarDotResource = new ResourceLocation("rise/icons/radar/radardot.png");
    private final ResourceLocation radarScopeResource = new ResourceLocation("rise/icons/radar/radarscope.png");

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        position.scale = new Vector2d(100, 100);

        // Don't draw if the F3 menu is open
        if (mc.gameSettings.showDebugInfo) return;

        // Draw the background
        NORMAL_POST_RENDER_RUNNABLES.add(() -> {

            // Draw background with animated fade
            if (this.fade.getValue()) {
                Color color1 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getSecondColor(), getTheme().getBlendFactor(new Vector2d(0, position.position.y)));
                Color color2 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getSecondColor(), getTheme().getBlendFactor(new Vector2d(0, position.position.y + position.scale.y * 8.75)));

                RenderUtil.drawRoundedGradientRect(position.position.x, position.position.y, position.scale.x, position.scale.y,
                        11, ColorUtil.withAlpha(color1, opacity.getValue().intValue()),
                        ColorUtil.withAlpha(color2, opacity.getValue().intValue()), true);
            }

            // Draw static gradient background
            else {
                RenderUtil.drawRoundedGradientRect(position.position.x, position.position.y, position.scale.x, position.scale.y,
                        11, ColorUtil.withAlpha(getTheme().getFirstColor(), opacity.getValue().intValue()),
                        ColorUtil.withAlpha(getTheme().getSecondColor(), opacity.getValue().intValue()), true);
            }
        });

        // Blur the area behind the background
        NORMAL_BLUR_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(position.position.x, position.position.y, position.scale.x, position.scale.y, 11, Color.BLACK);
        });

        // Draw the glow
        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {

            // Colored glow
            if (this.glowMode.getValue().getName().equals("Colored")) {

                Color color1 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getSecondColor(), getTheme().getBlendFactor(new Vector2d(0, position.position.y)));
                Color color2 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getSecondColor(), getTheme().getBlendFactor(new Vector2d(0, position.position.y + position.scale.y * 8.75)));
                boolean fade = this.fade.getValue();

                RenderUtil.drawRoundedGradientRect(position.position.x, position.position.y, position.scale.x, position.scale.y,
                        12, ColorUtil.withAlpha(fade ? color1 : getTheme().getFirstColor(), opacity.getValue().intValue() + 100),
                        ColorUtil.withAlpha(fade ? color2 : getTheme().getSecondColor(), opacity.getValue().intValue() + 100), true);
            }

            // Shadow glow
            else if (this.glowMode.getValue().getName().equals("Shadow")) {
                RenderUtil.roundedRectangle(position.position.x, position.position.y, position.scale.x, position.scale.y,
                        12, getTheme().getDropShadow());
            }
        });

        // Draw the radar icons
        NORMAL_POST_RENDER_RUNNABLES.add(() -> {
            RenderUtil.image(radarScopeResource, position.position.x, position.position.y, position.scale.x, position.scale.y);

            GL11.glPushMatrix();
            GL11.glTranslated(position.position.x + position.scale.x / 2.0F, position.position.y + position.scale.y / 2.0F, 0);
            GL11.glRotated(-mc.thePlayer.rotationYaw + 180, 0, 0, 1);

            // Moved above loop (why calc for every entity xd)
            double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.getPartialTicks();
            double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.getPartialTicks();

            for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                if (entity.getDistanceToEntity(mc.thePlayer) > 30) continue; // someone explain why it has to be hardcoded
                if (entity == mc.thePlayer) continue;
                if (entity instanceof EntityArmorStand || !(entity instanceof EntityLivingBase)) continue;

                double entityX = entity.prevPosX + (entity.posX - entity.prevPosX) * event.getPartialTicks();
                double entityZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * event.getPartialTicks();
                drawPoint((entityX - playerX) * 2, (entityZ - playerZ) * 2);
            }

            GL11.glPopMatrix();
        });

        if (this.scanner.getValue()) {
            NORMAL_POST_RENDER_RUNNABLES.add(() -> {
                GL11.glPushMatrix();
                GL11.glTranslated(position.position.x + position.scale.x / 2.0F, position.position.y + position.scale.y / 2.0F, 0);
                GL11.glRotated(-(System.currentTimeMillis() / 10D) % 360, 0, 0, 1);
                RenderUtil.image(radarBeamResource, -position.scale.x / 2.0F, -position.scale.x / 2.0F, position.scale.x, position.scale.y);
                GL11.glPopMatrix();
            });
        }
    };

    // - 100 to + 100
    private void drawPoint(double x, double y) {
        RenderUtil.image(radarDotResource, x * 0.75 - 8, y * 0.75 - 8, 16, 16);
    }
}