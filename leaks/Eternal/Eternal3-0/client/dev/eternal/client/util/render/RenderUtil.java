package dev.eternal.client.util.render;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30C.glFramebufferTexture2D;

import dev.eternal.client.render.engine.RenderableRenderer;
import dev.eternal.client.render.engine.program.ShaderProgram;
import dev.eternal.client.render.engine.renderable.RenderableType;
import dev.eternal.client.render.engine.renderable.renderables.RoundedRectangleRenderable;
import dev.eternal.client.util.shader.Shader;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Function;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

@UtilityClass
@SuppressWarnings("unused")
public class RenderUtil {

  private static final Minecraft MC = Minecraft.getMinecraft();
  private static final FontRenderer FR = MC.fontRendererObj;
  private static final Shader gaussShader = new Shader("impl/gaussian.frag");
  private static final Framebuffer fboA = new Framebuffer(1, 1, false);
  private static final Framebuffer fboB = new Framebuffer(1, 1, false);
  public static boolean COLORING_PASS = false;
  public static int DEPTH_TEXTURE = 0;
  public static int DEPTH_COLOR_TEXTURE = 0;
  public static int DEPTH_FBO = 0;
  public static ShaderProgram BLUR_FOG = new ShaderProgram("vertex.glsl", "depth/test.glsl", "");
  private static ShaderProgram COLORING_PROGRAM =
      new ShaderProgram("vertex.glsl", "esp/coloring.glsl", "");
  private static final int COLOR_UNIFORM = COLORING_PROGRAM.uniform("color");

  static {
    DEPTH_TEXTURE = glGenTextures();

    glBindTexture(GL_TEXTURE_2D, DEPTH_TEXTURE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
    glTexImage2D(
        GL_TEXTURE_2D,
        0,
        GL_DEPTH_COMPONENT24,
        MC.displayWidth,
        MC.displayHeight,
        0,
        GL_DEPTH_COMPONENT,
        GL_FLOAT,
        (ByteBuffer) null);
    glBindTexture(GL_TEXTURE_2D, 0);

    DEPTH_FBO = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, DEPTH_FBO);

    glFramebufferTexture2D(
        GL_FRAMEBUFFER,
        GL_COLOR_ATTACHMENT0,
        GL_TEXTURE_2D,
        DEPTH_COLOR_TEXTURE,
        0 /*mipmap level*/);

    glFramebufferTexture2D(
        GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, DEPTH_TEXTURE, 0 /*mipmap level*/);
  }

  public static void drawSmallString(String text, double x, double y, int colour) {
    GL11.glScaled(0.5, 0.5, 0.5);
    FR.drawStringWithShadow(text, x * 2.000001, y * 2.000001, colour);
    GL11.glScaled(2, 2, 2);
  }

  public static void drawTexturedQuad(double width, double height, int texture) {
    glBindTexture(GL_TEXTURE_2D, texture);
    glBegin(GL_QUADS);
    {
      glTexCoord2f(0, 1);
      glVertex2d(0, 0);

      glTexCoord2f(0, 0);
      glVertex2d(0, height);

      glTexCoord2f(1, 0);
      glVertex2d(width, height);

      glTexCoord2f(1, 1);
      glVertex2d(width, 0);
    }
    glEnd();
  }

  public static void initStencil() {
    initStencil(MC.getFramebuffer());
  }

  public static void checkSetupFBO(Framebuffer framebuffer) {
    if (framebuffer != null) {
      if (framebuffer.depthBuffer > -1) {
        setupFBO(framebuffer);
        framebuffer.depthBuffer = -1;
      }
    }
  }

