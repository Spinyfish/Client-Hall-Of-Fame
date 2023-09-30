package dev.echo.module.impl.render;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.utils.tuples.Pair;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.render.Render3DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.ColorSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.GLUtil;
import dev.echo.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright Alan Wood 2021
 * None of this code to be reused without my written permission
 * Intellectual Rights owned by Alan Wood
 */
public final class Breadcrumbs extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Rise", "Rise", "Line");
    private final NumberSetting particleAmount = new NumberSetting("Particle Amount", 15, 500, 1, 1);
    private final BooleanSetting seeThroughWalls = new BooleanSetting("Walls", true);
    private final ModeSetting colorMode = new ModeSetting("Color Mode", "Sync", "Sync", "Custom");
    private final ColorSetting color = new ColorSetting("Color", Color.WHITE);

    public Breadcrumbs() {
        super("Breadcrumbs", Category.RENDER, "shows where you've walked");
        color.addParent(colorMode, modeSetting -> modeSetting.is("Custom"));
        seeThroughWalls.addParent(mode, mode -> mode.getMode().equals("Rise"));
        addSettings(mode, particleAmount, seeThroughWalls, colorMode, color);
    }

    private final List<Vec3> path = new ArrayList<>();

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        if (e.isPre()) {
            if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }

            while (path.size() > particleAmount.getValue()) {
                path.remove(0);
            }
        }
    };

    @Link
    public Listener<Render3DEvent> render3DEventListener = e -> {
        int i = 0;

        Pair<Color, Color> colors = colorMode.is("Custom") ? Pair.of(color.getColor(), color.getAltColor()) : HUDMod.getClientColors();

        switch (mode.getMode()) {
            case "Rise":
                if (seeThroughWalls.isEnabled()) {
                    GlStateManager.disableDepth();
                }
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                for (final Vec3 v : path) {


                    i++;

                    boolean draw = true;

                    final double x = v.xCoord - mc.getRenderManager().renderPosX;
                    final double y = v.yCoord - mc.getRenderManager().renderPosY;
                    final double z = v.zCoord - mc.getRenderManager().renderPosZ;

                    final double distanceFromPlayer = mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1, v.zCoord);
                    int quality = (int) (distanceFromPlayer * 4 + 10);

                    if (quality > 350)
                        quality = 350;

                    if (i % 10 != 0 && distanceFromPlayer > 25) {
                        draw = false;
                    }

                    if (i % 3 == 0 && distanceFromPlayer > 15) {
                        draw = false;
                    }

                    if (draw) {

                        GL11.glPushMatrix();
                        GL11.glTranslated(x, y, z);

                        final float scale = 0.06f;
                        GL11.glScalef(-scale, -scale, -scale);

                        GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
                        GL11.glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);


                        Color c = ColorUtil.interpolateColorsBackAndForth(7, 3 + (i * 20), colors.getFirst(), colors.getSecond(), false);

                        if (colorMode.is("Custom") && color.isRainbow()) {
                            c = color.getRainbow().getColor(3 + (i * 20));
                        }


                        //  final Color c = ColorManager.rainbow( 3 +(i * 20), 7);
                        //  final Color c = new Color(ColorUtil.interpolate(color1, color2, (float)(Math.sin(Minecraft.getMinecraft().timer.renderPartialTicks + -(i / (float) path.size()) * 13) + 1) * 0.5f));
                        RenderUtil.drawFilledCircleNoGL(0, -2, 0.7, ColorUtil.applyOpacity(c.getRGB(), .6f), quality);

                        if (distanceFromPlayer < 4)
                            RenderUtil.drawFilledCircleNoGL(0, -2, 1.4, ColorUtil.applyOpacity(c.getRGB(), .25f), quality);

                        if (distanceFromPlayer < 20)
                            RenderUtil.drawFilledCircleNoGL(0, -2, 2.3, ColorUtil.applyOpacity(c.getRGB(), .15f), quality);


                        GL11.glScalef(0.8f, 0.8f, 0.8f);

                        GL11.glPopMatrix();

                    }

                }

                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                if (seeThroughWalls.isEnabled()) {
                    GlStateManager.enableDepth();
                }

                GL11.glColor3d(255, 255, 255);
                break;
            case "Line":
                renderLine(path, colors);
                break;
        }
    };

    @Override
    public void onEnable() {
        path.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        path.clear();
        super.onDisable();
    }

    public void renderLine(final List<Vec3> path) {
        renderLine(path, Pair.of(Color.WHITE));
    }

    public void renderLine(final List<Vec3> path, Pair<Color, Color> colors) {
        GlStateManager.disableDepth();

        RenderUtil.setAlphaLimit(0);
        RenderUtil.resetColor();
        GLUtil.setup2DRendering();
        GLUtil.startBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(3);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        boolean custom = colorMode.is("Custom") && this.color.isRainbow();
        int count = 0;
        int alpha = 200;
        int fadeOffset = 15;
        for (Vec3 v : path) {
            if (fadeOffset > count) {
                alpha = count * (200 / fadeOffset);
            }

            RenderUtil.resetColor();
            if (custom || HUDMod.isRainbowTheme()) {
                if (custom) {
                    RenderUtil.color(this.color.getRainbow().getColor(count * 2).getRGB(), alpha / 255f);
                } else {
                    RenderUtil.color(HUDMod.color1.getRainbow().getColor(count * 2).getRGB(), alpha / 255f);
                }
            } else {
                RenderUtil.color(ColorUtil.interpolateColorsBackAndForth(15, count * 5, colors.getFirst(), colors.getSecond(), false).getRGB(), alpha / 255f);
            }
            final double x = v.xCoord - mc.getRenderManager().renderPosX;
            final double y = v.yCoord - mc.getRenderManager().renderPosY;
            final double z = v.zCoord - mc.getRenderManager().renderPosZ;
            GL11.glVertex3d(x, y, z);
            count++;
        }

        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GLUtil.end2DRendering();

        GlStateManager.enableDepth();
    }

}
