/** THIS ONLY FOR DRAW **/
package alos.stella.utils.render;
import alos.stella.module.modules.visual.Trails;
import alos.stella.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.List;
public class DrawUtils {
    protected static Minecraft mc = Minecraft.getMinecraft();
    private static final Frustum frustrum = new Frustum();
    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (color >> 24 & 255) / 255.0F;
        float f = (color >> 16 & 255) / 255.0F;
        float f1 = (color >> 8 & 255) / 255.0F;
        float f2 = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawBorder(float x, float y, float x2, float y2, float width, int color1) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        alos.stella.utils.render.ColorUtils.color(color1);
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
    public static void drawLine(final double x, final double y, final double x1, final double y1, final float width) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;
        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    public static void drawRectAngle(double x, double y, double x2, double y2, int color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        alos.stella.utils.render.ColorUtils.color(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
    public static void drawTargetHudRect(double d, double e, double g, double h, int color, int i) {
        drawRect(d+1, e, g-1, h, color);
        drawRect(d, e+1, d+1, h-1, color);
        drawRect(d+1, e+1, d+0.5, e+0.5, color);
        drawRect(d+1, e+1, d+0.5, e+0.5, color);
        drawRect(g-1, e+1, g-0.5, e+0.5, color);
        drawRect(g-1, e+1, g, h-1, color);
        drawRect(d+1, h-1, d+0.5, h-0.5, color);
        drawRect(g-1, h-1, g-0.5, h-0.5, color);
    }
    public static void drawRectAngleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        drawRectAngle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color( 1.0f,  1.0f,  1.0f,  1.0f);
        drawRectAngle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color( 1.0f,  1.0f,  1.0f,  1.0f);
        drawRectAngle(x, y, x + width, y1, borderColor);
        GlStateManager.color( 1.0f,  1.0f,  1.0f,  1.0f);
        drawRectAngle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color( 1.0f,  1.0f,  1.0f,  1.0f);
        drawRectAngle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color( 1.0f,  1.0f,  1.0f,  1.0f);
    }
    public static void drawFace(int x, int y,float scale, AbstractClientPlayer target) {
        try {
            ResourceLocation skin = target.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, (int)scale, (int)scale, 64.0f, 64.0f);
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void drawOutlinedStringCock(FontRenderer fr, String s, float x, float y, int color, int outlineColor) {
        fr.drawString(ColorUtils.stripColor(s), (int) (x - 1.0f), (int) y, outlineColor);
        fr.drawString(ColorUtils.stripColor(s), (int) x, (int) (y - 1.0f), outlineColor);
        fr.drawString(ColorUtils.stripColor(s), (int) (x + 1.0f), (int) y, outlineColor);
        fr.drawString(ColorUtils.stripColor(s), (int) x, (int) (y + 1.0f), outlineColor);
        fr.drawString(s, (int) x, (int) y, color);
    }
    private static void drawEnchantTag(String text, int x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawOutlinedStringCock(Minecraft.getMinecraft().fontRendererObj, text, x, y, -1, new Color(0, 0, 0, 220).darker().getRGB());
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
    public static void drawCheck(double x, double y, int lineWidth, int color) {
        GLUtils.glStart2D();
        GL11.glPushMatrix();
        GL11.glLineWidth(lineWidth);
        alos.stella.utils.render.ColorUtils.color(new Color(color));
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + 2, y + 3);
        GL11.glVertex2d(x + 6, y - 2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GLUtils.glStop2D();
    }
    public static void drawTargetHudRect(double x, double y, double x1, double y1, double size) {
        drawRectAngleBordered(x, y + 4.0D, x1 + size, y1 + size, 0.5D, (new Color(60, 60, 60)).getRGB(), (new Color(22, 22, 22)).getRGB());
        drawRectAngleBordered(x + 1.0D, y + 3.0D, x1 + size - 1.0D, y1 + size - 1.0D, 1.0D, (new Color(40, 40, 40)).getRGB(), (new Color(40, 40, 40)).getRGB());
        drawRectAngleBordered(x + 2.5D, y + 1.5D, x1 + size - 2.5D, y1 + size - 2.5D, 0.5D, (new Color(40, 40, 40)).getRGB(), (new Color(60, 60, 60)).getRGB());
        drawRectAngleBordered(x + 2.5D, y + 1.5D, x1 + size - 2.5D, y1 + size - 2.5D, 0.5D, (new Color(22, 22, 22)).getRGB(), (new Color(255, 255, 255, 0)).getRGB());
    }
    public static void drawTargetHudRect1(double x, double y, double x1, double y1, double size) {
        drawRectAngleBordered(x + 4.35D, y + 0.5D, x1 + size - 84.5D, y1 + size - 4.35D, 0.5D, (new Color(48, 48, 48)).getRGB(), (new Color(10, 10, 10)).getRGB());
        drawRectAngleBordered(x + 5.0D, y + 1.0D, x1 + size - 85.0D, y1 + size - 5.0D, 0.5D, (new Color(17, 17, 17)).getRGB(), (new Color(255, 255, 255, 0)).getRGB());
    }
    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glEnd();
    }
    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        drawRound(x, y, width, height, radius, false, color);
    }
    public static void drawRenderBreadCrumbs(final List<Vector3d> vec3s) {
        GlStateManager.disableDepth();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        int i = 0;
        try {
            for (final Vector3d v : vec3s) {
                i++;
                boolean draw = true;
                final double x = v.x - (mc.getRenderManager()).renderPosX;
                final double y = v.y - (mc.getRenderManager()).renderPosY;
                final double z = v.z - (mc.getRenderManager()).renderPosZ;
                final double distanceFromPlayer = mc.thePlayer.getDistance(v.x, v.y - 1, v.z);
                int quality = (int) (distanceFromPlayer * 4 + 10);
                if (quality > 350) quality = 350;
                if (i % 10 != 0 && distanceFromPlayer > 25) {
                    draw = false;
                }
                if (i % 3 == 0 && distanceFromPlayer > 15) {
                    draw = false;
                }
                if (draw) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);
                    final float scale = 0.04f;
                    GL11.glScalef(-scale, -scale, -scale);
                    GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
                    GL11.glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);
                    Color c = Color.WHITE;
                    switch (Trails.colorMode.get()) {
                        case "Custom":
                            c = new Color(Trails.colorRedValue.get(), Trails.colorGreenValue.get(), Trails.colorBlueValue.get());
                            break;
                        case "rainbow":
                            c = new Color(alos.stella.utils.render.ColorUtils.getRainbowOpaque(2,Trails.saturationValue.get(), Trails.brightnessValue.get(), 0));
                            break;
                        case "astolfo":
                            c = alos.stella.utils.render.ColorUtils.skyRainbow(0, Trails.saturationValue.get(), Trails.brightnessValue.get());
                            break;
                        case "fade":
                            c = ColorUtils.fade(new Color(Trails.colorRedValue.get(), Trails.colorGreenValue.get(), Trails.colorBlueValue.get()), 0, 100);
                            break;
                    }
                    DrawUtils.drawFilledCircleNoGL(0, 0, 0.7, c.hashCode(), quality);
                    if (distanceFromPlayer < 4) DrawUtils.drawFilledCircleNoGL(0, 0, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);
                    if (distanceFromPlayer < 20) DrawUtils.drawFilledCircleNoGL(0, 0, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);
                    GL11.glScalef(0.8f, 0.8f, 0.8f);
                    GL11.glPopMatrix();
                }
            }
        } catch (final ConcurrentModificationException ignored) {
        }
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableDepth();
        GL11.glColor3d(255, 255, 255);
    }
    public static void drawQuads(float x, float y, float width, float height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
    }
    public static void renderEnchantText(ItemStack stack, int x, float y) {
        RenderHelper.disableStandardItemLighting();
        float enchantmentY = y + 24f;
        if (stack.getItem() instanceof ItemArmor) {
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int thornLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
            if (protectionLevel > 0) {
                drawEnchantTag("P" + ColorUtils.getColor(protectionLevel) + protectionLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                drawEnchantTag("U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (thornLevel > 0) {
                drawEnchantTag("T" + ColorUtils.getColor(thornLevel) + thornLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemBow) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (powerLevel > 0) {
                drawEnchantTag("Pow" + ColorUtils.getColor(powerLevel) + powerLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (punchLevel > 0) {
                drawEnchantTag("Pun" + ColorUtils.getColor(punchLevel) + punchLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (flameLevel > 0) {
                drawEnchantTag("F" + ColorUtils.getColor(flameLevel) + flameLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                drawEnchantTag("U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemSword) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (sharpnessLevel > 0) {
                drawEnchantTag("S" +  ColorUtils.getColor(sharpnessLevel) + sharpnessLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (knockbackLevel > 0) {
                drawEnchantTag("K" + ColorUtils.getColor(knockbackLevel) + knockbackLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (fireAspectLevel > 0) {
                drawEnchantTag("F" + ColorUtils.getColor(fireAspectLevel) + fireAspectLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                drawEnchantTag("U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getRarity() == EnumRarity.EPIC) {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            drawOutlinedStringCock(Minecraft.getMinecraft().fontRendererObj, "God", x * 2, enchantmentY, new Color(255, 255, 0).getRGB(), new Color(100, 100, 0, 200).getRGB());
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }
    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        alos.stella.utils.render.ColorUtils.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GLUtils.roundedShader.init();

        drawSetupRoundedRectUniforms(x, y, width, height, radius);
        GLUtils.roundedShader.setUniformi("blur", blur ? 1 : 0);
        GLUtils.roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        DrawUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        GLUtils.roundedShader.unload();
        GlStateManager.disableBlend();
    }
    private static void drawSetupRoundedRectUniforms(float x, float y, float width, float height, float radius) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GLUtils.roundedShader.setUniformf("location", x * sr.getScaleFactor(), (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        GLUtils.roundedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        GLUtils.roundedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }
    public static boolean drawIsInViewFrustrum(Entity entity) {
        return drawIsInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }
    private static boolean drawIsInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }
    public static double drawInterpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }
}