  public static void setupFBO(Framebuffer framebuffer) {
    EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.depthBuffer);
    final int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
    EXTFramebufferObject.glBindRenderbufferEXT(
        EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
    EXTFramebufferObject.glRenderbufferStorageEXT(
        EXTFramebufferObject.GL_RENDERBUFFER_EXT,
        EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT,
        MC.displayWidth,
        MC.displayHeight);
    EXTFramebufferObject.glFramebufferRenderbufferEXT(
        EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
        EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
        EXTFramebufferObject.GL_RENDERBUFFER_EXT,
        stencilDepthBufferID);
    EXTFramebufferObject.glFramebufferRenderbufferEXT(
        EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
        EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
        EXTFramebufferObject.GL_RENDERBUFFER_EXT,
        stencilDepthBufferID);
  }

  public static void initStencil(Framebuffer framebuffer) {
    framebuffer.bind(false);
    checkSetupFBO(framebuffer);
    glClear(GL_STENCIL_BUFFER_BIT);
    glEnable(GL_STENCIL_TEST);
  }

  public static void bindWriteStencilBuffer() {
    glStencilFunc(GL_ALWAYS, 1, 1);
    glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
    glColorMask(false, false, false, false);
  }

  public static void bindReadStencilBuffer(int ref) {
    glColorMask(true, true, true, true);
    glStencilFunc(GL_EQUAL, ref, 1);
    glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
  }

  public static void uninitStencilBuffer() {
    glDisable(GL_STENCIL_TEST);
  }

