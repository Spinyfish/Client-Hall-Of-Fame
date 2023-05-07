package client.util.player;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
    private static final Frustum frustrum = new Frustum();

    public static Color darker(Color color, float factor) {
        factor = MathHelper.clamp_float(factor, 0.001F, 0.999F);
        return new Color(Math.max((int)(color.getRed() * factor), 0), Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0), color.getAlpha());
    }

    public static int fadeBetween(int color1, int color2, float offset) {
        if (offset > 1.0F)
            offset = 1.0F - offset % 1.0F;
        double invert = (1.0F - offset);
        int r = (int)((color1 >> 16 & 0xFF) * invert + ((
                color2 >> 16 & 0xFF) * offset));
        int g = (int)((color1 >> 8 & 0xFF) * invert + ((
                color2 >> 8 & 0xFF) * offset));
        int b = (int)((color1 & 0xFF) * invert + ((
                color2 & 0xFF) * offset));
        int a = (int)((color1 >> 24 & 0xFF) * invert + ((
                color2 >> 24 & 0xFF) * offset));
        return (a & 0xFF) << 24 | (
                r & 0xFF) << 16 | (
                g & 0xFF) << 8 |
                b & 0xFF;
    }

    public static Color brighter(Color color, float factor) {
        factor = MathHelper.clamp_float(factor, 0.001F, 0.999F);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();
        int i = (int)(1.0D / (1.0D - factor));
        if (r == 0 && g == 0 && b == 0)
            return new Color(i, i, i, alpha);
        if (r > 0 && r < i)
            r = i;
        if (g > 0 && g < i)
            g = i;
        if (b > 0 && b < i)
            b = i;
        return new Color(Math.min((int)(r / factor), 255), Math.min((int)(g / factor), 255),
                Math.min((int)(b / factor), 255), alpha);
    }

    public static void scissorBox(int x, int y, int width, int height) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scaledResolution.getScaleFactor();
        GL11.glEnable(3089);
        GL11.glScissor(x * factor, (scaledResolution.getScaledHeight() - y + height) * factor, (
                x + width - x) * factor, (y + height - y) * factor);
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        glColor(color);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, (y + height));
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawFace(int x, int y, ResourceLocation resourceLocation) {
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GlStateManager.enableBlend();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8.0F, 8, 8, 31, 31, 64.0F, 64.0F);
        GlStateManager.disableBlend();
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        glColor(color);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2f((float)x + (float)width, (float)y + (float)height);
        GL11.glVertex2f((float)x + (float)width, (float)y);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawRoundedRect2(double x, double y, double width, double height, double radius, int color) {
        drawRoundedRect(x, y, width - x, height - y, radius, color);
    }

    public static void drawRect2(double x, double y, double width, double height, int color) {
        drawRect(x, y, width - x, height - y, color);
    }

    public static void drawVerticalGradient2(float x, float y, float width, float height, int bottomColor, int topColor) {
        drawVerticalGradient(x, y, width - x, height - y, topColor, bottomColor);
    }

    public static void drawHorizontalGardient2(float x, float y, float width, float height, int leftColor, int rightColor) {
        drawHorizontalGradient(x, y, width - x, height - y, leftColor, rightColor);
    }



    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x1 = x + width;
        double y1 = y + height;
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        GL11.glDisable(3553);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 3)
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void relativeRect(float left, float top, float right, float bottom, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        glColor(color);
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static final void polygonCircle(double x, double y, double sideLength, double degree, int color) {
        sideLength *= 0.5D;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GlStateManager.disableAlpha();
        glColor(color);
        GL11.glLineWidth(1.0F);
        GL11.glEnable(2848);
        GL11.glBegin(6);
        for (double i = 0.0D; i <= degree; i++) {
            double angle = i * 6.283185307179586D / degree;
            GL11.glVertex2d(x + sideLength * Math.cos(angle) + sideLength,
                    y + sideLength * Math.sin(angle) + sideLength);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableAlpha();
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawHorizontalGradient(float x, float y, float width, float height, int leftColor, int rightColor) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        glColor(leftColor);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, (y + height));
        glColor(rightColor);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawVerticalGradient(float x, float y, float width, float height, int topColor, int bottomColor) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        glColor(topColor);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        glColor(bottomColor);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static int getRainbow(int speed, int offset, float s) {
        float hue = (float)((System.currentTimeMillis() + offset) % speed);
        hue /= speed;
        return Color.getHSBColor(hue, s, 1.0F).getRGB();
    }



    public static int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x * factor), (int)((scale.getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), (int)((y2 - y) * factor));
    }

    public static void startDrawing() {
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        (Minecraft.getMinecraft()).entityRenderer.setupCameraTransform((Minecraft.getMinecraft()).timer.renderPartialTicks,
                0);
    }

    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return !(!isInViewFrustrum(entity.getEntityBoundingBox()) && !entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static double interpolateScale(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static double interpolate(double old, double now, float partialTicks) {
        return old + (now - old) * partialTicks;
    }

    public static float interpolate(float old, float now, float partialTicks) {
        return old + (now - old) * partialTicks;
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569F * redRGB;
        float green = 0.003921569F * greenRGB;
        float blue = 0.003921569F * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }


    public static void func_181662_bt3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (!GL11.glIsEnabled(2896))
            GL11.glEnable(2896);
    }



    public static void drawLines(AxisAlignedBB boundingBox) {
        GL11.glPushMatrix();
        GL11.glBegin(2);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glEnable(2848);
    }

    public static void post3D() {
        GL11.glDisable(2848);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GL11.glPopMatrix();
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        drawImage(image, x, y, width, height, 1.0F);
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height, float alpha) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height,
                width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void startSmooth() {
        GL11.glEnable(2848);
        GL11.glEnable(2881);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
    }

    public static void endSmooth() {
        GL11.glDisable(2848);
        GL11.glDisable(2881);
        GL11.glEnable(2832);
    }

    public static void drawCheckMark(float x, float y, int width, int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.5F);
        GL11.glBegin(3);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d((x + width) - 6.5D, (y + 3.0F));
        GL11.glVertex2d((x + width) - 11.5D, (y + 10.0F));
        GL11.glVertex2d((x + width) - 13.5D, (y + 8.0F));
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawCircle(double d, double e, float r, int c) {
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f2 = (c >> 16 & 0xFF) / 255.0F;
        float f3 = (c >> 8 & 0xFF) / 255.0F;
        float f4 = (c & 0xFF) / 255.0F;
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(2848);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(6);
        int i;
        for (i = 0; i <= 360; i++) {
            double x2 = Math.sin(i * Math.PI / 180.0D) * (r / 2.0F);
            double y2 = Math.cos(i * Math.PI / 180.0D) * (r / 2.0F);
            GL11.glVertex2d(d + (r / 2.0F) + x2, e + (r / 2.0F) + y2);
        }
        GL11.glEnd();
        GL11.glBegin(2);
        for (i = 0; i <= 360; i++) {
            double x2 = Math.sin(i * Math.PI / 180.0D) * (r / 2.0F);
            double y2 = Math.cos(i * Math.PI / 180.0D) * (r / 2.0F);
            GL11.glVertex2d(d + (r / 2.0F) + x2, e + (r / 2.0F) + y2);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static int drawHealth(EntityLivingBase entityLivingBase) {
        float health = entityLivingBase.getHealth();
        float maxHealth = entityLivingBase.getMaxHealth();
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(health, maxHealth) / maxHealth) / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    public static Color drawHealth(float health, float maxHealth) {
        float[] fractions = { 0.0F, 0.5F, 1.0F };
        Color[] colors = { new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN };
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress).brighter();
    }

    public static Color getGradientOffset(Color color1, Color color2, double offset) {
        if (offset > 1.0D) {
            double left = offset % 1.0D;
            int off = (int)offset;
            offset = (off % 2 == 0) ? left : (1.0D - left);
        }
        double inverse_percent = 1.0D - offset;
        int redPart = (int)(color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int)(color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int)(color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public static Color fade(Color color) {
        return fade(color, 2, 100);
    }

    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + index / count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = { fractions[indices[0]], fractions[indices[1]] };
            Color[] colorRange = { colors[indices[0]], colors[indices[1]] };
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            Color color = blend(colorRange[0], colorRange[1], (1.0F - weight));
            return color;
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static int[] getFractionIndices(float[] fractions, float progress) {
        int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; startPoint++);
        if (startPoint >= fractions.length)
            startPoint = fractions.length - 1;
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0F - r;
        float[] rgb1 = color1.getColorComponents(new float[3]);
        float[] rgb2 = color2.getColorComponents(new float[3]);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0F) {
            red = 0.0F;
        } else if (red > 255.0F) {
            red = 255.0F;
        }
        if (green < 0.0F) {
            green = 0.0F;
        } else if (green > 255.0F) {
            green = 255.0F;
        }
        if (blue < 0.0F) {
            blue = 0.0F;
        } else if (blue > 255.0F) {
            blue = 255.0F;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException illegalArgumentException) {}
        return color3;
    }



    public static void startBlending() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }

    public static void endBlending() {
        GL11.glDisable(3042);
    }

    public static float[] getRGBAs(int rgb) {
        return new float[] { (rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, (
                rgb >> 24 & 0xFF) / 255.0F };
    }

    public static double animate(double target, double current, double speed) {
        boolean larger = (target > current);
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D)
            factor = 0.1D;
        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }
        return current;
    }

    public static void vertex(double x, double y) {
        GL11.glVertex2d(x, y);
    }

    public static void begin(int glMode) {
        GL11.glBegin(glMode);
    }

    public static void enable(int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void disable(int glTarget) {
        GL11.glDisable(glTarget);
    }

    public static void start() {
        enable(3042);
        GL11.glBlendFunc(770, 771);
        disable(3553);
        disable(2884);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void polygon(double x, double y, double sideLength, double amountOfSides, boolean filled, Color color) {
        sideLength /= 2.0D;
        start();
        if (color != null)
            color(color);
        if (!filled)
            GL11.glLineWidth(2.0F);
        GL11.glEnable(2848);
        begin(filled ? 6 : 3);
        for (double i = 0.0D; i <= amountOfSides / 4.0D; i++) {
            double angle = i * 4.0D * 6.283185307179586D / 360.0D;
            vertex(x + sideLength * Math.cos(angle) + sideLength, y + sideLength * Math.sin(angle) + sideLength);
        }
        end();
        GL11.glDisable(2848);
        stop();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        enable(2884);
        enable(3553);
        disable(3042);
        color(Color.white);
    }

    public void polygon(double x, double y, double sideLength, int amountOfSides, boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public static void polygon(double x, double y, double sideLength, int amountOfSides, Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygon(double x, double y, double sideLength, int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(double red, double green, double blue) {
        color(red, green, blue, 1.0D);
    }

    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color((color.getRed() / 255.0F), (color.getGreen() / 255.0F), (color.getBlue() / 255.0F), (color.getAlpha() / 255.0F));
    }

    public void color(Color color, int alpha) {
        if (color == null)
            color = Color.white;
        color((color.getRed() / 255.0F), (color.getGreen() / 255.0F), (color.getBlue() / 255.0F), 0.5D);
    }

    public void circle(double x, double y, double radius, boolean filled, Color color) {
        polygon(x, y, radius, 360.0D, filled, color);
    }

    public void circle(double x, double y, double radius, boolean filled) {
        polygon(x, y, radius, 360, filled);
    }

    public static void circle(double x, double y, double radius, Color color) {
        polygon(x, y, radius, 360, color);
    }

    public void circle(double x, double y, double radius) {
        polygon(x, y, radius, 360);
    }

    public static void roundedRectCustom(double x, double y, double width, double height, double edgeRadius, Color color, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        double halfRadius = edgeRadius / 2.0D;
        width -= halfRadius;
        height -= halfRadius;
        float sideLength = (float)edgeRadius;
        sideLength /= 2.0F;
        start();
        if (color != null)
            color(color);
        if (topLeft) {
            GL11.glEnable(2848);
            begin(6);
            for (double i = 180.0D; i <= 270.0D; i++) {
                double angle = i * 6.283185307179586D / 360.0D;
                vertex(x + sideLength * Math.cos(angle) + sideLength, y + sideLength * Math.sin(angle) + sideLength);
            }
            vertex(x + sideLength, y + sideLength);
            end();
            GL11.glDisable(2848);
            stop();
        } else {
            rect(x, y, sideLength, sideLength, color);
        }
        sideLength = (float)edgeRadius;
        sideLength /= 2.0F;
        start();
        if (color != null)
            color(color);
        if (bottomRight) {
            GL11.glEnable(2848);
            begin(6);
            for (double i = 0.0D; i <= 90.0D; i++) {
                double angle = i * 6.283185307179586D / 360.0D;
                vertex(x + width + sideLength * Math.cos(angle), y + height + sideLength * Math.sin(angle));
            }
            vertex(x + width, y + height);
            end();
            GL11.glDisable(2848);
            stop();
        } else {
            rect(x + width, y + height, sideLength, sideLength, color);
        }
        sideLength = (float)edgeRadius;
        sideLength /= 2.0F;
        start();
        if (color != null)
            color(color);
        if (topRight) {
            GL11.glEnable(2848);
            begin(6);
            for (double i = 270.0D; i <= 360.0D; i++) {
                double angle = i * 6.283185307179586D / 360.0D;
                vertex(x + width + sideLength * Math.cos(angle), y + sideLength * Math.sin(angle) + sideLength);
            }
            vertex(x + width, y + sideLength);
            end();
            GL11.glDisable(2848);
            stop();
        } else {
            rect(x + width, y, sideLength, sideLength, color);
        }
        sideLength = (float)edgeRadius;
        sideLength /= 2.0F;
        start();
        if (color != null)
            color(color);
        if (bottomLeft) {
            GL11.glEnable(2848);
            begin(6);
            for (double i = 90.0D; i <= 180.0D; i++) {
                double angle = i * 6.283185307179586D / 360.0D;
                vertex(x + sideLength * Math.cos(angle) + sideLength, y + height + sideLength * Math.sin(angle));
            }
            vertex(x + sideLength, y + height);
            end();
            GL11.glDisable(2848);
            stop();
        } else {
            rect(x, y + height, sideLength, sideLength, color);
        }
        rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color);
        rect(x, y + halfRadius, edgeRadius / 2.0D, height - halfRadius, color);
        rect(x + width, y + halfRadius, edgeRadius / 2.0D, height - halfRadius, color);
        rect(x + halfRadius, y, width - halfRadius, halfRadius, color);
        rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color);
    }

    public void rect(double x, double y, double width, double height, boolean filled) {
        rect(x, y, width, height, filled, null);
    }

    public static void rect(double x, double y, double width, double height, Color color) {
        rect(x, y, width, height, true, color);
    }

    public void rect(double x, double y, double width, double height) {
        rect(x, y, width, height, true, null);
    }

    public void rectCentered(double x, double y, double width, double height, boolean filled, Color color) {
        x -= width / 2.0D;
        y -= height / 2.0D;
        rect(x, y, width, height, filled, color);
    }

    public void rectCentered(double x, double y, double width, double height, boolean filled) {
        x -= width / 2.0D;
        y -= height / 2.0D;
        rect(x, y, width, height, filled, null);
    }

    public void rectCentered(double x, double y, double width, double height, Color color) {
        x -= width / 2.0D;
        y -= height / 2.0D;
        rect(x, y, width, height, true, color);
    }

    public void rectCentered(double x, double y, double width, double height) {
        x -= width / 2.0D;
        y -= height / 2.0D;
        rect(x, y, width, height, true, null);
    }

    public static void rect(double x, double y, double width, double height, boolean filled, Color color) {
        start();
        if (color != null)
            color(color);
        begin(filled ? 6 : 1);
        vertex(x, y);
        vertex(x + width, y);
        vertex(x + width, y + height);
        vertex(x, y + height);
        if (!filled) {
            vertex(x, y);
            vertex(x, y + height);
            vertex(x + width, y);
            vertex(x + width, y + height);
        }
        end();
        stop();
    }

    public static void resetColor() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }



    public static void drawFullCircle(double cx, double cy, double r, int c) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        r *= 2.0D;
        cx *= 2.0D;
        cy *= 2.0D;
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f1 = (c >> 16 & 0xFF) / 255.0F;
        float f2 = (c >> 8 & 0xFF) / 255.0F;
        float f3 = (c & 0xFF) / 255.0F;
        boolean blend = GL11.glIsEnabled(3042);
        boolean texture2d = GL11.glIsEnabled(3553);
        boolean line = GL11.glIsEnabled(2848);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(6);
        for (int i = 0; i <= 360; i++) {
            double x = Math.sin(i * Math.PI / 180.0D) * r;
            double y = Math.cos(i * Math.PI / 180.0D) * r;
            GL11.glVertex2d(cx + x, cy + y);
        }
        GL11.glEnd();
        f = (c >> 24 & 0xFF) / 255.0F;
        f1 = (c >> 16 & 0xFF) / 255.0F;
        f2 = (c >> 8 & 0xFF) / 255.0F;
        f3 = (c & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
        if (!line)
            GL11.glDisable(2848);
        if (texture2d)
            GL11.glEnable(3553);
        if (!blend)
            GL11.glDisable(3042);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }



    public static boolean isHovered(int mouseX, int mouseY, int i, int j, double d, double e) {
        return false;
    }
}
