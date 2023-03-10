/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.render.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.AntiBot;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.font.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Nametags
extends Module {
    public NumberSetting scale = new NumberSetting("Scale", 16.0, 32.0, 16.0, 1.0);
    public NumberSetting alpha = new NumberSetting("Alpha", 0.0, 255.0, 125.0, 1.0);
    public BooleanSetting healthBool = new BooleanSetting("Health", true);
    public BooleanSetting distanceBool = new BooleanSetting("Distance", true);

    public Nametags() {
        super("Nametags", 0, Category.RENDER);
        this.addSettings(this.scale, this.alpha, this.healthBool, this.distanceBool);
    }

    @Override
    @Subscribe
    public void on3D(EventRender3D e) {
        for (EntityPlayer p : Nametags.mc.theWorld.playerEntities) {
            if (!p.isEntityAlive() || p == Nametags.mc.thePlayer || AntiBot.bot.contains(p) || p.isInvisible()) continue;
            double tickPosX = p.lastTickPosX + (p.posX - p.lastTickPosX) * (double)Nametags.mc.timer.renderPartialTicks;
            double tickPosY = p.lastTickPosY + (p.posY - p.lastTickPosY) * (double)Nametags.mc.timer.renderPartialTicks;
            double tickPosZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * (double)Nametags.mc.timer.renderPartialTicks;
            try {
                this.renderNameTags(p, p.getDisplayName().getFormattedText(), tickPosX - Nametags.mc.getRenderManager().renderPosX, tickPosY - Nametags.mc.getRenderManager().renderPosY, tickPosZ - Nametags.mc.getRenderManager().renderPosZ, Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderManager().getFontRenderer());
            }
            catch (NullPointerException nullPointerException) {}
        }
    }

    private void renderNameTags(Entity entityIn, String str, double x, double y, double z, RenderManager renderManager, FontRenderer fontrenderer) {
        EntityPlayer p = (EntityPlayer)entityIn;
        TTFFontRenderer fr = November.INSTANCE.fm.getFont("PRODUCT SANS 16");
        int health = Math.round(p.getHealth());
        double distance = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entityIn);
        String healtColor = (float)health < 6.0f ? "\u00a7c" : ((float)health < 15.0f ? "\u00a76" : "\u00a7a");
        str = str + (this.healthBool.isEnabled() ? " " + healtColor + health / 2 : "") + "\u00a7r" + (this.distanceBool.isEnabled() ? " " + Math.round(distance) + "m" : "");
        float maxScale = -(this.scale.getValueFloat() / 1000.0f);
        double factor = 4.0 + distance;
        float scale = factor > 4.0 ? (float)((double)maxScale * factor / 6.0) : -0.03f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x + 0.0f, (float)y + entityIn.height + 0.5f, (float)z);
        GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int b0 = 0;
        if (str.equals("deadmau5")) {
            b0 = -10;
        }
        int i = fr.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-i - 1, -1 + b0, 0.0).color(190.0f, 190.0f, 190.0f, (float)(this.alpha.getValue() / 255.0)).endVertex();
        worldrenderer.pos(-i - 1, 8 + b0, 0.0).color(190.0f, 190.0f, 190.0f, (float)(this.alpha.getValue() / 255.0)).endVertex();
        worldrenderer.pos(i + 1, 8 + b0, 0.0).color(190.0f, 190.0f, 190.0f, (float)(this.alpha.getValue() / 255.0)).endVertex();
        worldrenderer.pos(i + 1, -1 + b0, 0.0).color(190.0f, 190.0f, 190.0f, (float)(this.alpha.getValue() / 255.0)).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fr.drawString(str, -fr.getStringWidth(str) / 2, b0, -1);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fr.drawString(str, -fr.getStringWidth(str) / 2, b0, -1);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GL11.glEnable((int)2929);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}

