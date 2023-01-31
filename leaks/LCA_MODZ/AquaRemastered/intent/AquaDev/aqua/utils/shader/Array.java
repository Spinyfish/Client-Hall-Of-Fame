package intent.AquaDev.aqua.utils.shader;

import events.listeners.EventGlowArray;
import intent.AquaDev.aqua.Aqua;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Array {
   public static Array INSTANCE;
   static Minecraft MC = Minecraft.getMinecraft();
   public static Framebuffer pass = new Framebuffer(MC.displayWidth, MC.displayHeight, false);
   public static Framebuffer output = new Framebuffer(MC.displayWidth, MC.displayHeight, false);
   public static Framebuffer input = new Framebuffer(MC.displayWidth, MC.displayHeight, true);
   public static ShaderProgram blurProgram = new ShaderProgram("vertex.vert", "alphaBlurArray.glsl");

   public static void checkSetup() {
      input.checkSetup(MC.displayWidth, MC.displayHeight);
      pass.checkSetup(MC.displayWidth, MC.displayHeight);
      output.checkSetup(MC.displayWidth, MC.displayHeight);
   }

   public static void doBlurPass(int pass, int texture, Framebuffer out, int width, int height) {
      out.framebufferClear();
      out.bindFramebuffer(false);
      GL20.glUniform2f(blurProgram.uniform("direction"), (float)(1 - pass), (float)pass);
      GL11.glBindTexture(3553, texture);
      blurProgram.doRenderPass((float)width, (float)height);
   }

   public static void setupBlurUniforms() {
      GL20.glUniform2f(blurProgram.uniform("texelSize"), 1.0F / (float)MC.displayWidth, 1.0F / (float)MC.displayHeight);
      GL20.glUniform1i(blurProgram.uniform("texture"), 0);
      float sigma1 = (float)Aqua.setmgr.getSetting("ArraylistSigma").getCurrentNumber();
      float strength = (float)Aqua.setmgr.getSetting("ArraylistMultiplier").getCurrentNumber();
      GL20.glUniform1f(blurProgram.uniform("sigmaArray"), sigma1);
      GL20.glUniform1f(blurProgram.uniform("strength"), strength);
   }

   public void drawTexturedQuad(int texture, double width, double height) {
      INSTANCE = this;
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

   public static void onArray(EventGlowArray event) {
      event.setCancelled(true);
      input.bindFramebuffer(false);
      event.getRunnable().run();
      MC.getFramebuffer().bindFramebuffer(false);
   }
}
