package dev.eternal.client.module.impl.render;

import dev.eternal.client.Client;
import dev.eternal.client.event.EventPriority;
import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventDrawBlurred;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.event.events.EventRenderGui;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.render.engine.program.ShaderProgram;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL20.*;

@ModuleInfo(name = "Blur", description = "Visually pleasing.", category = Module.Category.RENDER)
public class ModuleBlur extends Module {

  private static final Framebuffer mask = new Framebuffer(1, 1, true);
  private final Framebuffer fboA = new Framebuffer(1, 1, false);
  private final Framebuffer fboB = new Framebuffer(1, 1, false);
  private final Framebuffer auxiliaryBuffer = new Framebuffer(1, 1, false);

  private ShaderProgram blurProgram = new ShaderProgram("vertex.glsl", "kawase.glsl", "");
  private ShaderProgram shadow = new ShaderProgram("vertex.glsl", "incremental_gaussian.glsl", "");
  private ShaderProgram dither = new ShaderProgram("vertex.glsl", "dither.frag", "");

  private static final Framebuffer[] buffers = new Framebuffer[6];
  private static final int[] POWERS_OF_TWO = {
      1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096
  };

  public double quality = 5;
  public double offset = 5;

  public static void drawBlurredRect(int x, int y, int x1, int y1, int color) {
    EventDrawBlurred event = new EventDrawBlurred(x, y, x1, y1, null);
    Client.singleton().eventBus().post(event);
    if (event.cancelled()) return;
    Gui.drawRect(x, y, x1, y1, color);
  }

  public static void drawBlurred(Runnable r) {
    EventDrawBlurred event = new EventDrawBlurred(r);
    Client.singleton().eventBus().post(event);
    if (event.cancelled()) return;
    r.run();
  }

  private void onBlurRect(EventDrawBlurred event) {
    event.cancelled(true);

    mask.bind(false);
    if (event.r() != null) {
      event.r().run();
    } else {
      Gui.drawRect(event.x(), event.y(), event.x1(), event.y1(), 0xFF000000);
    }
    mc.getFramebuffer().bind(false);
  }

  @Subscribe
  public void onRender2D(EventRenderGui eventRenderGui) {
    var scaledResolution = new ScaledResolution(mc);
//    this.offset = 15;
//    this.quality = 4;
    fboA.clear();
    fboA.bind(false);
    RenderUtil.drawTexturedQuad(
        (float) scaledResolution.getScaledWidth_double(),
        (float) scaledResolution.getScaledHeight_double(),
        mc.getFramebuffer().framebufferTexture);
    mc.getFramebuffer().bind(false);
  }

  @Subscribe(priority = EventPriority.FIRST)
  public void onPostRender2D(EventPostRenderGui eventPostRenderGui) {
    mc.entityRenderer.disableLightmap();
    this.renderBlur();
    mc.getFramebuffer().bind(false);
  }

  @Subscribe
  public void onEventRenderBlurred(EventDrawBlurred eventDrawBlurred) {
    this.onBlurRect(eventDrawBlurred);
  }

  private void renderBlur() {
    final ScaledResolution scaledResolution = new ScaledResolution(mc);
    final float width = (float) scaledResolution.getScaledWidth_double();
    final float height = (float) scaledResolution.getScaledHeight_double();

    this.checkSetup();
    this.drawBlur(width, height, (int) offset, (int) quality);

    mc.getFramebuffer().bind(true);
  }

  private void checkSetup() {
    var sr = new ScaledResolution(mc);
    float width = mc.displayWidth;
    float height = mc.displayHeight;
    fboA.checkSetupFbo((int) width, (int) height);
    fboB.checkSetupFbo((int) width, (int) height);
    mask.checkSetupFbo((int) width, (int) height);
  }

  @Override
  public void onEnable() {
    loadShaders();
    setup();
    super.onEnable();
  }

  private void loadShaders() {
    blurProgram = new ShaderProgram("vertex.glsl", "kawase.glsl", "");
    shadow = new ShaderProgram("vertex.glsl", "incremental_gaussian.glsl", "");
  }

  // Dual Kawase test
  private static ShaderProgram k_down = new ShaderProgram("vertex.glsl", "kawase/kawase_down.glsl", "");
  private static ShaderProgram k_up = new ShaderProgram("vertex.glsl", "kawase/kawase_up.glsl", "");

  public void setup() {
    k_down = new ShaderProgram("vertex.glsl", "kawase/kawase_down.glsl", "");
    k_up = new ShaderProgram("vertex.glsl", "kawase/kawase_up.glsl", "");

    for (int i = 0; i < buffers.length; i++) {
      // Framebuffers do not have to be deleted because they are initially null
      if (buffers[i] != null) {
        buffers[i].delete();
      }
      buffers[i] = new Framebuffer(1, 1, false);
      buffers[i].setFramebufferFilter(GL_LINEAR);
    }
  }

  public static void doPass(
      ShaderProgram program, int in, Framebuffer out, int offset, float width, float height) {
    out.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
    out.clear();
    out.bind(false);
    glBindTexture(GL_TEXTURE_2D, in);
    glUniform2f(program.uniform("u_texelSize"), (1F / out.width), (1F / out.height));
    glUniform2f(program.uniform("u_offset"), offset, offset);
    RenderUtil.drawTexturedQuad(width, height, in);
  }

