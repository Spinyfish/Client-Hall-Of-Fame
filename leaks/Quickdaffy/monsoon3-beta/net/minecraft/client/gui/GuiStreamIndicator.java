/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiStreamIndicator {
    private static final ResourceLocation locationStreamIndicator = new ResourceLocation("textures/gui/stream_indicator.png");
    private final Minecraft mc;
    private float field_152443_c = 1.0f;
    private int field_152444_d = 1;

    public GuiStreamIndicator(Minecraft mcIn) {
        this.mc = mcIn;
    }

    public void render(int p_152437_1_, int p_152437_2_) {
        if (this.mc.getTwitchStream().isBroadcasting()) {
            GlStateManager.enableBlend();
            int i = this.mc.getTwitchStream().func_152920_A();
            if (i > 0) {
                String s = "" + i;
                int j = this.mc.fontRendererObj.getStringWidth(s);
                int k = 20;
                int l = p_152437_1_ - j - 1;
                int i1 = p_152437_2_ + 20 - 1;
                int j1 = p_152437_2_ + 20 + this.mc.fontRendererObj.FONT_HEIGHT - 1;
                GlStateManager.disableTexture2D();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                GlStateManager.color(0.0f, 0.0f, 0.0f, (0.65f + 0.35000002f * this.field_152443_c) / 2.0f);
                worldrenderer.begin(7, DefaultVertexFormats.field_181705_e);
                worldrenderer.pos(l, j1, 0.0).end();
                worldrenderer.pos(p_152437_1_, j1, 0.0).end();
                worldrenderer.pos(p_152437_1_, i1, 0.0).end();
                worldrenderer.pos(l, i1, 0.0).end();
                tessellator.draw();
                GlStateManager.enableTexture2D();
                this.mc.fontRendererObj.drawString(s, (float)(p_152437_1_ - j), (float)(p_152437_2_ + 20), 0xFFFFFF);
            }
            this.render(p_152437_1_, p_152437_2_, this.func_152440_b(), 0);
            this.render(p_152437_1_, p_152437_2_, this.func_152438_c(), 17);
        }
    }

    private void render(int p_152436_1_, int p_152436_2_, int p_152436_3_, int p_152436_4_) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.65f + 0.35000002f * this.field_152443_c);
        this.mc.getTextureManager().bindTexture(locationStreamIndicator);
        float f = 150.0f;
        float f1 = 0.0f;
        float f2 = (float)p_152436_3_ * 0.015625f;
        float f3 = 1.0f;
        float f4 = (float)(p_152436_3_ + 16) * 0.015625f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(p_152436_1_ - 16 - p_152436_4_, p_152436_2_ + 16, f).func_181673_a(f1, f4).end();
        worldrenderer.pos(p_152436_1_ - p_152436_4_, p_152436_2_ + 16, f).func_181673_a(f3, f4).end();
        worldrenderer.pos(p_152436_1_ - p_152436_4_, p_152436_2_ + 0, f).func_181673_a(f3, f2).end();
        worldrenderer.pos(p_152436_1_ - 16 - p_152436_4_, p_152436_2_ + 0, f).func_181673_a(f1, f2).end();
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private int func_152440_b() {
        return this.mc.getTwitchStream().isPaused() ? 16 : 0;
    }

    private int func_152438_c() {
        return this.mc.getTwitchStream().func_152929_G() ? 48 : 32;
    }

    public void func_152439_a() {
        if (this.mc.getTwitchStream().isBroadcasting()) {
            this.field_152443_c += 0.025f * (float)this.field_152444_d;
            if (this.field_152443_c < 0.0f) {
                this.field_152444_d *= -1;
                this.field_152443_c = 0.0f;
            } else if (this.field_152443_c > 1.0f) {
                this.field_152444_d *= -1;
                this.field_152443_c = 1.0f;
            }
        } else {
            this.field_152443_c = 1.0f;
            this.field_152444_d = 1;
        }
    }
}

