package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventGlowESP;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import events.listeners.EventRender3D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.shader.ShaderProgram;
import intent.AquaDev.aqua.utils.shader.ShaderStencilUtil;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderESP extends Module {
   private final Framebuffer input = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
   private final Framebuffer pass = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
   private final Framebuffer output = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
   private ShaderProgram outlineProgram = new ShaderProgram("vertex.vert", "outline.glsl");

   public ShaderESP() {
      super("ShaderESP", Module.Type.Visual, "ShaderESP", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("OutlineWidth", this, 1.0, 0.0, 20.0, false));
      Aqua.setmgr.register(new Setting("ShaderAlpha", this, 255.0, 0.0, 255.0, false));
      Aqua.setmgr.register(new Setting("Players", this, true));
      Aqua.setmgr.register(new Setting("Animals", this, true));
      Aqua.setmgr.register(new Setting("Chests", this, true));
      Aqua.setmgr.register(new Setting("Items", this, true));
      Aqua.setmgr.register(new Setting("Glow", this, false));
      Aqua.setmgr.register(new Setting("Color", this));
   }

   @Override
   public void onEnable() {
      this.outlineProgram = new ShaderProgram("vertex.vert", "outline.glsl");
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventRender3D) {
         mc.getRenderManager().setRenderOutlines(true);
         this.input.bindFramebuffer(false);
         mc.getRenderManager().setRenderOutlines(false);
         GlStateManager.pushMatrix();

         for(Entity entity : mc.theWorld.loadedEntityList) {
            if (Aqua.setmgr.getSetting("ShaderESPItems").isState() && entity instanceof EntityItem) {
               mc.getRenderManager().renderEntitySimple(entity, ((EventRender3D)e).getPartialTicks());
            }

            if (Aqua.setmgr.getSetting("ShaderESPPlayers").isState()
               && entity instanceof EntityPlayer
               && (entity != mc.thePlayer || mc.gameSettings.thirdPersonView == 1 || mc.gameSettings.thirdPersonView == 2)) {
               mc.getRenderManager().renderEntitySimple(entity, ((EventRender3D)e).getPartialTicks());
            }

            if (Aqua.setmgr.getSetting("ShaderESPAnimals").isState() && entity instanceof EntityAnimal) {
               mc.getRenderManager().renderEntitySimple(entity, ((EventRender3D)e).getPartialTicks());
            }
         }

         if (Aqua.setmgr.getSetting("ShaderESPChests").isState()) {
            List<TileEntity> loadedTileEntityList = mc.theWorld.loadedTileEntityList;
            int i = 0;

            for(int loadedTileEntityListSize = loadedTileEntityList.size(); i < loadedTileEntityListSize; ++i) {
               TileEntity tileEntity = loadedTileEntityList.get(i);
               if (tileEntity instanceof TileEntityChest) {
                  GlStateManager.disableTexture2D();
                  TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, mc.timer.renderPartialTicks, 1);
                  GlStateManager.enableTexture2D();
               }
            }
         }

         GlStateManager.popMatrix();
         mc.getRenderManager().setRenderOutlines(false);
         mc.getFramebuffer().bindFramebuffer(false);
      }

      if (e instanceof EventRender2D) {
      }

      if (e instanceof EventPostRender2D) {
         this.checkSetup();
         this.pass.framebufferClear();
         this.output.framebufferClear();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableTexture2D();
         ScaledResolution sr = new ScaledResolution(mc);
         double screenWidth = sr.getScaledWidth_double();
         double screenHeight = sr.getScaledHeight_double();
         GlStateManager.setActiveTexture(33984);
         this.outlineProgram.init();
         GL20.glUniform2f(this.outlineProgram.uniform("texelSize"), 1.0F / (float)mc.displayWidth, 1.0F / (float)mc.displayHeight);
         GL20.glUniform1i(this.outlineProgram.uniform("texture"), 0);
         float alpha = (float)Aqua.setmgr.getSetting("ShaderESPShaderAlpha").getCurrentNumber();
         float width = (float)Aqua.setmgr.getSetting("ShaderESPOutlineWidth").getCurrentNumber();
         GL20.glUniform1f(this.outlineProgram.uniform("outline_width"), width);
         GL20.glUniform1f(this.outlineProgram.uniform("alpha"), alpha / 255.0F);
         int[] rgb = getRGB(this.getColor2());
         int fadeColor = new Color(rgb[0], rgb[1], rgb[2]).getRGB();
         GL20.glUniform3f(
            this.outlineProgram.uniform("mixColor"),
            (float)new Color(fadeColor).getRed() / 255.0F,
            (float)new Color(fadeColor).getGreen() / 255.0F,
            (float)new Color(fadeColor).getBlue() / 255.0F
         );
         this.doOutlinePass(0, this.input.framebufferTexture, this.output, (int)screenWidth, (int)screenHeight);
         this.outlineProgram.uninit();
         if (Aqua.setmgr.getSetting("ShaderESPGlow").isState()) {
            ShaderMultiplier.drawGlowESP(() -> this.drawTexturedQuad1(this.output.framebufferTexture, screenWidth, screenHeight), false);
         }

         ShaderStencilUtil.initStencil();
         ShaderStencilUtil.bindWriteStencilBuffer();
         this.drawTexturedQuad(this.input.framebufferTexture, screenWidth, screenHeight);
         ShaderStencilUtil.bindReadStencilBuffer(0);
         this.drawTexturedQuad(this.output.framebufferTexture, screenWidth, screenHeight);
         ShaderStencilUtil.uninitStencilBuffer();
         GlStateManager.bindTexture(0);
         GlStateManager.alphaFunc(516, 0.2F);
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         this.input.framebufferClear();
         mc.getFramebuffer().bindFramebuffer(false);
      }
   }

   public static void drawGlowESP(Runnable runnable, boolean renderTwice) {
      EventGlowESP event = new EventGlowESP(runnable);
      Aqua.INSTANCE.onEvent(event);
      if (!event.isCancelled() || renderTwice) {
         runnable.run();
      }
   }

   public void doOutlinePass(int pass, int texture, Framebuffer out, int width, int height) {
      out.framebufferClear();
      out.bindFramebuffer(false);
      GL20.glUniform2f(this.outlineProgram.uniform("direction"), (float)(1 - pass), (float)pass);
      GL11.glBindTexture(3553, texture);
      this.outlineProgram.doRenderPass((float)width, (float)height);
   }

   public void checkSetup() {
      this.input.checkSetup(mc.displayWidth, mc.displayHeight);
      this.pass.checkSetup(mc.displayWidth, mc.displayHeight);
      this.output.checkSetup(mc.displayWidth, mc.displayHeight);
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

   private void drawTexturedQuad1(int texture, double width, double height) {
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

   public static int[] getRGB(int hex) {
      int a = hex >> 24 & 0xFF;
      int r = hex >> 16 & 0xFF;
      int g = hex >> 8 & 0xFF;
      int b = hex & 0xFF;
      return new int[]{r, g, b, a};
   }

   public int getColor2() {
      try {
         return Aqua.setmgr.getSetting("ShaderESPColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }
}
