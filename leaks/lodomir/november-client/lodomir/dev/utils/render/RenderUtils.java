/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.utils.render;

import java.awt.Color;
import lodomir.dev.utils.math.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

public final class RenderUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Frustum frustrum = new Frustum();

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return RenderUtils.isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static void start(int mode) {
        GL11.glBegin((int)mode);
    }

    public static void add(double x, double y, Color color) {
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        GL11.glVertex2d((double)x, (double)y);
    }

    public static double transition(double now, double desired, double speed) {
        double dif = Math.abs(now - desired);
        int fps = Minecraft.getDebugFPS();
        if (dif > 0.0) {
            double animationSpeed = MathUtils.roundToDecimalPlace(Math.min(10.0, Math.max(0.0625, 144.0 / (double)fps * (dif / 10.0) * speed)), 0.0625);
            if (dif != 0.0 && dif < animationSpeed) {
                animationSpeed = dif;
            }
            if (now < desired) {
                return now + animationSpeed;
            }
            if (now > desired) {
                return now - animationSpeed;
            }
        }
        return now;
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d((double)red, (double)green, (double)blue, (double)alpha);
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }

    public void color(double red, double green, double blue) {
        RenderUtils.color(red, green, blue, 1.0);
    }

    public static void color(Color color) {
        if (color == null) {
            color = Color.white;
        }
        RenderUtils.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public void color(Color color, int alpha) {
        if (color == null) {
            color = Color.white;
        }
        RenderUtils.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 0.5);
    }

    public static void startBlending() {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
    }

    public static void endBlending() {
        GL11.glDisable((int)3042);
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        if (x < width) {
            double i = x;
            x = width;
            width = i;
        }
        if (y < height) {
            double j = y;
            y = height;
            height = j;
        }
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        Gui.drawRect(x, y, width, height, color);
    }

    public static void drawRect2(double x, double y, double x2, double y2, int color) {
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        Gui.drawRect((int)x, (int)y, (int)x2, (int)y2, color);
    }

    public static void drawCircle(double x, double y, float radius, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glBegin((int)9);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d((double)(x + Math.sin((double)i * 3.141526 / 180.0) * (double)radius), (double)(y + Math.cos((double)i * 3.141526 / 180.0) * (double)radius));
        }
        GL11.glEnd();
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        int i;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x2 = x + width;
        double y2 = y + height;
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(color & 0xFF) / 255.0f;
        GL11.glPushAttrib((int)0);
        GL11.glScaled((double)0.5, (double)0.5, (double)0.5);
        x *= 2.0;
        y *= 2.0;
        x2 *= 2.0;
        y2 *= 2.0;
        GL11.glDisable((int)3553);
        GL11.glColor4f((float)f2, (float)f3, (float)f4, (float)f);
        GL11.glEnable((int)2848);
        GL11.glBegin((int)9);
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d((double)(x + radius + Math.sin((double)i * Math.PI / 180.0) * (radius * -1.0)), (double)(y + radius + Math.cos((double)i * Math.PI / 180.0) * (radius * -1.0)));
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d((double)(x + radius + Math.sin((double)i * Math.PI / 180.0) * (radius * -1.0)), (double)(y2 - radius + Math.cos((double)i * Math.PI / 180.0) * (radius * -1.0)));
        }
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d((double)(x2 - radius + Math.sin((double)i * Math.PI / 180.0) * radius), (double)(y2 - radius + Math.cos((double)i * Math.PI / 180.0) * radius));
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d((double)(x2 - radius + Math.sin((double)i * Math.PI / 180.0) * radius), (double)(y + radius + Math.cos((double)i * Math.PI / 180.0) * radius));
        }
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glScaled((double)2.0, (double)2.0, (double)2.0);
        GL11.glPopAttrib();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedCircle(double x, double y, float radius, int outsideC, int insideC) {
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        float scale = 0.1f;
        GL11.glScalef((float)0.1f, (float)0.1f, (float)0.1f);
        RenderUtils.drawCircle(x * 10.0, y * 10.0, radius * 10.0f, insideC);
        GL11.glScalef((float)10.0f, (float)10.0f, (float)10.0f);
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2848);
    }

    public static void color(int color) {
        float red = (float)(color & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color >> 16 & 0xFF) / 255.0f;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }

    public static void drawAxisAlignedBBFilled(AxisAlignedBB axisAlignedBB, int color, boolean depth) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        if (depth) {
            GL11.glDisable((int)2929);
        }
        GL11.glDepthMask((boolean)false);
        RenderUtils.color(color);
        RenderUtils.drawBoxFilled(axisAlignedBB);
        GlStateManager.resetColor();
        GL11.glEnable((int)3553);
        if (depth) {
            GL11.glEnable((int)2929);
        }
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
    }

    public void drawRoundedRect(double x, double y, double width, double height, double cornerRadius, Color color) {
        RenderUtils.drawRect(x, y + cornerRadius, x + cornerRadius, y + height - cornerRadius, color.getRGB());
        RenderUtils.drawRect(x + cornerRadius, y, x + width - cornerRadius, y + height, color.getRGB());
        RenderUtils.drawRect(x + width - cornerRadius, y + cornerRadius, x + width, y + height - cornerRadius, color.getRGB());
        this.drawArc(x + cornerRadius, y + cornerRadius, cornerRadius, 0.0, 90.0, color);
        this.drawArc(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270.0, 360.0, color);
        this.drawArc(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 180.0, 270.0, color);
        this.drawArc(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90.0, 180.0, color);
    }

    public static void drawRoundedRectOutline(double x, double y, double width, double height, double cornerRadius, Color color) {
        RenderUtils.drawRect(x - 0.5, y + cornerRadius, x + 0.5, y + height - cornerRadius, color.getRGB());
        RenderUtils.drawRect(x + width - 0.5, y + cornerRadius, x + width + 0.5, y + height - cornerRadius, color.getRGB());
        RenderUtils.drawRect(x + cornerRadius, y - 0.5, x + width - cornerRadius, y + 0.5, color.getRGB());
        RenderUtils.drawRect(x + cornerRadius, y + height - 0.5, x + width - cornerRadius, y + height + 0.5, color.getRGB());
        RenderUtils.drawArcOutline(x + cornerRadius, y + cornerRadius, cornerRadius, 0.0, 90.0, 2.0f, color);
        RenderUtils.drawArcOutline(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270.0, 360.0, 2.0f, color);
        RenderUtils.drawArcOutline(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 180.0, 270.0, 2.0f, color);
        RenderUtils.drawArcOutline(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90.0, 180.0, 2.0f, color);
    }

    public void drawArc(double x, double y, double radius, double startAngle, double endAngle, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        RenderUtils.start(6);
        RenderUtils.add(x, y, color);
        for (double i = startAngle / 360.0 * 100.0; i <= endAngle / 360.0 * 100.0; i += 1.0) {
            double angle = Math.PI * 2 * i / 100.0 + Math.toRadians(180.0);
            RenderUtils.add(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, color);
        }
        RenderUtils.end();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void preRenderShade() {
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.disableCull();
    }

    public static void postRenderShade() {
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GL11.glDisable((int)2848);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    public static void drawArcOutline(double x, double y, double radius, double startAngle, double endAngle, float lineWidth, Color color) {
        GlStateManager.pushMatrix();
        RenderUtils.preRenderShade();
        GL11.glLineWidth((float)lineWidth);
        RenderUtils.start(3);
        for (double i = startAngle / 360.0 * 100.0; i <= endAngle / 360.0 * 100.0; i += 1.0) {
            double angle = Math.PI * 2 * i / 100.0 + Math.toRadians(180.0);
            RenderUtils.add(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, color);
        }
        RenderUtils.end();
        RenderUtils.postRenderShade();
        GlStateManager.popMatrix();
    }

    public static void drawBox(BlockPos pos, int color, boolean depth) {
        RenderManager renderManager = mc.getRenderManager();
        Timer timer = RenderUtils.mc.timer;
        double x = (double)pos.getX() - renderManager.renderPosX;
        double y = (double)pos.getY() - renderManager.renderPosY;
        double z = (double)pos.getZ() - renderManager.renderPosZ;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Block block = RenderUtils.mc.theWorld.getBlockState(pos).getBlock();
        if (block != null) {
            EntityPlayerSP player = RenderUtils.mc.thePlayer;
            double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)timer.renderPartialTicks;
            double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)timer.renderPartialTicks;
            double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)timer.renderPartialTicks;
            axisAlignedBB = block.getSelectedBoundingBox(RenderUtils.mc.theWorld, pos).expand(0.002, 0.002, 0.002).offset(-posX, -posY, -posZ);
            RenderUtils.drawAxisAlignedBBFilled(axisAlignedBB, color, depth);
        }
    }

    public static void drawBoxFilled(AxisAlignedBB axisAlignedBB) {
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glEnd();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer vertexbuffer = tessellator.getWorldRenderer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static double[] interpolate(Entity entity) {
        double partialTicks = RenderUtils.mc.timer.renderPartialTicks;
        return new double[]{entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks};
    }

    public static void blockEsp(BlockPos blockPos, double red, double green, double blue, float thickness) {
        double x = (double)blockPos.getX() - RenderUtils.mc.getRenderManager().renderPosX;
        double y = (double)blockPos.getY() - RenderUtils.mc.getRenderManager().renderPosY;
        double z = (double)blockPos.getZ() - RenderUtils.mc.getRenderManager().renderPosZ;
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glLineWidth((float)thickness);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4d((double)red, (double)green, (double)blue, (double)13.0);
        GL11.glColor4d((double)red, (double)green, (double)blue, (double)1.0);
        RenderUtils.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
    }

    public static void drawImg(ResourceLocation loc, int posX, int posY, int width, int height) {
        GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        RenderUtils.enable(3042);
        GlStateManager.disableAlpha();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        mc.getTextureManager().bindTexture(loc);
        Gui.drawModalRectWithCustomSizedTexture(posX, posY, 0.0f, 0.0f, width, height, width, height);
        GlStateManager.enableAlpha();
        RenderUtils.disable(3042);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawHsvScale(double left, double top, double right, double bottom) {
        float width = (float)(right - left);
        for (float i = 0.0f; i < width; i += 1.0f) {
            double posX = left + (double)i;
            int color = Color.getHSBColor(i / width, 1.0f, 1.0f).getRGB();
            Gui.drawRect(posX, top, posX + 1.0, bottom, color);
        }
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)((int)(x * (float)factor)), (int)((int)(((float)scale.getScaledHeight() - y2) * (float)factor)), (int)((int)((x2 - x) * (float)factor)), (int)((int)((y2 - y) * (float)factor)));
    }

    public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth, int insideColor, int borderColor) {
        Gui.drawRect(left - borderWidth, top - borderWidth, right + borderWidth, bottom + borderWidth, borderColor);
        Gui.drawRect(left, top, right, bottom, insideColor);
    }

    public static void drawRoundedBorderedRect(int left, int top, int right, int bottom, int radius, double borderWidth, int insideColor, int borderColor) {
        RenderUtils.drawRoundedRect((double)left - borderWidth, (double)top - borderWidth, (double)right + borderWidth, (double)bottom + borderWidth, radius, borderColor);
        RenderUtils.drawRoundedRect(left, top, right, bottom, radius, insideColor);
    }

    public static void drawBorder(double left, double top, double width, double height, double lineWidth, int color) {
        Gui.drawRect(left, top, left + width, top + lineWidth, color);
        Gui.drawRect(left, top, left + lineWidth, top + height, color);
        Gui.drawRect(left, top + height - lineWidth, left + width, top + height, color);
        Gui.drawRect(left + width - lineWidth, top, left + width, top + height, color);
    }

    public static void startSmooth() {
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2881);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glHint((int)3153, (int)4354);
    }

    public static void endSmooth() {
        GL11.glDisable((int)2848);
        GL11.glDisable((int)2881);
        GL11.glEnable((int)2832);
    }

    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + (float)index / (float)count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();
        y = (double)sr.getScaledHeight() - y;
        GL11.glScissor((int)((int)(x *= scale)), (int)((int)((y *= scale) - (height *= scale))), (int)((int)(width *= scale)), (int)((int)height));
    }

    public static void enable(int glTarget) {
        GL11.glEnable((int)glTarget);
    }

    public static void disable(int glTarget) {
        GL11.glDisable((int)glTarget);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void start() {
        RenderUtils.enable(3042);
        GL11.glBlendFunc((int)770, (int)771);
        RenderUtils.disable(3553);
        RenderUtils.disable(2884);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        RenderUtils.enable(2884);
        RenderUtils.enable(3553);
        RenderUtils.disable(3042);
        RenderUtils.color(Color.white);
    }

    public static void begin(int glMode) {
        GL11.glBegin((int)glMode);
    }

    public static void vertex(double x, double y) {
        GL11.glVertex2d((double)x, (double)y);
    }

    public static void gradient(double x, double y, double width, double height, boolean filled, Color color1, Color color2) {
        RenderUtils.start();
        GL11.glShadeModel((int)7425);
        GlStateManager.enableAlpha();
        GL11.glAlphaFunc((int)516, (float)0.0f);
        if (color1 != null) {
            RenderUtils.color(color1);
        }
        RenderUtils.begin(filled ? 7 : 1);
        RenderUtils.vertex(x, y);
        RenderUtils.vertex(x + width, y);
        if (color2 != null) {
            RenderUtils.color(color2);
        }
        RenderUtils.vertex(x + width, y + height);
        RenderUtils.vertex(x, y + height);
        if (!filled) {
            RenderUtils.vertex(x, y);
            RenderUtils.vertex(x, y + height);
            RenderUtils.vertex(x + width, y);
            RenderUtils.vertex(x + width, y + height);
        }
        RenderUtils.end();
        GL11.glAlphaFunc((int)516, (float)0.1f);
        GlStateManager.disableAlpha();
        GL11.glShadeModel((int)7424);
        RenderUtils.stop();
    }

    public static void roundedRect(double x, double y, double width, double height, double edgeRadius, Color color) {
        double angle;
        double i;
        double halfRadius = edgeRadius / 2.0;
        width -= halfRadius;
        height -= halfRadius;
        float sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        RenderUtils.start();
        if (color != null) {
            RenderUtils.color(color);
        }
        RenderUtils.begin(6);
        for (i = 180.0; i <= 270.0; i += 1.0) {
            angle = i * (Math.PI * 2) / 360.0;
            RenderUtils.vertex(x + (double)sideLength * Math.cos(angle) + (double)sideLength, y + (double)sideLength * Math.sin(angle) + (double)sideLength);
        }
        RenderUtils.vertex(x + (double)sideLength, y + (double)sideLength);
        RenderUtils.end();
        RenderUtils.stop();
        sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        RenderUtils.start();
        if (color != null) {
            RenderUtils.color(color);
        }
        GL11.glEnable((int)2848);
        RenderUtils.begin(6);
        for (i = 0.0; i <= 90.0; i += 1.0) {
            angle = i * (Math.PI * 2) / 360.0;
            RenderUtils.vertex(x + width + (double)sideLength * Math.cos(angle), y + height + (double)sideLength * Math.sin(angle));
        }
        RenderUtils.vertex(x + width, y + height);
        RenderUtils.end();
        GL11.glDisable((int)2848);
        RenderUtils.stop();
        sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        RenderUtils.start();
        if (color != null) {
            RenderUtils.color(color);
        }
        GL11.glEnable((int)2848);
        RenderUtils.begin(6);
        for (i = 270.0; i <= 360.0; i += 1.0) {
            angle = i * (Math.PI * 2) / 360.0;
            RenderUtils.vertex(x + width + (double)sideLength * Math.cos(angle), y + (double)sideLength * Math.sin(angle) + (double)sideLength);
        }
        RenderUtils.vertex(x + width, y + (double)sideLength);
        RenderUtils.end();
        GL11.glDisable((int)2848);
        RenderUtils.stop();
        sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        RenderUtils.start();
        if (color != null) {
            RenderUtils.color(color);
        }
        GL11.glEnable((int)2848);
        RenderUtils.begin(6);
        for (i = 90.0; i <= 180.0; i += 1.0) {
            angle = i * (Math.PI * 2) / 360.0;
            RenderUtils.vertex(x + (double)sideLength * Math.cos(angle) + (double)sideLength, y + height + (double)sideLength * Math.sin(angle));
        }
        RenderUtils.vertex(x + (double)sideLength, y + height);
        RenderUtils.end();
        GL11.glDisable((int)2848);
        RenderUtils.stop();
        RenderUtils.drawRect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color.getRGB());
        RenderUtils.drawRect(x, y + halfRadius, edgeRadius / 2.0, height - halfRadius, color.getRGB());
        RenderUtils.drawRect(x + width, y + halfRadius, edgeRadius / 2.0, height - halfRadius, color.getRGB());
        RenderUtils.drawRect(x + halfRadius, y, width - halfRadius, halfRadius, color.getRGB());
        RenderUtils.drawRect(x + halfRadius, y + height, width - halfRadius, halfRadius, color.getRGB());
    }

    public static int getOppositeColor(int color) {
        int R = color & 0xFF;
        int G = color >> 8 & 0xFF;
        int B = color >> 16 & 0xFF;
        int A = color >> 24 & 0xFF;
        R = 255 - R;
        G = 255 - G;
        B = 255 - B;
        return R + (G << 8) + (B << 16) + (A << 24);
    }
}

