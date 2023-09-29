package dev.eternal.client.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL20;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.enableBlend;
import static net.minecraft.client.renderer.OpenGlHelper.glBlendFunc;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform2f;

/**
 * Renders a Gaussian blur using an OpenGL Shader. In a future version, this may be
 * migrated to a faster Kawase blur system with down sampling to improve FPS, but this
 * works for the time being. The FPS impact from using this blur is minimal however
 * still not nullable.
 *
 * <br><br>
 * Unlike most blur implementations in most clients, this is 100% static, the BlurUtil
 * does not need to be instantiated or anything similar. Notably, this, along with
 * the provided Gaussian blur implementation also supports the usage of float values
 * being passed in as a blur radius, breaking almost every other shader in other clients.
 * This allows for smooth blur "fading" as a transition/animation.
 *
 * <br><br>
 * Usage:
 * <li>Call the {@code setupBuffers()} method to set up frame buffers</li>
 * <li>Call the {@code preBlur()} method</li>
 * <li>Call the main {@code renderGaussianBlur(float radius, float compression, boolean backTrackBlurOnScreen)}
 * method. This method will make two passes to the {@code ShaderUtil} instance for the provided shader to draw
 * the blur. The {@code radius} and {@code compression} parameters determine the strength of the blur while the
 * {@code backtrackBlurOnScreen} boolean will pre-dev.eternal.client.render a GL Quad. <b>Don't use {@code backtrackBlurOnScreen}
 * if you don't know what you're doing.</b></li>
 * <li>Call the {@code postBlur(float radius, float compression)} method. You <b>must</b> use the same radius
 * and compression as used in the call to {@code renderGaussianBlur}</li>
 *
 * <br>
 *
 * @author Cedo, Hazsi, milse113
 * @see ShaderUtil
 */
public class BlurUtil {
  private static final Minecraft mc = Minecraft.getMinecraft();

  public static ShaderUtil gaussianProgram = new ShaderUtil("eternal/shader/impl/gaussian.frag");
  public static ScaledResolution sr = new ScaledResolution(mc);

  public static Framebuffer toBlurBuffer = new Framebuffer(1, 1, false);
  public static Framebuffer blurredBuffer = new Framebuffer(1, 1, false);
  private static Framebuffer blurPass2 = new Framebuffer(1, 1, false);
  private static Framebuffer blurPass3 = new Framebuffer(1, 1, false);


  public static void preBlur() {
    preBlur(mc.getFramebuffer());
  }

  public static void preBlur(Framebuffer fboIn) {
    toBlurBuffer.bind(false);
    setupBuffers();
    enableBlend();
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

    fboIn.bind(false);
    checkSetupFBO(fboIn);

    glClear(GL_STENCIL_BUFFER_BIT);
    glEnable(GL_STENCIL_TEST);
    glStencilFunc(GL_ALWAYS, 1, 1);
    glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
    glColorMask(false, false, false, false);
  }

  public static void renderGaussianBlur(final float radius, final float compression, final boolean backTrackBlurOnScreen) {
    renderGaussianBlur(mc.getFramebuffer(), radius, compression, backTrackBlurOnScreen);
  }

  public static void renderGaussianBlur(final Framebuffer fboIn, final float radius, final float compression, final boolean backTrackBlurOnScreen) {
    renderGaussianBlur(fboIn, mc.getFramebuffer(), radius, compression, backTrackBlurOnScreen);
  }

