package dev.eternal.client.ui.clickgui.eternal;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.ui.clickgui.ClickGui;
import dev.eternal.client.ui.clickgui.eternal.component.impl.CategoryComponent;
import dev.eternal.client.util.animation.Animation;
import dev.eternal.client.util.render.BlurUtil;
import dev.eternal.client.util.render.RenderUtil;
import dev.eternal.client.util.shader.Shader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hazsi & Eternal
 */
@Deprecated(forRemoval = true)
public class EternalClickGui extends ClickGui {

  // Colours
  public static final Color categoryTitleBackground = new Color(0, 0, 0, 200);
  public static final Color moduleBackground = new Color(0, 0, 0, 150);
  public static final Color color1 = new Color(Client.singleton().colour());
  public static final Color color2 = new Color(0xFFAA0000);

  private long initTime;
  private boolean closing;
  private Framebuffer framebuffer;
  private Shader shader = new Shader("clickgui/open.glsl");
  private final List<CategoryComponent> categoryComponents = new ArrayList<>();

  private final Animation shaderInAnimation = new Animation(1000);
//    private final Animation blurAnimation = new Animation(new EasingCubic(EasingDirection.INOUT), 1000);
//    private final Animation openingAnimation = new Animation(new EasingElastic(EasingDirection.OUT), 500);
//    private final Animation closingAnimation = new Animation(new EasingElastic(EasingDirection.IN), 500);

  public EternalClickGui() {
    // Add our categories
    int categoryX = 25;

    for (Module.Category category : Module.Category.values()) {
      categoryComponents.add(new CategoryComponent(category, categoryX, 20));
      categoryX += 150;
    }
  }

  @Override
  public void initGui() {
    closing = false;
    shader = new Shader("clickgui/open.glsl");
    framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    initTime = System.currentTimeMillis();
    shaderInAnimation.startTime(initTime);
//        openingAnimation.setStartTime(System.currentTimeMillis() - 150);
//        blurAnimation.setStartTime(System.currentTimeMillis());
//        closingAnimation.setStartTime(-1);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {

    framebuffer.clear();
    framebuffer.bind(false);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    // Blur and dark background
    float blurAmount = 5;

    BlurUtil.setupBuffers();
    BlurUtil.preBlur();
    BlurUtil.renderGaussianBlur(framebuffer, blurAmount, 2, false);
    BlurUtil.postBlur(framebuffer, blurAmount, 2);

    // Draw our categories

    for (CategoryComponent component : categoryComponents) {
      component.draw(mouseX, mouseY, partialTicks);
    }

    GlStateManager.disableBlend();

    float time = (float) (System.currentTimeMillis() - initTime) / 500F;
    if (closing) time = (initTime - System.currentTimeMillis()) / 500F + 1;

    mc.getFramebuffer().bind(false);
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
    shader.useShader();
    shader.setUniform1i("tex", 0);
    shader.setUniform2f("res", mc.displayWidth, mc.displayHeight);
    shader.setUniform1f("time", time);
    RenderUtil.renderImage(0, 0, mc.displayWidth / 2f, mc.displayHeight / 2f);
    shader.stopShader();

    if (time < 0)
      mc.displayGuiScreen(null);

  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    for (CategoryComponent component : categoryComponents) {
      component.keyPress(typedChar, keyCode);
    }
    if (keyCode == 1 && !closing) { // Escape
      initTime = System.currentTimeMillis();
      closing = true;
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    for (CategoryComponent component : categoryComponents) {
      component.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    for (CategoryComponent component : categoryComponents) {
      component.mouseReleased(mouseX, mouseY, state);
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void save() {

  }

  @Override
  public void load() {

  }
}
