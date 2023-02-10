/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 10.08.22, 01:36
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.events.Render3DEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.display.HUDFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Feature.Info(name = "JumpCircle", description = "Renders a Circle if u jump", category = Feature.Category.VISUAL)
public class JumpCircle extends Feature {

    /* settings */
    public final EnumSetting<ColorMode> colorMode = new EnumSetting<>("Color Mode", ColorMode.SYNC);
    public final ColorSetting customColor = new ColorSetting("Custom Color", Color.CYAN).addDependency(() -> colorMode.is(ColorMode.CUSTOM));
    public final NumberSetting rainbowDelay = new NumberSetting("Color Delay", 200, 50, 500, 1).addDependency(() -> colorMode.is(ColorMode.RAINBOW));
    public final NumberSetting radius = new NumberSetting("Radius", 1.5, 0.5, 3, 0.1);


    /* values */
    final HashMap<EntityPlayer, Boolean> wasAir = new HashMap<>();
    final ArrayList<Circle> circles = new ArrayList<>();
    final CopyOnWriteArrayList<Circle> circlesCopy = new CopyOnWriteArrayList<>();


    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        for (EntityPlayer player : MC.theWorld.playerEntities) {
            if (player == getPlayer())
                if (player.onGround) {
                    if (wasAir.containsKey(player) && wasAir.get(player)) {
                        wasAir.put(player, false);
                        circles.add(new Circle(player.getPositionVector(), 0, 255));
                    }
                } else {
                    wasAir.put(player, true);
                }
        }
    };


    @Handler
    public final Listener<Render3DEvent> render3DEventListener = event -> {
        final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);
        circlesCopy.clear();
        circlesCopy.addAll(circles);
        for (Circle circle : circlesCopy) {

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


            final Vec3 pos = circle.getPosition();
            final double y = pos.yCoord - MC.getRenderManager().renderPosY;
            circle.adjust(0.001);
            GL11.glPushMatrix();
            if (circle.radius <= radius.getValue()) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);

                if (circle.alpha > 0)
                    circle.alpha -= circle.getRadius() / radius.getValue() * 2;

                GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, circle.alpha / 255f);
                GL11.glLineWidth(4);
                GL11.glBegin(GL11.GL_LINE_LOOP);

                for (int i = 0; i < 360; i++) {
                    final Tuple<Double, Double> anglePosition = getCirclePosition3D(pos.xCoord, pos.zCoord, i, circle.radius);
                    final double x = anglePosition.getFirst() - MC.getRenderManager().renderPosX;
                    final double z = anglePosition.getSecond() - MC.getRenderManager().renderPosZ;
                    GL11.glVertex3d(x, y, z);
                }

                GL11.glEnd();
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1, 1, 1, 1);
            } else
                circles.remove(circle);

            GL11.glPopMatrix();
        }
    };

    public Tuple<Double, Double> getCirclePosition3D(double x, double z, double angle, double radius) {
        if (angle < 0) {
            final int distance = (int) (Math.max(Math.abs(angle) / 360, 1));
            angle = angle + 360 * distance;
        }
        angle %= 360;
        for (int i = 0; i < 360; i++) {
            if (i == (int) (angle)) {
                final double math = i * Math.PI / 180;
                return new Tuple<>(x - Math.sin(math) * radius, z + Math.cos(math) * radius);
            }
        }
        return new Tuple<>(x, z);
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

    @Getter
    @Setter
    @AllArgsConstructor
    static class Circle {
        private final Vec3 position;
        private double radius;
        private float alpha;

        public void adjust(double adjustment) {
            radius += adjustment;
        }
    }
}