  public static void renderGaussianBlur(final Framebuffer fboIn, final Framebuffer blurBuffer, final float radius, final float compression, final boolean backTrackBlurOnScreen) {
    enableBlend();
    color(1, 1, 1, 1);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

    fboIn.bind(false);
    checkSetupFBO(fboIn);

    glClear(GL_STENCIL_BUFFER_BIT);
    glEnable(GL_STENCIL_TEST);

    if (backTrackBlurOnScreen) {
      glStencilFunc(GL_ALWAYS, 1, 1);
      glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
      glColorMask(false, false, false, false);

      ShaderUtil.drawQuads(sr);
    }

    glColorMask(true, true, true, true);
    glStencilFunc(GL_EQUAL, 1, 1);
    glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

    // First horizontal pass
    gaussianProgram.init();
    setupBlurUniforms(radius);
    blurredBuffer.clear();
    blurredBuffer.bind(false);
    glUniform2f(gaussianProgram.getUniform("direction"), compression, 0);
    glBindTexture(GL_TEXTURE_2D, blurBuffer.framebufferTexture);
    ShaderUtil.drawQuads(sr);
    blurredBuffer.unbind();

    // Second vertical pass
    gaussianProgram.init();
    setupBlurUniforms(radius);
    fboIn.bind(false);
    glUniform2f(gaussianProgram.getUniform("direction"), 0, compression);
    glBindTexture(GL_TEXTURE_2D, blurredBuffer.framebufferTexture);
    ShaderUtil.drawQuads(sr);

    // Done
    gaussianProgram.unload();
    glDisable(GL_STENCIL_TEST);
  }

  /**
   * @param bufferIn    The FrameBuffer to dev.eternal.client.render the blur to
   * @param blurBuffer  The FrameBuffer to blur
   * @param radius      The radius of the guassian kernel
   * @param compression Compression
   */
  public static void postBlur(final Framebuffer bufferIn, final Framebuffer blurBuffer, final float radius, final float compression) {
    final ScaledResolution sr = new ScaledResolution(mc);

    glColorMask(true, true, true, true);
    glStencilFunc(GL_EQUAL, 1, 1);
    glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

    // First horizintal pass
    gaussianProgram.init();
    setupBlurUniforms(radius);
    blurredBuffer.clear();
    blurredBuffer.bind(false);
    glUniform2f(gaussianProgram.getUniform("direction"), compression, 0);
    glBindTexture(GL_TEXTURE_2D, blurBuffer.framebufferTexture);
    ShaderUtil.drawQuads(sr);
    blurredBuffer.unbind();

    // Second vertical pass
    gaussianProgram.init();
    setupBlurUniforms(radius);
    bufferIn.bind(false);
    glUniform2f(gaussianProgram.getUniform("direction"), 0, compression);
    glBindTexture(GL_TEXTURE_2D, blurredBuffer.framebufferTexture);
    ShaderUtil.drawQuads(sr);

    // Done
    gaussianProgram.unload();
    glDisable(GL_STENCIL_TEST);
  }

  public static void postBlur(final float radius, final float compression) {
    postBlur(mc.getFramebuffer(), mc.getFramebuffer(), radius, compression);
  }

  public static void postBlur(final Framebuffer bufferIn, final float radius, final float compression) {
    postBlur(bufferIn, mc.getFramebuffer(), radius, compression);
  }

  public static void setupBuffers() {
    // Check if the display size has changed, and update the buffers if so
    if (mc.displayWidth != blurredBuffer.width || mc.displayHeight != blurredBuffer.height) {
      blurredBuffer.delete();
      blurredBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

      blurPass2.delete();
      blurPass2 = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

      blurPass3.delete();
      blurPass3 = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

      toBlurBuffer.delete();
      toBlurBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
    }
  }

  private static void setupBlurUniforms(final float radius) {
    GL20.glUniform1i(gaussianProgram.getUniform("texture"), 0);
    GL20.glUniform2f(gaussianProgram.getUniform("texelSize"), 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
    GL20.glUniform1f(gaussianProgram.getUniform("radius"), MathHelper.ceiling_float_int(2 * radius));
  }

  private static void checkSetupFBO(Framebuffer framebuffer) {
    if (framebuffer != null && framebuffer.depthBuffer > -1) {
      EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.depthBuffer);
      int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();

      EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
      EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);

      framebuffer.depthBuffer = -1;
    }
  }
}