  public static void blurFramebuffer(
      Framebuffer inBuffer, Framebuffer outBuffer, float radius, float xDir, float yDir) {
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL_TEXTURE_2D, inBuffer.framebufferTexture);
    outBuffer.bind(false);
    gaussShader.useShader();
    gaussShader.setUniform1i("texture", 0);
    gaussShader.setUniform2f("texelSize", 1.0F / MC.displayWidth, 1.0F / MC.displayHeight);
    gaussShader.setUniform2f("direction", xDir, yDir);
    gaussShader.setUniform1f("radius", radius);
    renderImage(0, 0, MC.displayWidth / 2f, MC.displayHeight / 2F);
    gaussShader.stopShader();
  }

  public static void renderImage(float x, float y, float width, float height) {
    glBegin(GL_QUADS);
    glTexCoord2f(0, 1);
    glVertex2d(x, y);

    glTexCoord2f(0, 0);
    glVertex2d(x, y + height);

    glTexCoord2f(1, 0);
    glVertex2d(x + width, y + height);

    glTexCoord2f(1, 1);
    glVertex2d(x + width, y);
    glEnd();
  }

  public static void pre3D() {
    GL11.glPushMatrix();
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glShadeModel(GL11.GL_SMOOTH);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_LINE_SMOOTH);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDepthMask(false);
    GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
  }

  public static void post3D() {
    GL11.glDepthMask(true);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LINE_SMOOTH);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glPopMatrix();
  }

  public void drawRect(float left, float top, float right, float bottom, int color) {
    if (left < right) {
      var i = left;
      left = right;
      right = i;
    }

    if (top < bottom) {
      var j = top;
      top = bottom;
      bottom = j;
    }

    var red = (float) (color >> 16 & 255) / 255.0F;
    var green = (float) (color >> 8 & 255) / 255.0F;
    var blue = (float) (color & 255) / 255.0F;
    var alpha = (float) (color >> 24 & 255) / 255.0F;

    GlStateManager.pushMatrix();
    begin2D();
    GlStateManager.color(red, green, blue, alpha);

    GL11.glBegin(GL_QUADS);
    GL11.glVertex2f(left, bottom);
    GL11.glVertex2f(right, bottom);
    GL11.glVertex2f(right, top);
    GL11.glVertex2f(left, top);
    GL11.glEnd();

    end2D();
    GlStateManager.color(1, 1, 1, 1);
    GlStateManager.popMatrix();
  }

  public static void bindFboTexture(Framebuffer framebuffer) {
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
  }

  public void drawRoundedRect(double x, double y, double x1, double y1, float radius, int insideC) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    RenderableRenderer.of(RenderableType.ROUNDED_RECT)
        .batching()
        .apply(
            context ->
                context.push(
                    RoundedRectangleRenderable.of(
                        (float) x, (float) y, (float) (x1 - x), (float) (y1 - y), radius, insideC)))
        .render();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }

  public void drawHorizontalLine(float x, float y, float y1, int color) {
    drawRect(x, y1, y + 1.0f, y1 + 1.0f, color);
  }

  public void drawVerticalLine(float x, float y, float y1, int color) {
    drawRect(x, y + 1.0f, x + 1.0f, y1, color);
  }

  public void drawRect(float x, float y2, float x1, float y1) {
    GL11.glBegin(GL_QUADS);
    GL11.glVertex2f(x, y1);
    GL11.glVertex2f(x1, y1);
    GL11.glVertex2f(x1, y2);
    GL11.glVertex2f(x, y2);
    GL11.glEnd();
  }

  public void begin2D() {
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_TRUE, GL_FALSE);
  }

  public void end2D() {
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }

  public void drawGradientRect(
      double x, double y, double width, double height, Color left, Color right) {
    Scope.enable(GL11.GL_BLEND)
        .scope(
            () ->
                Scope.disable(GL11.GL_DEPTH_TEST, GL11.GL_TEXTURE_2D, GL11.GL_ALPHA_TEST)
                    .scope(
                        () -> {
                          GL11.glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                          GL11.glShadeModel(GL11.GL_SMOOTH);
                          GL11.glPushMatrix();

                          GL11.glBegin(GL11.GL_TRIANGLE_FAN);
                          GL11.glColor4f(
                              left.getRed() / 255F,
                              left.getGreen() / 255F,
                              left.getBlue() / 255F,
                              left.getAlpha());
                          GL11.glVertex2f((float) x, (float) y);
                          GL11.glVertex2f((float) x, (float) (y + height));
                          GL11.glColor4f(
                              right.getRed() / 255F,
                              right.getGreen() / 255F,
                              right.getBlue() / 255F,
                              right.getAlpha());
                          GL11.glVertex2f((float) (x + width), (float) (y + height));
                          GL11.glVertex2f((float) (x + width), (float) y);
                          GL11.glEnd();

                          GL11.glPopMatrix();
                        }));
  }

  public static void drawFace(
      int x,
      int y,
      float u,
      float v,
      int uWidth,
      int vHeight,
      int width,
      int height,
      float tileWidth,
      float tileHeight,
      AbstractClientPlayer target) {
    try {
      ResourceLocation skin = target.getLocationSkin();
      Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glColor4f(1, 1, 1, 1);
      Gui.drawScaledCustomSizeModalRect(
          x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
      GL11.glDisable(GL11.GL_BLEND);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void glResets() {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableBlend();
    GlStateManager.disableLighting();
    GlStateManager.enableAlpha();
  }

  public static void drawRect(double left, double top, double right, double bottom, float opacity) {
    if (left < right) {
      double i = left;
      left = right;
      right = i;
    }

    if (top < bottom) {
      double j = top;
      top = bottom;
      bottom = j;
    }

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(.1F, .1F, .1F, opacity);
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(left, bottom, 0.0D).endVertex();
    worldrenderer.pos(right, bottom, 0.0D).endVertex();
    worldrenderer.pos(right, top, 0.0D).endVertex();
    worldrenderer.pos(left, top, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }

  public void entityRenderer(Minecraft mc, Entity entity, Runnable runnable) {
    var x =
        (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks)
            - RenderManager.renderPosX;
    var y =
        (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks)
            - RenderManager.renderPosY;
    var z =
        (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks)
            - RenderManager.renderPosZ;
    GL11.glPushMatrix();
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GlStateManager.translate(x, y, z);
    runnable.run();
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glColor3f(1, 1, 1);
    GL11.glPopMatrix();
  }

  public Color fade(Color color, int count) {
    float[] hsb = new float[3];
    Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

    float brightness =
        Math.abs(
            ((float) (System.currentTimeMillis() % 2000L) / 1000.0F + 100 / (float) count * 2.0F)
                % 2
                - 1.0F);

    brightness = 0.5F + 0.5F * brightness;
    hsb[2] = brightness % 2.0F;

    return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
  }

  public static void prepareScissorBox(double x, double y, double x2, double y2) {
    ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
    int factor = scale.getScaleFactor();
    GL11.glScissor(
        (int) (x * (float) factor),
        (int) (((float) scale.getScaledHeight() - y2) * (float) factor),
        (int) ((x2 - x) * (float) factor),
        (int) ((y2 - y) * (float) factor));
  }

  public void drawPlayerFace(Entity entity, float x, float y, float w, float h) {
    if (entity != null) {
      ResourceLocation rs =
          MC.getNetHandler().getPlayerInfo(entity.getUniqueID()) == null
              ? null
              : MC.getNetHandler().getPlayerInfo(entity.getUniqueID()).getLocationSkin();
      if (rs != null) {
        MC.getTextureManager().bindTexture(rs);
        Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, w, h, 64, 64);
      } else {
        try {
          drawPlayerFaceOther(entity.getName(), x, y, (int) w, (int) h);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void drawPlayerFaceOther(String name, float x, float y, int w, int h) throws IOException {
    AbstractClientPlayer.getDownloadImageSkin(AbstractClientPlayer.getLocationSkin(name), name)
        .loadTexture(Minecraft.getMinecraft().getResourceManager());

    Minecraft.getMinecraft()
        .getTextureManager()
        .bindTexture(AbstractClientPlayer.getLocationSkin(name));
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glColor4f(1, 1, 1, 1);

    Gui.drawModalRectWithCustomSizedTexture(x, y, w, h, w, h, 192, 192);
    Gui.drawModalRectWithCustomSizedTexture(x, y, 120, h, w, h, 192, 192);

    GL11.glDisable(GL11.GL_BLEND);
  }

  public static void beginColoringPass() {
    RenderUtil.COLORING_PASS = true;
    COLORING_PROGRAM.use();
  }

  public static void setColor(float red, float green, float blue) {
    GL20.glUniform3f(COLOR_UNIFORM, red, green, blue);
  }

  public static void endColoringPass() {
    COLORING_PROGRAM.unUse();
    RenderUtil.COLORING_PASS = false;
  }

  public static void renderEntities(
      ArrayList<Object> entities, Framebuffer targetFbo, Function<Object, float[]> colorProvider) {
    targetFbo.clear();
    targetFbo.bind(false);

    GlStateManager.disableLighting();

    RendererLivingEntity.NAME_TAG_RANGE = 0.0F;
    RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0.0F;
    boolean flag = MC.gameSettings.entityShadows;
    MC.gameSettings.entityShadows = false;
    COLORING_PROGRAM = new ShaderProgram("vertex.glsl", "esp/coloring.glsl", "");
    final float time = (System.currentTimeMillis() - 100) / 1000F;
    beginColoringPass();
    for (Object entity : entities) {
      final float[] color = colorProvider.apply(entity);
      setColor(color[0], color[1], color[2]);
      glUniform1f(COLORING_PROGRAM.uniform("time"), time);
      if (entity instanceof Entity) {
        var ent = ((Entity) entity);
        MC.getRenderManager()
            .renderEntityStatic((Entity) entity, MC.timer.renderPartialTicks, true);
      } else if (entity instanceof TileEntity) {
        TileEntityRendererDispatcher.instance.renderTileEntity(
            (TileEntity) entity, MC.timer.renderPartialTicks, -1);
      }
    }
    MC.gameSettings.entityShadows = flag;
    endColoringPass();

    RendererLivingEntity.NAME_TAG_RANGE = 64.0F;
    RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32.0F;

    GlStateManager.bindTexture(0);
    GlStateManager.disableLighting();
  }
}