  public static void setupKawaseBlurUniforms(ShaderProgram program) {
    glUniform1i(program.uniform("u_texture"), 0);
    glUniform1f(program.uniform("alpha"), 1.0F);
  }

  private void ditheringPass(int textureIn, Framebuffer out, float width, float height) {
    out.clear();
    out.bind(false);

    dither.use();
    glActiveTexture(GL_TEXTURE21);
    glBindTexture(GL_TEXTURE_2D, textureIn);
    glUniform1i(dither.uniform("texture"), 21);
    glUniform2f(dither.uniform("texelSize"), 1.0F / width, 1.0F / height);
    RenderUtil.drawTexturedQuad(width, height, textureIn);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, 0);
    dither.unUse();
  }

  /**
   * Draws the kawase blur, stencils out the mask fbo and does several passes through the shader
   *
   * <pre>{@code
   * // The passes at the end are very suitable for a nice kawase blur
   * drawBlur(width, height, 1, 2, 2, 3, 5, 9, 12, 19, 26);
   * }</pre>
   *
   * @param width  the scaled width
   * @param height the scaled height
   */
  private void drawBlur(float width, float height, int offset, int iterationCount) {
    fboB.checkSetupFbo(mc.displayWidth, mc.displayHeight);
    fboA.checkSetupFbo(mc.displayWidth, mc.displayHeight);
    auxiliaryBuffer.checkSetupFbo(mc.displayWidth, mc.displayHeight);

    for (int i = 0; i < buffers.length; i++) {
      int scale = POWERS_OF_TWO[i];
      buffers[i].checkSetupFbo(mc.displayWidth / scale, mc.displayHeight / scale);
      buffers[i].setFramebufferFilter(GL_LINEAR);
      buffers[i].setTextureWrap(GL_MIRRORED_REPEAT);
    }

    fboA.setFramebufferFilter(GL_LINEAR);
    fboB.setFramebufferFilter(GL_LINEAR);
    auxiliaryBuffer.setFramebufferFilter(GL_LINEAR);
    blur(fboA, fboA, false, offset, iterationCount, width, height);

    ditheringPass(fboA.framebufferTexture, fboB, width, height);

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0F, 0.0F, -1.0F);
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableLighting();
    RenderUtil.initStencil();

    // draw mask into stencil buffer
    RenderUtil.bindWriteStencilBuffer();
    RenderUtil.drawTexturedQuad(width, height, mask.framebufferTexture);

    // draw blurred buffer
    RenderUtil.bindReadStencilBuffer(1);
    RenderUtil.drawTexturedQuad(width, height, fboB.framebufferTexture);
    fboA.clear();
    fboA.checkSetupFbo(mc.displayWidth, mc.displayHeight);
    fboB.checkSetupFbo(mc.displayWidth, mc.displayHeight);
    if (true) {
      GL11.glDisable(GL11.GL_STENCIL_TEST);

      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(
          GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

      blur(mask, fboA, true, 3, iterationCount, width, height);

      // doing it by hand because the stencil buffer already has the right values in
      // it
      GL11.glEnable(GL11.GL_STENCIL_TEST);
      GL11.glStencilFunc(GL11.GL_EQUAL, 0, 1);
      GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
      mc.getFramebuffer().bind(false);
      RenderUtil.drawTexturedQuad(width, height, fboA.framebufferTexture);
    }
    RenderUtil.uninitStencilBuffer();
    GlStateManager.bindTexture(0);

    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.2F);
    GlStateManager.disableAlpha();
    GlStateManager.popMatrix();
    GlStateManager.disableBlend();

    mask.clear();
  }

  /**
   * Blurs the given framebuffer, the result is then stored in the 0th buuffer of buffers
   *
   * @param in             the Framebuffer to blur
   * @param useAlpha       if the blur should use the sampled alpha or set it to 1
   * @param offset         the offset
   * @param iterationCount the quality
   * @param width          self-explanatory
   * @param height         self-explanatory
   */
  public static void blur(
      Framebuffer in,
      Framebuffer out,
      boolean useAlpha,
      int offset,
      int iterationCount,
      float width,
      float height) {
    GlStateManager.enableAlpha();
    k_down.use();
    setupKawaseBlurUniforms(k_down);
    setupKawaseBlurUniforms(k_down);
    for (int i = 0; i < iterationCount; i++) {
      glUniform1f(k_down.uniform("alpha"), useAlpha ? 0.0F : 1.0F);
      doPass(
          k_down, i == 0 ? in.framebufferTexture : buffers[i].framebufferTexture, buffers[i + 1], offset, width, height);
    }

    k_up.use();
    setupKawaseBlurUniforms(k_up);
    for (int i = iterationCount; i > 0; i--) {
      glUniform1f(k_up.uniform("alpha"), useAlpha ? 0.0F : 1.0F);
      doPass(k_up, buffers[i].framebufferTexture, i == 1 ? out : buffers[i - 1], offset, width, height);
    }
    k_down.unUse();
    k_up.unUse();
    GlStateManager.disableAlpha();

  }

}
