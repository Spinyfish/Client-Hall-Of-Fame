package intent.AquaDev.aqua.utils.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public final class ShaderStencilUtil {
   private static final Minecraft MC = Minecraft.getMinecraft();

   public static void checkSetupFBO(Framebuffer framebuffer) {
      if (framebuffer != null && framebuffer.depthBuffer > -1) {
         setupFBO(framebuffer);
         framebuffer.depthBuffer = -1;
      }
   }

   public static void setupFBO(Framebuffer framebuffer) {
      EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.depthBuffer);
      int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
      EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferID);
      EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, MC.displayWidth, MC.displayHeight);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferID);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferID);
   }

   public static void initStencil() {
      initStencil(MC.getFramebuffer());
   }

   public static void initStencil(Framebuffer framebuffer) {
      framebuffer.bindFramebuffer(false);
      checkSetupFBO(framebuffer);
      GL11.glClear(1024);
      GL11.glEnable(2960);
   }

   public static void bindWriteStencilBuffer() {
      GL11.glStencilFunc(519, 1, 1);
      GL11.glStencilOp(7681, 7681, 7681);
      GL11.glColorMask(false, false, false, false);
   }

   public static void bindReadStencilBuffer(int ref) {
      GL11.glColorMask(true, true, true, true);
      GL11.glStencilFunc(514, ref, 1);
      GL11.glStencilOp(7680, 7680, 7680);
   }

   public static void uninitStencilBuffer() {
      GL11.glDisable(2960);
   }
}
