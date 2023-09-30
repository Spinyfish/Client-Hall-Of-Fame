package dev.tenacity.module.impl.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ColorSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.ESPUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ChinaHat extends Module {

    private final BooleanSetting firstPerson = new BooleanSetting("Show in first person", false);
    private final BooleanSetting allPlayers = new BooleanSetting("All players", false);
    private final ModeSetting colorMode = new ModeSetting("Color Mode", "Sync", "Sync", "Custom");
    private final ColorSetting color = new ColorSetting("Color", Color.WHITE);

    public ChinaHat() {
        super("ChinaHat", Category.RENDER, "epic hat");
        color.addParent(colorMode, modeSetting -> modeSetting.is("Custom"));
        this.addSettings(firstPerson, allPlayers, colorMode, color);
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;


        float partialTicks = event.getTicks();

        double renderPosX = mc.getRenderManager().renderPosX, renderPosY = mc.getRenderManager().renderPosY, renderPosZ = mc.getRenderManager().renderPosZ;

        glShadeModel(GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            boolean self = player == mc.thePlayer;
            if ((!allPlayers.isEnabled() && !self) || (self && !firstPerson.isEnabled() && mc.gameSettings.thirdPersonView == 0)
                    || player.isDead || player.isInvisible() || (!self && (!ESPUtil.isInView(player) || !mc.thePlayer.canEntityBeSeen(player))))
                continue;

            glPushMatrix();

            double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - renderPosX,
                    posY = player.lastTickPosY + (player.posY - player.lastTickPosY ) * partialTicks - renderPosY,
                    posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - renderPosZ;

            AxisAlignedBB bb = player.getEntityBoundingBox();

            double height = bb.maxY - bb.minY + 0.02, radius = bb.maxX - bb.minX;

            float yaw = MathUtils.interpolate(player.prevRotationYawHead, player.rotationYawHead, partialTicks).floatValue();
            if (Tenacity.INSTANCE.isEnabled(KillAura.class) || Tenacity.INSTANCE.isEnabled(Scaffold.class)) {
                yaw = MathUtils.interpolate(MathHelper.wrapAngleTo180_float(player.prevRotationYawHead),
                        MathHelper.wrapAngleTo180_float(player.rotationYawHead), partialTicks).floatValue();
            }


            float pitch = MathUtils.interpolate(player.prevRotationPitchHead, player.rotationPitchHead, partialTicks).floatValue();

            glTranslated(0, posY + height, 0);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            if (self) glRotated(yaw, 0, -1, 0);
            glRotated(pitch / 3.0, 1, 0, 0);
            glTranslated(0, 0, pitch / 270.0);
            glLineWidth(2);
            glBegin(GL_LINE_LOOP);

            // outline/border or whatever you call it
            for (int i = 0; i <= 180; i++) {
                int color1 = getColor(i * 4, .5f).getRGB();
                RenderUtil.color(color1);
                glVertex3d(
                        posX - Math.sin(i * MathHelper.PI2 / 90) * radius,
                        -(player.isSneaking() ? 0.2 : 0) - 0.002,
                        posZ + Math.cos(i * MathHelper.PI2 / 90) * radius
                );
            }
            glEnd();

            glBegin(GL_TRIANGLE_FAN);
            int color12 = getColor(4, .7f).getRGB();
            RenderUtil.color(color12);
            glVertex3d(posX, 0.3 - (player.isSneaking() ? 0.23 : 0), posZ);

            // draw hat
            for (int i = 0; i <= 180; i++) {
                int color1 = getColor(i * 4, .2f).getRGB();
                RenderUtil.color(color1);
                glVertex3d(posX - Math.sin(i * MathHelper.PI2 / 90) * radius,
                        -(player.isSneaking() ? 0.23F : 0),
                        posZ + Math.cos(i * MathHelper.PI2 / 90) * radius
                );

            }
            glVertex3d(posX, 0.3 - (player.isSneaking() ? 0.23 : 0), posZ);
            glEnd();
            glPopMatrix();
        }

        RenderUtil.resetColor();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    private Color getColor(int index, float alpha) {
        Color returnColor;
        if (colorMode.is("Custom")) {
            returnColor = ColorUtil.rainbow(10, 1, 0.5F, 0.76F, 1.0F);
        } else {
            final Pair<Color, Color> colors = HUDMod.getClientColors();
            if (HUDMod.isRainbowTheme()) {
                returnColor = HUDMod.color1.getRainbow().getColor(index);
            } else {
                returnColor = ColorUtil.interpolateColorsBackAndForth(7, index, colors.getFirst(), colors.getSecond(), false);
            }
        }
        return ColorUtil.applyOpacity(returnColor, alpha);
    }

}
