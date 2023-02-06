package dev.elysium.client.ui.widgets.impl;

import dev.elysium.client.ui.widgets.Widget;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class Keystrokes extends Widget {
    public Keystrokes() {
        super("Keystrokes", 100, 50);
    }

    public void draw() {
        super.draw();

        int color = 0xff55ff55;

        int left = 0;
        int top = 0;
        int right = 90;
        int bottom = 50;

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(9, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left + 10, (double)bottom + 7, 0.0D).endVertex();
        worldrenderer.pos((double)right - 10, (double)bottom + 7, 0.0D).endVertex();
        worldrenderer.pos((double)right - 10 + 7, bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top + 7, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();


        RenderUtils.drawACircle(left + 10, bottom, 0, 306, 7, 0xaaffffff);
        RenderUtils.drawACircle(left, top, 0, 306, 7, 0xaaffffff);
        RenderUtils.drawACircle(right - 10, bottom, 0, 306, 7, 0xaaffffff);
        RenderUtils.drawACircle(right, top, 0, 306, 7, 0xaaffffff);


        GlStateManager.popMatrix();
    }
}
