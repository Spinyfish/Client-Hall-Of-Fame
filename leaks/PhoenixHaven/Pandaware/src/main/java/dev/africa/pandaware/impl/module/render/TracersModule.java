package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.ColorSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.vector.Vec3d;
import dev.africa.pandaware.utils.render.ColorUtils;
import lombok.AllArgsConstructor;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Tracers", category = Category.VISUAL)
public class TracersModule extends Module {
    private final EnumSetting<ColorMode> colorMode = new EnumSetting<>("Color mode", ColorMode.CUSTOM);
    private final ColorSetting firstColor
            = new ColorSetting("Color", UISettings.DEFAULT_FIRST_COLOR,
            () -> this.colorMode.getValue() == ColorMode.CUSTOM);
    private final BooleanSetting invisible = new BooleanSetting("Invisible", true);
    private final BooleanSetting center = new BooleanSetting("Center", false);
    private final NumberSetting lineThickness = new NumberSetting("Line Thickness", 2, 0.1, 2, 0.1);

    public TracersModule() {
        this.registerSettings(this.colorMode, this.center, this.lineThickness, this.firstColor, this.invisible);
    }

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_3D) {
            GL11.glPushMatrix();

            // gl caps
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);

            GL11.glLineWidth(this.lineThickness.getValue().floatValue());

            // center position on screen if enabled
            if (this.center.getValue()) {
                GL11.glTranslated(0, mc.thePlayer.getEyeHeight(), 0);
            }

            mc.theWorld.playerEntities.stream().filter(entity -> entity != mc.thePlayer).forEach(entity -> {
                if (Client.getInstance().getIgnoreManager().isIgnore(entity, false)) return;
                if ((invisible.getValue() && entity.isInvisible()) ||
                        (!invisible.getValue() && !entity.isInvisible())) return;
                // begin line rendering
                GL11.glLineWidth(this.lineThickness.getValue().floatValue());
                GL11.glBegin(GL11.GL_LINE_STRIP);

                // set color
                ColorUtils.glColor(ColorUtils.getColorAlpha(this.getColor(1), 190));

                // interpolate position to make it smooth
                Vec3d localInterpolated = MathUtils.interpolate(mc.thePlayer, event.getPartialTicks());
                Vec3d targetInterpolated = MathUtils.interpolate(entity, event.getPartialTicks());

                // render line vertices
                GL11.glVertex3d(
                        localInterpolated.getX() - mc.getRenderManager().viewerPosX,
                        localInterpolated.getY() - mc.getRenderManager().viewerPosY,
                        localInterpolated.getZ() - mc.getRenderManager().viewerPosZ
                );
                GL11.glVertex3d(
                        targetInterpolated.getX() - mc.getRenderManager().viewerPosX,
                        targetInterpolated.getY() - mc.getRenderManager().viewerPosY,
                        targetInterpolated.getZ() - mc.getRenderManager().viewerPosZ
                );

                GL11.glEnd();
            });

            // gl caps
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            GL11.glPopMatrix();
        }
    };

    Color getColor(int index) {
        if (this.colorMode.getValue() == ColorMode.RAINBOW) {
            return ColorUtils.rainbow(index * 20, 0.7f, 3.5);
        }
        return this.firstColor.getValue();
    }

    @AllArgsConstructor
    enum ColorMode {
        CUSTOM("Custom"),
        RAINBOW("Rainbow");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
