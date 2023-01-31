package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventGlow;
import events.listeners.EventPostRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.shader.ShaderProgram;
import intent.AquaDev.aqua.utils.shader.ShaderStencilUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shadow extends Module {
   Minecraft MC = Minecraft.getMinecraft();
   private final Framebuffer pass = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, false);
   private final Framebuffer output = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, false);
   private final Framebuffer input = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, true);
   private ShaderProgram blurProgram = new ShaderProgram("vertex.vert", "alphaBlur.glsl");

   public Shadow() {
      super("Shadow", Module.Type.Visual, "Shadow", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Sigma", this, 6.0, 6.0, 20.0, false));
   }

   public static void drawGlow(Runnable runnable, boolean renderTwice) {
      EventGlow event = new EventGlow(runnable);
      Aqua.INSTANCE.onEvent(event);
      if (!event.isCancelled() || renderTwice) {
         runnable.run();
      }
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPostRender2D) {
         drawGlow(() -> Gui.drawRect(-2001, -2001, -2000, -2000, Color.red.getRGB()), false);
         this.checkSetup();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableDepth();
         GlStateManager.enableTexture2D();
         GlStateManager.disableLighting();
         ScaledResolution sr = new ScaledResolution(this.MC);
         double screenWidth = sr.getScaledWidth_double();
         double screenHeight = sr.getScaledHeight_double();
         this.blurProgram.init();
         this.setupBlurUniforms();
         this.doBlurPass(0, this.input.framebufferTexture, this.pass, (int)screenWidth, (int)screenHeight);
         this.doBlurPass(1, this.pass.framebufferTexture, this.output, (int)screenWidth, (int)screenHeight);
         this.blurProgram.uninit();
         ShaderStencilUtil.initStencil();
         ShaderStencilUtil.bindWriteStencilBuffer();
         this.drawTexturedQuad(this.input.framebufferTexture, screenWidth, screenHeight);
         ShaderStencilUtil.bindReadStencilBuffer(0);
         this.drawTexturedQuad(this.output.framebufferTexture, screenWidth, screenHeight);
         ShaderStencilUtil.uninitStencilBuffer();
         GlStateManager.bindTexture(0);
         GlStateManager.alphaFunc(516, 0.2F);
         GlStateManager.disableAlpha();
         GlStateManager.popMatrix();
         GlStateManager.disableBlend();
         this.input.framebufferClear();
         this.MC.getFramebuffer().bindFramebuffer(false);
      } else if (event instanceof EventGlow) {
         this.onGlowEvent((EventGlow)event);
      }
   }

   public void onGlowEvent(EventGlow event) {
      event.setCancelled(true);
      this.input.bindFramebuffer(false);
      event.getRunnable().run();
      this.MC.getFramebuffer().bindFramebuffer(false);
   }

   @Override
   public void onEnable() {
      this.blurProgram = new ShaderProgram("vertex.vert", "alphaBlur.glsl");
      super.onEnable();
   }

   public void checkSetup() {
      this.input.checkSetup(this.MC.displayWidth, this.MC.displayHeight);
      this.pass.checkSetup(this.MC.displayWidth, this.MC.displayHeight);
      this.output.checkSetup(this.MC.displayWidth, this.MC.displayHeight);
   }

   public void doBlurPass(int pass, int texture, Framebuffer out, int width, int height) {
      out.framebufferClear();
      out.bindFramebuffer(false);
      GL20.glUniform2f(this.blurProgram.uniform("direction"), (float)(1 - pass), (float)pass);
      GL11.glBindTexture(3553, texture);
      this.blurProgram.doRenderPass((float)width, (float)height);
   }

   public void setupBlurUniforms() {
      GL20.glUniform2f(this.blurProgram.uniform("texelSize"), 1.0F / (float)this.MC.displayWidth, 1.0F / (float)this.MC.displayHeight);
      GL20.glUniform1i(this.blurProgram.uniform("texture"), 0);
      float strength = 1.0F;
      GL20.glUniform1f(this.blurProgram.uniform("strength"), 1.0F);
   }

   private void drawTexturedQuad(int texture, double width, double height) {
      GlStateManager.enableBlend();
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
