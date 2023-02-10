/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 17.10.22, 19:24
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.events.Render3DEvent;
import dev.myth.features.display.HUDFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Feature.Info(name = "ChinaHat", description = "Chinese kops", category = Feature.Category.VISUAL)
public class ChinaHatFeature extends Feature {

    /* settings */
    public final EnumSetting<ColorMode> colorMode = new EnumSetting<>("Color Mode", ColorMode.SYNC);
    public final ColorSetting customColor = new ColorSetting("Custom Color", Color.CYAN).addDependency(() -> colorMode.is(ColorMode.CUSTOM));
    public final NumberSetting rainbowDelay = new NumberSetting("Color Delay", 200, 50, 500, 1).addDependency(() -> colorMode.is(ColorMode.RAINBOW));
    public final NumberSetting positionY = new NumberSetting("Position Y", 2.2, 0.5, 3.5, 0.1);
    public final NumberSetting size = new NumberSetting("Size", 1, 0, 2, 0.1);

    @Handler
    public final Listener<Render3DEvent> render3DEventListener = event -> {
        if (MC.gameSettings.thirdPersonView != 0 ) {
            final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);
            float x = (float) ((float)(getPlayer().lastTickPosX + (getPlayer().posX - getPlayer().lastTickPosX) * event.getPartialTicks()) - MC.getRenderManager().getRenderPosX());
            float y = (float) (((float)(getPlayer().lastTickPosY + (getPlayer().posY - getPlayer().lastTickPosY) * event.getPartialTicks()) + positionY.getValue().floatValue()) - MC.getRenderManager().getRenderPosY());
            float z = (float) ((float)(getPlayer().lastTickPosZ + (getPlayer().posZ - getPlayer().lastTickPosZ) * event.getPartialTicks()) - MC.getRenderManager().getRenderPosZ());

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.0F);
            GL11.glBegin(1);
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

            for (double i = 0; i <= 360; i = i + 0.1) {
                GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.2F);
                GL11.glVertex3d(x + Math.sin(i * Math.PI / 180.0D) * size.getValue(), y - 0.3D , z + Math.cos(i * Math.PI / 180.0D) * size.getValue() );
                GL11.glVertex3d(x, y + 0.05D, z);
            }
            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        }
    };

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


}
