/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.scoreboard.ScorePlayerTeam
 *  net.minecraft.util.AxisAlignedBB
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.Cylinder
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.module.impl.combat.KillAura
 *  vip.astroline.client.storage.utils.render.render.WorldRenderUtils$ColorUtils
 */
package vip.astroline.client.storage.utils.render.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.module.impl.combat.KillAura;
import vip.astroline.client.storage.utils.render.render.WorldRenderUtils;

/*
 * Exception performing whole class analysis ignored.
 */
public class WorldRenderUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static int getScaledHeight() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledHeight();
    }

    public static int getScaledWidth() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledWidth();
    }

    public static void renderOne() {
        WorldRenderUtils.checkSetupFBO();
        GL11.glPushAttrib((int)1048575);
        GL11.glDisable((int)3008);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)3.0f);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2960);
        GL11.glClear((int)1024);
        GL11.glClearStencil((int)15);
        GL11.glStencilFunc((int)512, (int)1, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public static void checkSetupFBO() {
        Framebuffer fbo = mc.getFramebuffer();
        if (fbo == null) return;
        if (fbo.depthBuffer <= -1) return;
        WorldRenderUtils.setupFBO(fbo);
        fbo.depthBuffer = -1;
    }

    public static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT((int)fbo.depthBuffer);
        int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT((int)36161, (int)stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT((int)36161, (int)34041, (int)WorldRenderUtils.mc.displayWidth, (int)WorldRenderUtils.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36128, (int)36161, (int)stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36096, (int)36161, (int)stencil_depth_buffer_ID);
    }

    public static void renderTwo() {
        GL11.glStencilFunc((int)512, (int)0, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    public static void renderThree() {
        GL11.glStencilFunc((int)514, (int)1, (int)15);
        GL11.glStencilOp((int)7680, (int)7680, (int)7680);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public static ScaledResolution getScaledResolution() {
        return new ScaledResolution(mc);
    }

    public static void renderFour(int color) {
        WorldRenderUtils.setColor(color);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)10754);
        GL11.glPolygonOffset((float)1.0f, (float)-2000000.0f);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
    }

    public static void renderFour(EntityLivingBase entity) {
        if (entity != null) {
            int color = -11157267;
            if (Astroline.INSTANCE.moduleManager.getModule("KillAura").isToggled() && KillAura.target != null & entity.getEntityId() == KillAura.target.getEntityId()) {
                color = new Color(0xFF00AF).getRGB();
            }
            WorldRenderUtils.setColor(color);
        }
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)10754);
        GL11.glPolygonOffset((float)1.0f, (float)-2000000.0f);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
    }

    public static int getTeamColor(Entity player) {
        int var2 = 0xFFFFFF;
        if (!(player instanceof EntityPlayer)) return var2;
        ScorePlayerTeam var6 = (ScorePlayerTeam)((EntityPlayer)player).getTeam();
        if (var6 == null) return var2;
        String var7 = FontRenderer.getFormatFromString((String)var6.getColorPrefix());
        if (var7.length() < 2) return var2;
        if (!"0123456789abcdef".contains(String.valueOf(var7.charAt(1)))) {
            return var2;
        }
        var2 = WorldRenderUtils.mc.fontRendererObj.getColorCode(var7.charAt(1));
        return var2;
    }

    public static void renderFive() {
        GL11.glPolygonOffset((float)1.0f, (float)2000000.0f);
        GL11.glDisable((int)10754);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)2960);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glEnable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)3008);
        GL11.glPopAttrib();
    }

    public static void enableGL3D(float lineWidth) {
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glLineWidth((float)lineWidth);
    }

    public static void disableGL3D() {
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glDepthMask((boolean)true);
        GL11.glCullFace((int)1029);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    @Deprecated
    public static void drawRect(float x, float y, float x1, float y1, int color) {
        WorldRenderUtils.enableGL2D();
        WorldRenderUtils.glColor(color);
        WorldRenderUtils.drawRect(x, y, x1, y1);
        WorldRenderUtils.disableGL2D();
    }

    @Deprecated
    public static void drawRect(float x, float y, float x1, float y1, float r, float g, float b, float a) {
        WorldRenderUtils.enableGL2D();
        GL11.glColor4f((float)r, (float)g, (float)b, (float)a);
        WorldRenderUtils.drawRect(x, y, x1, y1);
        WorldRenderUtils.disableGL2D();
    }

    @Deprecated
    public static void drawRect(float x, float y, float x1, float y1) {
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
    }

    public static void drawBorderedRectReliant(float x, float y, float x1, float y1, float lineWidth, int inside, int border) {
        WorldRenderUtils.enableGL2D();
        WorldRenderUtils.drawRect(x, y, x1, y1, inside);
        WorldRenderUtils.glColor(border);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)lineWidth);
        GL11.glBegin((int)3);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        WorldRenderUtils.disableGL2D();
    }

    @Deprecated
    public static float getScaleFactor() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaleFactor();
    }

    @Deprecated
    public static int getDisplayWidth() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledWidth();
    }

    @Deprecated
    public static int getDisplayHeight() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledHeight();
    }

    private static void renderEnchantText(ItemStack stack, int x, int y) {
        int unbreakingLevel2;
        int enchantmentY = y - 24;
        if (stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() >= 6) {
            WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("god", (float)(x * 2), (float)enchantmentY, 0xFF0000);
            return;
        }
        if (stack.getItem() instanceof ItemArmor) {
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.protection.effectId, (ItemStack)stack);
            int projectileProtectionLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.projectileProtection.effectId, (ItemStack)stack);
            int blastProtectionLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.blastProtection.effectId, (ItemStack)stack);
            int fireProtectionLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fireProtection.effectId, (ItemStack)stack);
            int thornsLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.thorns.effectId, (ItemStack)stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.unbreaking.effectId, (ItemStack)stack);
            if (protectionLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("pr" + protectionLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (projectileProtectionLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("pp" + projectileProtectionLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (blastProtectionLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("bp" + blastProtectionLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (fireProtectionLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("fp" + fireProtectionLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (thornsLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("t" + thornsLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("u" + unbreakingLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemBow) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.power.effectId, (ItemStack)stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.punch.effectId, (ItemStack)stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.flame.effectId, (ItemStack)stack);
            unbreakingLevel2 = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.unbreaking.effectId, (ItemStack)stack);
            if (powerLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("po" + powerLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (punchLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("pu" + punchLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (flameLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("f" + flameLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (unbreakingLevel2 > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("u" + unbreakingLevel2, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemSword) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.sharpness.effectId, (ItemStack)stack);
            int knockbackLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.knockback.effectId, (ItemStack)stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fireAspect.effectId, (ItemStack)stack);
            unbreakingLevel2 = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.unbreaking.effectId, (ItemStack)stack);
            if (sharpnessLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("sh" + sharpnessLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (knockbackLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("kn" + knockbackLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (fireAspectLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("f" + fireAspectLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (unbreakingLevel2 > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("ub" + unbreakingLevel2, (float)(x * 2), (float)enchantmentY, -1052689);
            }
        }
        if (stack.getItem() instanceof ItemTool) {
            int unbreakingLevel3 = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.unbreaking.effectId, (ItemStack)stack);
            int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.efficiency.effectId, (ItemStack)stack);
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fortune.effectId, (ItemStack)stack);
            int silkTouch = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.silkTouch.effectId, (ItemStack)stack);
            if (efficiencyLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("eff" + efficiencyLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (fortuneLevel > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("fo" + fortuneLevel, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (silkTouch > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("st" + silkTouch, (float)(x * 2), (float)enchantmentY, -1052689);
                enchantmentY += 8;
            }
            if (unbreakingLevel3 > 0) {
                WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("ub" + unbreakingLevel3, (float)(x * 2), (float)enchantmentY, -1052689);
            }
        }
        if (stack.getItem() != Items.golden_apple) return;
        if (!stack.hasEffect()) return;
        WorldRenderUtils.mc.fontRendererObj.drawStringWithShadow("god", (float)(x * 2), (float)enchantmentY, -1052689);
    }

    public static void enableGL2D() {
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
    }

    public static void disableGL2D() {
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    public static void glColor(Color color) {
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
    }

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569f * (float)redRGB;
        float green = 0.003921569f * (float)greenRGB;
        float blue = 0.003921569f * (float)blueRGB;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    @Deprecated
    public static void drawBorderRect(int left, int top, int right, int bottom, int bcolor, int icolor, int bwidth) {
        Gui.drawRect((int)(left + bwidth), (int)(top + bwidth), (int)(right - bwidth), (int)(bottom - bwidth), (int)icolor);
        Gui.drawRect((int)left, (int)(top + 1), (int)(left + bwidth), (int)(bottom - 1), (int)bcolor);
        Gui.drawRect((int)(left + bwidth), (int)top, (int)right, (int)(top + bwidth), (int)bcolor);
        Gui.drawRect((int)(left + bwidth), (int)(bottom - bwidth), (int)right, (int)bottom, (int)bcolor);
        Gui.drawRect((int)(right - bwidth), (int)(top + bwidth), (int)right, (int)(bottom - bwidth), (int)bcolor);
    }

    public static void drawRectZZ(double x, double y, double x1, double y1) {
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)((float)x), (float)((float)y1));
        GL11.glVertex2f((float)((float)x1), (float)((float)y1));
        GL11.glVertex2f((float)((float)x1), (float)((float)y));
        GL11.glVertex2f((float)((float)x), (float)((float)y));
        GL11.glEnd();
    }

    public static int transparency(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float)c.getRed();
        float g = 0.003921569f * (float)c.getGreen();
        float b = 0.003921569f * (float)c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBlockESP(double x, double y, double z, int maincoolor, int borderColor, float lineWidth) {
        float alpha = (float)(maincoolor >> 24 & 0xFF) / 255.0f;
        float red = (float)(maincoolor >> 16 & 0xFF) / 255.0f;
        float green = (float)(maincoolor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(maincoolor & 0xFF) / 255.0f;
        float lineAlpha = (float)(borderColor >> 24 & 0xFF) / 255.0f;
        float lineRed = (float)(borderColor >> 16 & 0xFF) / 255.0f;
        float lineGreen = (float)(borderColor >> 8 & 0xFF) / 255.0f;
        float lineBlue = (float)(borderColor & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glLineWidth((float)lineWidth);
        GL11.glColor4f((float)lineRed, (float)lineGreen, (float)lineBlue, (float)lineAlpha);
        WorldRenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawPointESP(double x, double y, double z, int maincoolor) {
        float alpha = (float)(maincoolor >> 24 & 0xFF) / 255.0f;
        float red = (float)(maincoolor >> 16 & 0xFF) / 255.0f;
        float green = (float)(maincoolor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(maincoolor & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 0.1, y + 0.1, z + 0.1));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawFilledBBESP(AxisAlignedBB axisalignedbb, int color) {
        GL11.glPushMatrix();
        float red = (float)(color >> 24 & 0xFF) / 255.0f;
        float green = (float)(color >> 16 & 0xFF) / 255.0f;
        float blue = (float)(color >> 8 & 0xFF) / 255.0f;
        float alpha = (float)(color & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawFilledBox(axisalignedbb);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawFilledBox(AxisAlignedBB boundingBox) {
        if (boundingBox == null) {
            return;
        }
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glEnd();
    }

    public static void drawBoundingBoxESP(AxisAlignedBB axisalignedbb, float width, int color) {
        GL11.glPushMatrix();
        float red = (float)(color >> 24 & 0xFF) / 255.0f;
        float green = (float)(color >> 16 & 0xFF) / 255.0f;
        float blue = (float)(color >> 8 & 0xFF) / 255.0f;
        float alpha = (float)(color & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)width);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawOutlinedBox(axisalignedbb);
        GL11.glLineWidth((float)1.0f);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawBlockESP(double x, double y, double z, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glLineWidth((float)lineWidth);
        GL11.glColor4f((float)lineRed, (float)lineGreen, (float)lineBlue, (float)lineAlpha);
        WorldRenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawClickTPESP(double x, double y, double z, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x, y + 1.1, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glLineWidth((float)lineWidth);
        GL11.glColor4f((float)lineRed, (float)lineGreen, (float)lineBlue, (float)lineAlpha);
        WorldRenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x, y + 1.1, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawSolidBlockESP(double x, double y, double z, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawSolidBoxESP(double x, double y, double z, Color color, float size) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x, y, z, x + (double)size, y + (double)size, z + (double)size));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawOutlinedEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)3.0f);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void setColor(int colorHex) {
        float alpha = (float)(colorHex >> 24 & 0xFF) / 255.0f;
        float red = (float)(colorHex >> 16 & 0xFF) / 255.0f;
        float green = (float)(colorHex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(colorHex & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)(alpha == 0.0f ? 1.0f : alpha));
    }

    public static void drawSolidEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth((float)lineWdith);
        GL11.glColor4f((float)lineRed, (float)lineGreen, (float)lineBlue, (float)lineAlpha);
        WorldRenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawHat(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        WorldRenderUtils.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth((float)lineWdith);
        GL11.glColor4f((float)lineRed, (float)lineGreen, (float)lineBlue, (float)lineAlpha);
        WorldRenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawCircle(float x, float y, float radius, float lineWidth, int color) {
        WorldRenderUtils.enableGL2D();
        WorldRenderUtils.setColor(color);
        GL11.glLineWidth((float)lineWidth);
        int vertices = (int)Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin((int)2);
        int i = 0;
        while (true) {
            if (i >= vertices) {
                GL11.glEnd();
                WorldRenderUtils.disableGL2D();
                return;
            }
            double angleRadians = Math.PI * 2 * (double)i / (double)vertices;
            GL11.glVertex2d((double)((double)x + Math.sin(angleRadians) * (double)radius), (double)((double)y + Math.cos(angleRadians) * (double)radius));
            ++i;
        }
    }

    public static void drawCircle(int x, int y, double r, int c) {
        float f = (float)(c >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(c >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(c >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(c & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)f2, (float)f3, (float)f4, (float)f);
        GL11.glBegin((int)2);
        int i = 0;
        while (true) {
            if (i > 360) {
                GL11.glEnd();
                GL11.glDisable((int)2848);
                GL11.glEnable((int)3553);
                GL11.glDisable((int)3042);
                return;
            }
            double x2 = Math.sin((double)i * Math.PI / 180.0) * r;
            double y2 = Math.cos((double)i * Math.PI / 180.0) * r;
            GL11.glVertex2d((double)((double)x + x2), (double)((double)y + y2));
            ++i;
        }
    }

    public static void drawFilledCircle(float x, float y, float radius, int color) {
        WorldRenderUtils.enableGL2D();
        WorldRenderUtils.setColor(color);
        int vertices = (int)Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin((int)9);
        int i = 0;
        while (true) {
            if (i >= vertices) {
                GL11.glEnd();
                WorldRenderUtils.disableGL2D();
                WorldRenderUtils.drawCircle(x, y, radius, 1.5f, 0xFFFFFF);
                return;
            }
            double angleRadians = Math.PI * 2 * (double)i / (double)vertices;
            GL11.glVertex2d((double)((double)x + Math.sin(angleRadians) * (double)radius), (double)((double)y + Math.cos(angleRadians) * (double)radius));
            ++i;
        }
    }

    public static void drawFilledCircle(int x, int y, double r, int c) {
        float f = (float)(c >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(c >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(c >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(c & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)f2, (float)f3, (float)f4, (float)f);
        GL11.glBegin((int)6);
        int i = 0;
        while (true) {
            if (i > 360) {
                GL11.glEnd();
                GL11.glDisable((int)2848);
                GL11.glEnable((int)3553);
                GL11.glDisable((int)3042);
                return;
            }
            double x2 = Math.sin((double)i * Math.PI / 180.0) * r;
            double y2 = Math.cos((double)i * Math.PI / 180.0) * r;
            GL11.glVertex2d((double)((double)x + x2), (double)((double)y + y2));
            ++i;
        }
    }

    public static void dr(double i, double j, double k, double l, int i1) {
        if (i < k) {
            double j2 = i;
            i = k;
            k = j2;
        }
        if (j < l) {
            double k2 = j;
            j = l;
            l = k2;
        }
        float f = (float)(i1 >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(i1 >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(i1 >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(i1 & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)f2, (float)f3, (float)f4, (float)f);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(i, l, 0.0);
        worldRenderer.pos(k, l, 0.0);
        worldRenderer.pos(k, j, 0.0);
        worldRenderer.pos(i, j, 0.0);
        tessellator.draw();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float var7 = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float var8 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float var9 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float var10 = (float)(startColor & 0xFF) / 255.0f;
        float var11 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float var12 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float var13 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float var14 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.shadeModel((int)7425);
        Tessellator var15 = Tessellator.getInstance();
        WorldRenderer var16 = var15.getWorldRenderer();
        var16.begin(7, DefaultVertexFormats.POSITION_COLOR);
        var16.pos(right, top, 0.0).color(var8, var9, var10, var7).endVertex();
        var16.pos(left, top, 0.0).color(var8, var9, var10, var7).endVertex();
        var16.pos(left, bottom, 0.0).color(var12, var13, var14, var11).endVertex();
        var16.pos(right, bottom, 0.0).color(var12, var13, var14, var11).endVertex();
        var15.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawBorderedRectZ(float left, float top, float right, float bottom, float borderWidth, int borderColor, int color) {
        float alpha = (float)(borderColor >> 24 & 0xFF) / 255.0f;
        float red = (float)(borderColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(borderColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(borderColor & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        WorldRenderUtils.drawRects(left, top, right, bottom, new Color(red, green, blue, alpha));
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
        if (borderWidth == 1.0f) {
            GL11.glEnable((int)2848);
        }
        GL11.glLineWidth((float)borderWidth);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos((double)left, (double)top, 0.0);
        worldRenderer.pos((double)left, (double)bottom, 0.0);
        worldRenderer.pos((double)right, (double)bottom, 0.0);
        worldRenderer.pos((double)right, (double)top, 0.0);
        worldRenderer.pos((double)left, (double)top, 0.0);
        worldRenderer.pos((double)right, (double)top, 0.0);
        worldRenderer.pos((double)left, (double)bottom, 0.0);
        worldRenderer.pos((double)right, (double)bottom, 0.0);
        tessellator.draw();
        GL11.glLineWidth((float)2.0f);
        if (borderWidth == 1.0f) {
            GL11.glDisable((int)2848);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRects(double left, double top, double right, double bottom, Color color) {
        WorldRenderUtils.enableGL2D();
        WorldRenderUtils.glColor(color);
        WorldRenderUtils.drawRectZZ(left, top, right, bottom);
        WorldRenderUtils.disableGL2D();
    }

    public static void drawRects2(double left, double top, double right, double bottom, int color) {
        WorldRenderUtils.enableGL2D();
        WorldRenderUtils.glColor(color);
        WorldRenderUtils.drawRectZZ(left, top, right, bottom);
        WorldRenderUtils.disableGL2D();
    }

    public static void drawBorderedGradientRect(double left, double top, double right, double bottom, float borderWidth, int borderColor, int startColor, int endColor) {
        float alpha = (float)(borderColor >> 24 & 0xFF) / 255.0f;
        float red = (float)(borderColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(borderColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(borderColor & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        WorldRenderUtils.drawGradientRect(left, top, right, bottom, startColor, endColor);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
        if (borderWidth == 1.0f) {
            GL11.glEnable((int)2848);
        }
        GL11.glLineWidth((float)borderWidth);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, top, 0.0);
        worldRenderer.pos(left, bottom, 0.0);
        worldRenderer.pos(right, bottom, 0.0);
        worldRenderer.pos(right, top, 0.0);
        worldRenderer.pos(left, top, 0.0);
        worldRenderer.pos(right, top, 0.0);
        worldRenderer.pos(left, bottom, 0.0);
        worldRenderer.pos(right, bottom, 0.0);
        tessellator.draw();
        GL11.glLineWidth((float)2.0f);
        if (borderWidth == 1.0f) {
            GL11.glDisable((int)2848);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static double getDiff(double lastI, double i, float ticks, double ownI) {
        return lastI + (i - lastI) * (double)ticks - ownI;
    }

    public static void boundingBox(Entity entity, double x, double y, double z, int color, int colorIn) {
        GlStateManager.pushMatrix();
        GL11.glLineWidth((float)1.0f);
        AxisAlignedBB var11 = entity.getEntityBoundingBox();
        AxisAlignedBB var12 = new AxisAlignedBB(var11.minX - entity.posX + x, var11.minY - entity.posY + y, var11.minZ - entity.posZ + z, var11.maxX - entity.posX + x, var11.maxY - entity.posY + y, var11.maxZ - entity.posZ + z);
        if (color != 0) {
            GlStateManager.disableDepth();
            WorldRenderUtils.filledBox(var12, colorIn, true);
            GlStateManager.disableLighting();
            int[] nArray = ColorUtils.getRGBAInt((int)color);
        }
        GlStateManager.popMatrix();
    }

    public static void filledBox(AxisAlignedBB boundingBox, int color, boolean shouldColor) {
        GlStateManager.pushMatrix();
        float var11 = (float)(color >> 24 & 0xFF) / 255.0f;
        float var12 = (float)(color >> 16 & 0xFF) / 255.0f;
        float var13 = (float)(color >> 8 & 0xFF) / 255.0f;
        float var14 = (float)(color & 0xFF) / 255.0f;
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        if (shouldColor) {
            GlStateManager.color((float)var12, (float)var13, (float)var14, (float)var11);
        }
        int draw = 7;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        Tessellator.getInstance().draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.popMatrix();
    }

    public static void drawBorderedRectZ(float left, float top, float right, float bottom, int borderColor, int color) {
        WorldRenderUtils.drawBorderedRectZ(left, top, right, bottom, 1.0f, borderColor, color);
    }

    public static void drawBorderedGradientRect(double left, double top, double right, double bottom, int borderColor, int startColor, int endColor) {
        WorldRenderUtils.drawBorderedGradientRect(left, top, right, bottom, 1.0f, borderColor, startColor, endColor);
    }

    public static void drawWolframEntityESP(EntityLivingBase entity, int rgb, double posX, double posY, double posZ) {
        GL11.glPushMatrix();
        GL11.glTranslated((double)posX, (double)posY, (double)posZ);
        GL11.glRotatef((float)(-entity.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        WorldRenderUtils.setColor(rgb);
        WorldRenderUtils.enableGL3D(1.0f);
        Cylinder c = new Cylinder();
        GL11.glRotatef((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        c.setDrawStyle(100011);
        c.draw(0.5f, 0.5f, entity.height + 0.1f, 18, 1);
        WorldRenderUtils.disableGL3D();
        GL11.glPopMatrix();
    }

    public static void drawExeterCrossESP(int rgb, double x, double y, double z) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x - 0.4, y, z - 0.4, x + 0.4, y + 2.0, z + 0.4);
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)x, (double)y, (double)z);
        GlStateManager.translate((double)(-x), (double)(-y), (double)(-z));
        WorldRenderUtils.enableGL3D(1.0f);
        WorldRenderUtils.setColor(rgb);
        WorldRenderUtils.drawOutlinedBoundingBox(axisAlignedBB);
        WorldRenderUtils.disableGL3D();
        GlStateManager.popMatrix();
    }

    public static void drawOutlinedBox(AxisAlignedBB box) {
        if (box == null) return;
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)box.minX, (double)box.minY, (double)box.minZ);
        GL11.glVertex3d((double)box.maxX, (double)box.minY, (double)box.minZ);
        GL11.glVertex3d((double)box.maxX, (double)box.minY, (double)box.maxZ);
        GL11.glVertex3d((double)box.minX, (double)box.minY, (double)box.maxZ);
        GL11.glVertex3d((double)box.minX, (double)box.minY, (double)box.minZ);
        GL11.glEnd();
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)box.minX, (double)box.maxY, (double)box.minZ);
        GL11.glVertex3d((double)box.maxX, (double)box.maxY, (double)box.minZ);
        GL11.glVertex3d((double)box.maxX, (double)box.maxY, (double)box.maxZ);
        GL11.glVertex3d((double)box.minX, (double)box.maxY, (double)box.maxZ);
        GL11.glVertex3d((double)box.minX, (double)box.maxY, (double)box.minZ);
        GL11.glEnd();
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)box.minX, (double)box.minY, (double)box.minZ);
        GL11.glVertex3d((double)box.minX, (double)box.maxY, (double)box.minZ);
        GL11.glVertex3d((double)box.maxX, (double)box.minY, (double)box.minZ);
        GL11.glVertex3d((double)box.maxX, (double)box.maxY, (double)box.minZ);
        GL11.glVertex3d((double)box.maxX, (double)box.minY, (double)box.maxZ);
        GL11.glVertex3d((double)box.maxX, (double)box.maxY, (double)box.maxZ);
        GL11.glVertex3d((double)box.minX, (double)box.minY, (double)box.maxZ);
        GL11.glVertex3d((double)box.minX, (double)box.maxY, (double)box.maxZ);
        GL11.glEnd();
    }

    public static void drawLaserPoint(int rgb, double posX, double posY, double posZ) {
        GL11.glPushMatrix();
        WorldRenderUtils.enableGL3D(2.0f);
        WorldRenderUtils.setColor(new Color(255, 0, 0).getRGB());
        GL11.glBegin((int)2);
        GL11.glVertex3d((double)0.0, (double)WorldRenderUtils.mc.thePlayer.getEyeHeight(), (double)0.0);
        GL11.glVertex3d((double)posX, (double)posY, (double)posZ);
        GL11.glEnd();
        WorldRenderUtils.disableGL3D();
        GL11.glPopMatrix();
    }

    public static void drawLine2D(double x1, double y1, double x2, double y2, float width, int color) {
        GL11.glEnable((int)3042);
        GL11.glDisable((int)2884);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)1.0f);
        WorldRenderUtils.setColor(color);
        GL11.glLineWidth((float)width);
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2848);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
}
