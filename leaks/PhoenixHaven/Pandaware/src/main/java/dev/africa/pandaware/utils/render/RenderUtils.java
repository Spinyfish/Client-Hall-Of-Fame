package dev.africa.pandaware.utils.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.List;


@UtilityClass
public class RenderUtils implements MinecraftInstance {
    private final HashMap<Integer, Integer> shadowCache = new HashMap<>();

    public double fpsMultiplier() {
        return (Client.getInstance().getRenderDeltaTime() / 60.0) * 3;
    }

    public void drawHorizontalGradientRect(double x, double y, double width, double height, Color right, Color left) {
        GlStateManager.pushAttribAndMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtils.glColor(right);
        GL11.glVertex2d(x, y + height);

        ColorUtils.glColor(left);
        GL11.glVertex2d(x + width, y + height);

        ColorUtils.glColor(left);
        GL11.glVertex2d(x + width, y);

        ColorUtils.glColor(right);
        GL11.glVertex2d(x, y);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popAttribAndMatrix();
    }

    public void drawLines(List<Vec3> list) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        mc.entityRenderer.disableLightmap();

        GL11.glLineWidth(1.2f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        for (Vec3 vector : list) {
            GL11.glVertex3d(vector.xCoord - mc.getRenderManager().viewerPosX, vector.yCoord
                    - mc.getRenderManager().viewerPosY, vector.zCoord - mc.getRenderManager().viewerPosZ);
        }

        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public void drawVerticalGradientRect(double x, double y, double width, double height, Color top, Color bottom) {
        GlStateManager.pushAttribAndMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtils.glColor(bottom);
        GL11.glVertex2d(x, y + height);

        ColorUtils.glColor(bottom);
        GL11.glVertex2d(x + width, y + height);

        ColorUtils.glColor(top);
        GL11.glVertex2d(x + width, y);

        ColorUtils.glColor(top);
        GL11.glVertex2d(x, y);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popAttribAndMatrix();
    }

    public void drawRainbowRect(double x, double y, double x2, double y2, int wave, float saturation, double speed) {
        for (int i = 0; i < x2 - x; ++i) {
            drawRect(x + i, y, x + i + 1, y2, ColorUtils.rainbow(i * wave, saturation, speed).getRGB());
        }
    }

    public void renderSkinHead(EntityLivingBase player, double x, double y, int size) {
        renderSkinHead(player, x, y, size, Color.WHITE);
    }

    public void renderSkinHead(EntityLivingBase player, double x, double y, int size, Color color) {
        if (!(player instanceof EntityPlayer))
            return;

        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

            Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 8, 8, 8, 8, size, size, 64, 64);
            GL11.glPopMatrix();
        } catch (Exception ignored) {
        }
    }

    public void renderItemOnScreenNoDepth(ItemStack item, int x, int y) {
        GlStateManager.pushMatrix();
        RenderItem itemRenderer = mc.ingameGUI.itemRenderer;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableDepth();
        itemRenderer.renderItemAndEffectIntoGUI(item, x, y);
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, item, x, y, "");
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void drawImage(ResourceLocation location, double x, double y, double width, double height, int alpha) {
        drawImage(location, x, y, width, height, new Color(255, 255, 255, alpha));
    }

    public void drawImage(ResourceLocation location, double x, double y, double width, double height) {
        drawImage(location, x, y, width, height, Color.WHITE);
    }

    public void drawImage(ResourceLocation location, double x, double y, double width, double height, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        ColorUtils.glColor(color);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(location);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }

    public void drawRect(double x, double y, double x2, double y2, int color) {
        GlStateManager.color(1, 1, 1, 1);
        Gui.drawRect(x, y, x2, y2, color);
    }

    public void preRenderShade() {
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.disableCull();
    }

    public void postRenderShade() {
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    public void startScissorBox() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public void drawScissorBox(double x, double y, double width, double height) {
        width = Math.max(width, 0.1);

        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public void endScissorBox() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public void drawCircleArc(double x, double y, double width, double height, Color color) {
        drawArc(x + width / 2f, y + height / 2f, width / 2f, 0, 360f, color);
    }

    public double ticks = 0;
    public long lastFrame = 0;

    public void drawCircle(Entity entity, double rad, int color, boolean shade) {
        ticks += .004 * (System.currentTimeMillis() - lastFrame);

        lastFrame = System.currentTimeMillis();

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();

        Color c = new Color(color);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
                * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosX;
        double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
                * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosY) + Math.sin(ticks) + 1;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
                * Minecraft.getMinecraft().timer.renderPartialTicks - RenderManager.renderPosZ;

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        double TAU = Math.PI * 2.D;
        for (float i = 0; i < TAU; i += TAU / 64.F) {

            double vecX = x + rad * Math.cos(i);
            double vecZ = z + rad * Math.sin(i);

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.05F
                );

                GL11.glVertex3d(vecX, y - Math.sin(ticks + 1) / 2.7f, vecZ);
            }

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.56F
            );

            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();

        GlStateManager.pushAttribAndMatrix();
        GL11.glPushMatrix();
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glLineWidth(2);
        ColorUtils.glColor(new Color(color));
        GL11.glBegin(1);
        for (int i = 0; i <= 90; ++i) {
            ColorUtils.glColor(new Color(color));
            GL11.glVertex3d(x + rad * Math.cos((double) i * (Math.PI * 2) / 45), y, z + rad * Math.sin((double) i * (Math.PI * 2) / 45));
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        mc.entityRenderer.enableLightmap();
        GL11.glPopMatrix();
        GlStateManager.popAttribAndMatrix();
    }

    public void drawRoundedRect(double x, double y, double width, double height, double cornerRadius, Color color) {
        drawRect(x, y + cornerRadius, x + cornerRadius, y + height - cornerRadius, color.getRGB());
        drawRect(x + cornerRadius, y, x + width - cornerRadius, y + height, color.getRGB());
        drawRect(x + width - cornerRadius, y + cornerRadius, x + width, y + height - cornerRadius, color.getRGB());
        drawArc(x + cornerRadius, y + cornerRadius, cornerRadius, 0, 90, color);
        drawArc(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270, 360, color);
        drawArc(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 180, 270, color);
        drawArc(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90, 180, color);
    }

    public void drawRoundedRectOutline(double x, double y, double width, double height, double cornerRadius, Color color) {
        drawRect(x - 0.5, y + cornerRadius, x + 0.5, y + height - cornerRadius, color.getRGB());
        drawRect(x + width - 0.5, y + cornerRadius, x + width + 0.5, y + height - cornerRadius, color.getRGB());

        drawRect(x + cornerRadius, y - 0.5, x + width - cornerRadius, y + 0.5, color.getRGB());
        drawRect(x + cornerRadius, y + height - 0.5, x + width - cornerRadius, y + height + 0.5, color.getRGB());

        drawArcOutline(x + cornerRadius, y + cornerRadius, cornerRadius, 0, 90, 2, color);
        drawArcOutline(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270, 360, 2, color);
        drawArcOutline(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 180, 270, 2, color);
        drawArcOutline(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90, 180, 2, color);
    }

    public void drawArc(double x, double y, double radius, double startAngle, double endAngle, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);

        VertexUtils.start(6);
        VertexUtils.add(x, y, color);

        for (double i = (startAngle / 360.0 * 100); i <= (endAngle / 360.0 * 100); i++) {
            double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
            VertexUtils.add(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, color);
        }

        VertexUtils.end();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public void drawCircleOutline(double x, double y, double width, double height, float lineWidth, Color color) {
        drawArcOutline(x + width / 2f, y + height / 2f, width / 2f, 0, 360f, lineWidth, color);
    }

    public void drawCheck(double x, double y, Color color) {
        GlStateManager.pushMatrix();
        RenderUtils.preRenderShade();
        GL11.glLineWidth(2);

        VertexUtils.start(GL11.GL_LINE_STRIP);

        VertexUtils.add(x + 1, y, color);
        VertexUtils.add(x + 3, y + 3.5, color);
        VertexUtils.add(x + 7, y - 2.5, color);

        VertexUtils.end();

        RenderUtils.postRenderShade();
        GlStateManager.popMatrix();
    }

    public void drawArcOutline(double x, double y, double radius, double startAngle, double endAngle, float lineWidth, Color color) {
        GlStateManager.pushMatrix();
        RenderUtils.preRenderShade();
        GL11.glLineWidth(lineWidth);

        VertexUtils.start(GL11.GL_LINE_STRIP);

        for (double i = (startAngle / 360.0 * 100); i <= (endAngle / 360.0 * 100); i++) {
            double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
            VertexUtils.add(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, color);
        }

        VertexUtils.end();

        RenderUtils.postRenderShade();
        GlStateManager.popMatrix();
    }

    public void drawBorderedRect(double x, double y,
                                 double x1, double y1, double width, int internalColor, int borderColor) {
        drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        drawRect(x + width, y, x1 - width, y + width, borderColor);
        drawRect(x, y, x + width, y1, borderColor);
        drawRect(x1 - width, y, x1, y1, borderColor);
        drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
    }
    public void bindFrameBuffer(double x, double y, double width, double height, Framebuffer framebuffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        drawQuad(x, y, width, height);
    }

    public void drawQuad(double x, double y, double width, double height) {
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(x, y);

        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(x, y + height);

        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(x + width, y + height);

        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(x + width, y);

        GL11.glEnd();
    }

    public Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth
                || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }

        return framebuffer;
    }

    public void preLight() {
        mc.entityRenderer.disableLightmap();
    }

    public void postLight() {
        mc.entityRenderer.enableLightmap();
    }


}
