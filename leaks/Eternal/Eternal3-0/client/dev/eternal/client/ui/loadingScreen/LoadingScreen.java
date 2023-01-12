package dev.eternal.client.ui.loadingScreen;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.util.IMinecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

public class LoadingScreen implements IMinecraft {

  private int stage;
  private final int MAX_STAGES = 9;

  public void update() {
    stage++;
    ScaledResolution scaledresolution = new ScaledResolution(mc);
    int i = scaledresolution.getScaleFactor();
    Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
    framebuffer.bind(false);
    GlStateManager.matrixMode(5889);
    GlStateManager.loadIdentity();
    GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
    GlStateManager.matrixMode(5888);
    GlStateManager.loadIdentity();
    GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    GlStateManager.disableLighting();
    GlStateManager.disableFog();
    GlStateManager.disableDepth();
    GlStateManager.enableTexture2D();

    Gui.drawRect(0, 0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0);
    final float width = scaledresolution.getScaledWidth() / 2f;
    final float height = scaledresolution.getScaledHeight() / 2f;

    //may come back to at a later date. 07/12/22

    /*final float texWidth = 1844 / 4f;
    final float texHeight = 303 / 4f;

    GlStateManager.pushMatrix();
    GlStateManager.color(1, 1, 1, 1);
    GlStateManager.enableBlend();
    GlStateManager.enableAlpha();
    mc.getTextureManager().bindTexture(new ResourceLocation("eternal/images/name.png"));
    Gui.drawModalRectWithCustomSizedTexture(
        width - (texWidth / 2f),
        scaledresolution.getScaledHeight() / 4f,
        0,
        0,
        (int) texWidth,
        (int) texHeight,
        texWidth,
        texHeight);
    GlStateManager.popMatrix();*/

    FontManager.getFontRenderer(FontType.ICIEL, 48).drawCenteredString("Loading Eternal..", width, height - 40, -1);
    Gui.drawRect(width - 150, height, width + 150, height + 25, Client.singleton().scheme().getBackground());
    Gui.drawRect(width - 149, height + 1, width - 149 + (298f / MAX_STAGES) * stage, height + 24, Client.singleton().scheme().getPrimary());

    GlStateManager.disableLighting();
    GlStateManager.disableFog();
    framebuffer.unbind();
    framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(516, 0.1F);
  }

}
