package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventBlur;
import events.listeners.EventPostRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.shader.ShaderProgram;
import intent.AquaDev.aqua.utils.shader.ShaderStencilUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Blur extends Module {
   Minecraft MC = Minecraft.getMinecraft();
   private final Framebuffer fboA = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, false);
   private final Framebuffer fboB = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, false);
   private final Framebuffer maskBuffer = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, true);
   private ShaderProgram blurProgram = new ShaderProgram("vertex.vert", "blur.glsl");
   private ShaderProgram blurProgram2 = new ShaderProgram("vertex.vert", "alphaBlur.glsl");

   public Blur() {
      super("Blur", Module.Type.Visual, "Blur", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Sigma", this, 6.0, 4.0, 20.0, false));
      Aqua.setmgr.register(new Setting("Mode", this, "Gaussian", new String[]{"Gaussian", "GaussianCleaner"}));
   }

   public static void drawBlurred(Runnable runnable, boolean renderTwice) {
      EventBlur event = new EventBlur(runnable);
      Aqua.INSTANCE.onEvent(event);
      if ((!event.isCancelled() || renderTwice) && Display.isActive()) {
         runnable.run();
      }
   }

   @Override
   public void onEnable() {
      if (Aqua.setmgr.getSetting("BlurMode").getCurrentMode().equalsIgnoreCase("Gaussian")) {
         this.blurProgram = new ShaderProgram("vertex.vert", "blur.glsl");
      }

      if (Aqua.setmgr.getSetting("BlurMode").getCurrentMode().equalsIgnoreCase("GaussianCleaner")) {
         this.blurProgram2 = new ShaderProgram("vertex.vert", "alphaBlur.glsl");
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPostRender2D) {
         this.onRender2D();
      } else if (event instanceof EventBlur) {
         this.onBlurredEvent((EventBlur)event);
      }
   }

   public void onBlurredEvent(EventBlur event) {
      event.setCancelled(true);
      this.maskBuffer.bindFramebuffer(false);
      event.getBlurredFunction().run();
      mc.getFramebuffer().bindFramebuffer(false);
   }

   public void checkSetup() {
      this.fboA.checkSetup(mc.displayWidth, mc.displayHeight);
      this.fboB.checkSetup(mc.displayWidth, mc.displayHeight);
      this.maskBuffer.checkSetup(mc.displayWidth, mc.displayHeight);
   }

   public void onRender2D() {
      this.checkSetup();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.0F, -1.0F);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.0F);
      GlStateManager.enableDepth();
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      ScaledResolution sr = new ScaledResolution(mc);
      float width = (float)sr.getScaledWidth_double();
      float height = (float)sr.getScaledHeight_double();
      if (Aqua.setmgr.getSetting("BlurMode").getCurrentMode().equalsIgnoreCase("Gaussian")) {
         this.blurProgram.init();
      } else {
         this.blurProgram2.init();
      }

      this.blurProgram.init();
      this.setupBlurUniforms();
      this.doBlurPass(0, mc.getFramebuffer().framebufferTexture, this.fboA, (int)width, (int)height);
      this.doBlurPass(1, this.fboA.framebufferTexture, this.fboB, (int)width, (int)height);
      if (Aqua.setmgr.getSetting("BlurMode").getCurrentMode().equalsIgnoreCase("Gaussian")) {
         this.blurProgram.uninit();
      } else {
         this.blurProgram2.uninit();
      }

      ShaderStencilUtil.initStencil();
      ShaderStencilUtil.bindWriteStencilBuffer();
      this.drawTexturedQuad(this.maskBuffer.framebufferTexture, (double)width, (double)height);
      ShaderStencilUtil.bindReadStencilBuffer(1);
      this.drawTexturedQuad(this.fboB.framebufferTexture, (double)width, (double)height);
      ShaderStencilUtil.uninitStencilBuffer();
      mc.getFramebuffer().bindFramebuffer(false);
      GlStateManager.bindTexture(0);
      GlStateManager.alphaFunc(516, 0.2F);
      GlStateManager.disableAlpha();
      GlStateManager.popMatrix();
      GlStateManager.disableBlend();
      this.maskBuffer.framebufferClear();
      mc.getFramebuffer().bindFramebuffer(false);
   }

   public void doBlurPass(int pass, int texture, Framebuffer out, int width, int height) {
      out.framebufferClear();
      out.bindFramebuffer(false);
      GL20.glUniform2f(this.blurProgram.uniform("direction"), (float)(1 - pass), (float)pass);
      GL11.glBindTexture(3553, texture);
      if (Aqua.setmgr.getSetting("BlurMode").getCurrentMode().equalsIgnoreCase("Gaussian")) {
         this.blurProgram.doRenderPass((float)width, (float)height);
      } else {
         this.blurProgram2.doRenderPass((float)width, (float)height);
      }
   }

   public void setupBlurUniforms() {
      if (Aqua.setmgr.getSetting("BlurMode").getCurrentMode().equalsIgnoreCase("Gaussian")) {
         GL20.glUniform2f(this.blurProgram.uniform("texelSize"), 1.0F / (float)this.MC.displayWidth, 1.0F / (float)this.MC.displayHeight);
         GL20.glUniform1i(this.blurProgram.uniform("texture"), 0);
         float sigma = (float)Aqua.setmgr.getSetting("BlurSigma").getCurrentNumber();
         GL20.glUniform1f(this.blurProgram.uniform("sigma"), sigma);
      } else {
         GL20.glUniform2f(this.blurProgram2.uniform("texelSize"), 1.0F / (float)this.MC.displayWidth, 1.0F / (float)this.MC.displayHeight);
         GL20.glUniform1i(this.blurProgram2.uniform("texture"), 0);
      }
   }

   private void drawTexturedQuad(int texture, double width, double height) {
      GL11.glBindTexture(3553, texture);
      GL11.glBegin(7);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(0.0, 0.0);
      GL11.glTexCoord2d(0.0, 0.0);
      GL11.glVertex2d(0.0, height);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(width, height);
      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d(width, 0.0);
      GL11.glEnd();
   }
}
