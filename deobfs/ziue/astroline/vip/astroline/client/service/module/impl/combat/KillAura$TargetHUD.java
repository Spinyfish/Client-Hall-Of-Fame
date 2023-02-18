/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiPlayerTabOverlay
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.WorldRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EnumPlayerModelParts
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.util.MathHelper
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.combat.KillAura
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.service.module.impl.combat;

import java.awt.Color;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.combat.KillAura;
import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class KillAura.TargetHUD {
    public final EntityPlayer ent;
    public float animation = 0.0f;

    public KillAura.TargetHUD(EntityPlayer player) {
        this.ent = player;
    }

    private void renderArmor(EntityPlayer player) {
        ItemStack stack;
        int index;
        int xOffset = 60;
        for (index = 3; index >= 0; --index) {
            stack = player.inventory.armorInventory[index];
            if (stack == null) continue;
            xOffset -= 8;
        }
        index = 3;
        while (index >= 0) {
            stack = player.inventory.armorInventory[index];
            if (stack != null) {
                ItemStack armourStack = stack.copy();
                if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
                    armourStack.stackSize = 1;
                }
                this.renderItemStack(armourStack, xOffset, 12);
                xOffset += 16;
            }
            --index;
        }
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        Module.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableCull();
        Module.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        Module.mc.getRenderItem().renderItemOverlays(Module.mc.fontRendererObj, stack, x, y);
        GlStateManager.enableCull();
        Module.mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.disableBlend();
        GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    public void render(float x, float y) {
        GL11.glPushMatrix();
        String playerName = this.ent.getName();
        String healthStr = (double)Math.round(this.ent.getHealth() * 10.0f) / 10.0 + " hp";
        float width = Math.max(75.0f, FontManager.wqy18.getStringWidth(playerName) + 25.0f);
        if (KillAura.targetHUDMode.isCurrentMode("Flux")) {
            GL11.glTranslatef((float)x, (float)y, (float)0.0f);
            GuiRenderUtils.drawRoundedRect((float)0.0f, (float)0.0f, (float)(28.0f + width), (float)28.0f, (float)2.0f, (int)RenderUtil.reAlpha((int)-16777216, (float)0.6f), (float)1.0f, (int)RenderUtil.reAlpha((int)-16777216, (float)0.5f));
            FontManager.wqy15.drawString(playerName, 30.0f, 3.0f, ColorUtils.WHITE.c);
            FontManager.roboto12.drawString(healthStr, 26.0f + width - FontManager.roboto12.getStringWidth(healthStr) - 2.0f, 4.0f, -3355444);
            float health = KillAura.target.getHealth();
            double hpPercentage = health / KillAura.target.getMaxHealth();
            hpPercentage = MathHelper.clamp_double((double)hpPercentage, (double)0.0, (double)1.0);
            int hue = (int)(hpPercentage * 120.0);
            Color color = Color.getHSBColor((float)hue / 360.0f, 0.7f, 1.0f);
            RenderUtil.drawRect((float)37.0f, (float)14.5f, (float)(26.0f + width - 2.0f), (float)17.5f, (int)RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.35f));
            float barWidth = 26.0f + width - 2.0f - 37.0f;
            float drawPercent = (float)(37.0 + (double)(barWidth / 100.0f) * (hpPercentage * 100.0));
            if (this.animation <= 0.0f) {
                this.animation = drawPercent;
            }
            if (this.ent.hurtTime <= 6) {
                this.animation = AnimationUtils.getAnimationState((float)this.animation, (float)drawPercent, (float)((float)Math.max(10.0, (double)(Math.abs(this.animation - drawPercent) * 30.0f) * 0.4)));
            }
            RenderUtil.drawRect((float)37.0f, (float)14.5f, (float)this.animation, (float)17.5f, (int)color.darker().getRGB());
            RenderUtil.drawRect((float)37.0f, (float)14.5f, (float)drawPercent, (float)17.5f, (int)color.getRGB());
            FontManager.icon10.drawString("s", 30.0f, 13.0f, ColorUtils.WHITE.c);
            FontManager.icon10.drawString("r", 30.0f, 20.0f, ColorUtils.WHITE.c);
            float f3 = 37.0f + barWidth / 100.0f * (float)(this.ent.getTotalArmorValue() * 5);
            RenderUtil.drawRect((float)37.0f, (float)21.5f, (float)(26.0f + width - 2.0f), (float)24.5f, (int)RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.35f));
            RenderUtil.drawRect((float)37.0f, (float)21.5f, (float)f3, (float)24.5f, (int)-12417291);
            this.rectangleBordered(1.5, 1.5, 26.5, 26.5, 0.5, 0, Color.GREEN.getRGB());
            GlStateManager.resetColor();
            for (NetworkPlayerInfo info : GuiPlayerTabOverlay.field_175252_a.sortedCopy(Module.mc.getNetHandler().getPlayerInfoMap())) {
                if (Module.mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) != this.ent) continue;
                Module.mc.getTextureManager().bindTexture(info.getLocationSkin());
                this.drawScaledCustomSizeModalRect(2.0f, 2.0f, 8.0f, 8.0f, 8.0f, 8.0f, 24.0f, 24.0f, 64.0f, 64.0f);
                if (this.ent.isWearing(EnumPlayerModelParts.HAT)) {
                    this.drawScaledCustomSizeModalRect(2.0f, 2.0f, 40.0f, 8.0f, 8.0f, 8.0f, 24.0f, 24.0f, 64.0f, 64.0f);
                }
                GlStateManager.bindTexture((int)0);
                break;
            }
            GL11.glPopMatrix();
            GlStateManager.resetColor();
        } else if (KillAura.targetHUDMode.isCurrentMode("Flux (Old)")) {
            GL11.glTranslatef((float)x, (float)y, (float)0.0f);
            String playerName2 = this.ent.getName();
            String healthStr2 = "Health: " + (double)Math.round(this.ent.getHealth() * 10.0f) / 10.0;
            float namewidth = FontManager.wqy18.getStringWidth(playerName2) + 4.0f;
            float healthwidth = FontManager.roboto16.getStringWidth(healthStr2) + 4.0f;
            float width2 = Math.max(namewidth, healthwidth);
            GuiRenderUtils.drawRoundedRect((float)0.0f, (float)0.0f, (float)(26.0f + width2), (float)40.0f, (float)2.0f, (int)RenderUtil.reAlpha((int)-14539477, (float)0.85f), (float)1.0f, (int)RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.0f));
            FontManager.wqy18.drawString(playerName2, 26.0f, 2.0f, ColorUtils.WHITE.c);
            FontManager.roboto16.drawString(healthStr2, 26.0f, 14.0f, ColorUtils.WHITE.c);
            float health = KillAura.target.getHealth();
            double hpPercentage = health / KillAura.target.getMaxHealth();
            hpPercentage = MathHelper.clamp_double((double)hpPercentage, (double)0.0, (double)1.0);
            float drawPercent = (float)((double)((16.5f + width2 - 2.0f) / 100.0f) * (hpPercentage * 100.0));
            if (this.animation <= 0.0f) {
                this.animation = drawPercent;
            }
            if (this.animation > 25.5f + width2 - 2.0f) {
                this.animation = drawPercent;
            }
            if (this.ent.hurtTime <= 6) {
                this.animation = AnimationUtils.getAnimationState((float)this.animation, (float)drawPercent, (float)((float)Math.max(10.0, (double)(Math.abs(this.animation - drawPercent) * 30.0f) * 0.4)));
            }
            RenderUtil.drawRect((float)10.0f, (float)27.5f, (float)(25.5f + width2 - 2.0f), (float)29.5f, (int)RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.35f));
            if (drawPercent > 0.0f) {
                float f1 = Math.max(10.0f + this.animation - 1.0f, 10.0f);
                float f2 = Math.max(10.0f + drawPercent - 1.0f, 10.0f);
                RenderUtil.drawRect((float)10.0f, (float)27.5f, (float)f1, (float)29.5f, (int)RenderUtil.reAlpha((int)-84409, (float)0.95f));
                RenderUtil.drawRect((float)10.0f, (float)27.5f, (float)f2, (float)29.5f, (int)RenderUtil.reAlpha((int)-16723585, (float)0.95f));
            }
            FontManager.icon10.drawString("s", 2.5f, 26.0f, ColorUtils.WHITE.c);
            FontManager.icon10.drawString("r", 2.5f, 33.0f, ColorUtils.WHITE.c);
            float f3 = Math.max(10.0f + (16.5f + width2 - 2.0f) / 100.0f * (float)(this.ent.getTotalArmorValue() * 5) - 1.0f, 10.0f);
            RenderUtil.drawRect((float)10.0f, (float)34.5f, (float)(25.5f + width2 - 2.0f), (float)36.5f, (int)RenderUtil.reAlpha((int)ColorUtils.BLACK.c, (float)0.35f));
            RenderUtil.drawRect((float)10.0f, (float)34.5f, (float)f3, (float)36.5f, (int)-12417291);
            this.rectangleBordered(1.5, 1.5, 24.5, 24.5, 0.5, 0, KillAura.target == this.ent ? ColorUtils.GREEN.c : -1);
            GlStateManager.resetColor();
            for (NetworkPlayerInfo info : GuiPlayerTabOverlay.field_175252_a.sortedCopy(Module.mc.getNetHandler().getPlayerInfoMap())) {
                if (Module.mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) != this.ent) continue;
                Module.mc.getTextureManager().bindTexture(info.getLocationSkin());
                this.drawScaledCustomSizeModalRect(2.0f, 2.0f, 8.0f, 8.0f, 8.0f, 8.0f, 22.0f, 22.0f, 64.0f, 64.0f);
                if (this.ent.isWearing(EnumPlayerModelParts.HAT)) {
                    this.drawScaledCustomSizeModalRect(2.0f, 2.0f, 40.0f, 8.0f, 8.0f, 8.0f, 22.0f, 22.0f, 64.0f, 64.0f);
                }
                GlStateManager.bindTexture((int)0);
                break;
            }
            GL11.glPopMatrix();
        } else if (KillAura.targetHUDMode.isCurrentMode("Astolfo")) {
            float width2 = Math.max(75, Module.mc.fontRendererObj.getStringWidth(playerName) + 20);
            String healthStr2 = (double)Math.round(this.ent.getHealth() * 10.0f) / 10.0 + " \u2764";
            GL11.glTranslatef((float)x, (float)y, (float)0.0f);
            GuiRenderUtils.drawBorderedRect((float)0.0f, (float)0.0f, (float)(55.0f + width2), (float)47.0f, (float)0.5f, (Color)new Color(0, 0, 0, 140), (Color)new Color(0, 0, 0));
            Module.mc.fontRendererObj.drawStringWithShadow(playerName, 35.0f, 3.0f, ColorUtils.WHITE.c);
            float health = KillAura.target.getHealth();
            double hpPercentage = health / KillAura.target.getMaxHealth();
            hpPercentage = MathHelper.clamp_double((double)hpPercentage, (double)0.0, (double)1.0);
            int hue = (int)(hpPercentage * 120.0);
            Color color = Color.getHSBColor((float)hue / 360.0f, 0.7f, 1.0f);
            GlStateManager.pushMatrix();
            GlStateManager.scale((double)2.0, (double)2.0, (double)2.0);
            Module.mc.fontRendererObj.drawStringWithShadow(healthStr2, 18.0f, 7.5f, color.getRGB());
            GlStateManager.popMatrix();
            RenderUtil.drawRect((float)36.0f, (float)36.5f, (float)(45.0f + width2), (float)44.5f, (int)RenderUtil.reAlpha((int)color.darker().darker().getRGB(), (float)0.35f));
            float barWidth = 43.0f + width2 - 2.0f - 37.0f;
            float drawPercent = (float)(43.0 + (double)(barWidth / 100.0f) * (hpPercentage * 100.0));
            if (this.animation <= 0.0f) {
                this.animation = drawPercent;
            }
            if (this.ent.hurtTime <= 6) {
                this.animation = AnimationUtils.getAnimationState((float)this.animation, (float)drawPercent, (float)((float)Math.max(10.0, (double)(Math.abs(this.animation - drawPercent) * 30.0f) * 0.4)));
            }
            RenderUtil.drawRect((float)36.0f, (float)36.5f, (float)(this.animation + 6.0f), (float)44.5f, (int)color.darker().darker().getRGB());
            RenderUtil.drawRect((float)36.0f, (float)36.5f, (float)this.animation, (float)44.5f, (int)color.getRGB());
            RenderUtil.drawRect((float)36.0f, (float)36.5f, (float)drawPercent, (float)44.5f, (int)color.getRGB());
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.resetColor();
            GlStateManager.disableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GuiInventory.drawEntityOnScreen((int)17, (int)46, (int)((int)(42.0f / KillAura.target.height)), (float)0.0f, (float)0.0f, (EntityLivingBase)this.ent, (float)165.0f);
            GL11.glPopMatrix();
        } else {
            if (!KillAura.targetHUDMode.isCurrentMode("Innominate")) return;
            float width2 = Math.max(75.0f, FontManager.tahoma13.getStringWidth(playerName) + 25.0f);
            String healthStr2 = (double)Math.round(this.ent.getHealth() * 10.0f) / 10.0 + "";
            GL11.glTranslatef((float)x, (float)y, (float)0.0f);
            GuiRenderUtils.drawRect((float)0.0f, (float)0.0f, (float)(45.0f + width2), (float)37.0f, (Color)new Color(0, 0, 0, 90));
            GuiRenderUtils.drawRect((float)4.0f, (float)4.0f, (float)(37.0f + width2), (float)29.0f, (Color)new Color(-14803428));
            FontManager.tahoma13.drawOutlinedString(playerName, 8.0f, 5.0f, ColorUtils.WHITE.c, ColorUtils.BLACK.c);
            float health = KillAura.target.getHealth();
            double hpPercentage = health / KillAura.target.getMaxHealth();
            hpPercentage = MathHelper.clamp_double((double)hpPercentage, (double)0.0, (double)1.0);
            int hue = (int)(hpPercentage * 120.0);
            Color color = Color.getHSBColor((float)hue / 360.0f, 0.7f, 1.0f);
            RenderUtil.drawRect((float)7.0f, (float)14.0f, (float)(27.5f + width2), (float)21.0f, (int)RenderUtil.reAlpha((int)-14869219, (float)1.0f));
            float barWidth = 34.5f + width2 - 2.0f - 37.0f;
            float drawPercent = (float)(34.5 + (double)(barWidth / 100.0f) * (hpPercentage * 100.0));
            if (this.animation <= 0.0f) {
                this.animation = drawPercent;
            }
            if (this.ent.hurtTime <= 6) {
                this.animation = AnimationUtils.getAnimationState((float)this.animation, (float)drawPercent, (float)((float)Math.max(10.0, (double)(Math.abs(this.animation - drawPercent) * 30.0f) * 0.4)));
            }
            RenderUtil.drawRect((float)7.0f, (float)14.0f, (float)this.animation, (float)21.0f, (int)color.getRGB());
            RenderUtil.drawRect((float)7.0f, (float)14.0f, (float)drawPercent, (float)21.0f, (int)color.getRGB());
            FontManager.tahoma13.drawOutlinedString(healthStr2, 55.5f, 13.0f, ColorUtils.WHITE.c, ColorUtils.BLACK.c);
            FontManager.tahoma13.drawOutlinedString("Distance: " + (double)Math.round(KillAura.target.getDistanceToEntity((Entity)Module.mc.thePlayer) * 10.0f) / 10.0 + " - Target HurtTime: " + Math.round(KillAura.target.hurtTime), 9.0f, 23.0f, ColorUtils.WHITE.c, ColorUtils.BLACK.c);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GL11.glPopMatrix();
        }
    }

    public void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        this.rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public void rectangle(double left, double top, double right, double bottom, int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (float)(color >> 24 & 0xFF) / 255.0f;
        float var6 = (float)(color >> 16 & 0xFF) / 255.0f;
        float var7 = (float)(color >> 8 & 0xFF) / 255.0f;
        float var8 = (float)(color & 0xFF) / 255.0f;
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)var6, (float)var7, (float)var8, (float)var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public void drawScaledCustomSizeModalRect(float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0f / tileWidth;
        float f1 = 1.0f / tileHeight;
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)x, (double)(y + height), 0.0).tex((double)(u * f), (double)((v + vHeight) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0).tex((double)((u + uWidth) * f), (double)((v + vHeight) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)y, 0.0).tex((double)((u + uWidth) * f), (double)(v * f1)).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
    }
}
