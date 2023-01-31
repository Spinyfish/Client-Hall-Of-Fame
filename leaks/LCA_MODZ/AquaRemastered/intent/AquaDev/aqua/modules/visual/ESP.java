package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventGlowESP;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender3D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.shader.Glow;
import intent.AquaDev.aqua.utils.shader.ShaderProgram;
import intent.AquaDev.aqua.utils.shader.ShaderStencilUtil;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

public class ESP extends Module {
   Minecraft MC = Minecraft.getMinecraft();
   private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
   private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
   private final Frustum frustum = new Frustum();
   private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
   private final ArrayList<Entity> collectedEntities = new ArrayList<>();
   private final Framebuffer input = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, true);
   private final Framebuffer pass = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, true);
   private final Framebuffer output = new Framebuffer(this.MC.displayWidth, this.MC.displayHeight, true);
   private ShaderProgram outlineProgram = new ShaderProgram("vertex.vert", "outline.glsl");
   public static double stringWidth;
   public static double glowStrength;

   public ESP() {
      super("ESP", Module.Type.Visual, "ESP", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Sigma", this, 5.0, 0.0, 50.0, true));
      Aqua.setmgr.register(new Setting("Multiplier", this, 1.0, 0.0, 3.0, false));
      Aqua.setmgr.register(new Setting("OutlineWidth", this, 1.0, 0.0, 20.0, false));
      Aqua.setmgr.register(new Setting("ShaderAlpha", this, 255.0, 0.0, 255.0, false));
      Aqua.setmgr.register(new Setting("VanillaTeamColor", this, false));
      Aqua.setmgr.register(new Setting("VanillaBlackLines", this, false));
      Aqua.setmgr.register(new Setting("Glow", this, true));
      Aqua.setmgr.register(new Setting("Fade", this, false));
      Aqua.setmgr.register(new Setting("Animals", this, true));
      Aqua.setmgr.register(new Setting("Mobs", this, false));
      Aqua.setmgr.register(new Setting("Players", this, true));
      Aqua.setmgr.register(new Setting("Items", this, false));
      Aqua.setmgr.register(new Setting("Rainbow", this, false));
      Aqua.setmgr.register(new Setting("AstolfoColors", this, false));
      Aqua.setmgr.register(new Setting("Chests", this, true));
      Aqua.setmgr.register(new Setting("EnderChests", this, false));
      Aqua.setmgr.register(new Setting("ClientColor", this, true));
      Aqua.setmgr.register(new Setting("Mode", this, "Shader", new String[]{"Shader", "Vanilla", "Hizzy", "Xave"}));
      Aqua.setmgr.register(new Setting("Color", this));
   }

   @Override
   public void onEnable() {
      this.outlineProgram = new ShaderProgram("vertex.vert", "outline.glsl");
      Glow.blurProgram = new ShaderProgram("vertex.vert", "alphaBlurESP.glsl");
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   public static void drawGlowESP(Runnable runnable, boolean renderTwice) {
      EventGlowESP event = new EventGlowESP(runnable);
      Aqua.INSTANCE.onEvent(event);
      if (!event.isCancelled() || renderTwice) {
         runnable.run();
      }
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventRender3D) {
         mc.getRenderManager().setRenderOutlines(true);
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         this.input.bindFramebuffer(false);
         this.MC.getRenderManager().setRenderOutlines(false);
         if (Aqua.setmgr.getSetting("ESPChests").isState()) {
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

         if (Aqua.setmgr.getSetting("ESPEnderChests").isState()) {
            List<TileEntity> loadedTileEntityList = mc.theWorld.loadedTileEntityList;
            int i = 0;

            for(int loadedTileEntityListSize = loadedTileEntityList.size(); i < loadedTileEntityListSize; ++i) {
               TileEntity tileEntity = loadedTileEntityList.get(i);
               if (tileEntity instanceof TileEntityEnderChest) {
                  GlStateManager.disableTexture2D();
                  TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, mc.timer.renderPartialTicks, 1);
                  GlStateManager.enableTexture2D();
               }
            }
         }

         if (Aqua.setmgr.getSetting("ESPMode").getCurrentMode().equalsIgnoreCase("Shader")
            && !Aqua.setmgr.getSetting("ESPMode").getCurrentMode().equalsIgnoreCase("Vanilla")
            && Aqua.setmgr.getSetting("ESPPlayers").isState()) {
            for(Entity entity : this.MC.theWorld.loadedEntityList) {
               if (entity instanceof EntityPlayer && (entity != mc.thePlayer || mc.gameSettings.thirdPersonView == 1 || mc.gameSettings.thirdPersonView == 2)) {
                  mc.getRenderManager().renderEntityStatic(entity, ((EventRender3D)event).getPartialTicks(), false);
               }
            }
         }

         if (Aqua.setmgr.getSetting("ESPItems").isState()) {
            for(Entity entity : this.MC.theWorld.loadedEntityList) {
               if (entity instanceof EntityItem) {
                  mc.getRenderManager().renderEntityStatic(entity, ((EventRender3D)event).getPartialTicks(), false);
               }
            }
         }

         if (Aqua.setmgr.getSetting("ESPAnimals").isState() && !Aqua.setmgr.getSetting("ESPMode").getCurrentMode().equalsIgnoreCase("Vanilla")) {
            for(Entity entity : this.MC.theWorld.loadedEntityList) {
               if (entity instanceof EntityAnimal) {
                  mc.getRenderManager().renderEntityStatic(entity, ((EventRender3D)event).getPartialTicks(), false);
               }
            }
         }

         if (Aqua.setmgr.getSetting("ESPMobs").isState() && !Aqua.setmgr.getSetting("ESPMode").getCurrentMode().equalsIgnoreCase("Vanilla")) {
            for(Entity entity : this.MC.theWorld.loadedEntityList) {
               if (entity instanceof EntityMob) {
                  mc.getRenderManager().renderEntityStatic(entity, ((EventRender3D)event).getPartialTicks(), false);
               }
            }
         }

         mc.getRenderManager().setRenderOutlines(false);
         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
         mc.getFramebuffer().bindFramebuffer(false);
      }

      if (event instanceof EventPostRender2D) {
         drawGlowESP(() -> Gui.drawRect(-2001, -2001, -2000, -2000, Color.red.getRGB()), false);
         Glow.checkSetup();
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
         Glow.blurProgram.init();
         Glow.setupBlurUniforms();
         Glow.doBlurPass(0, Glow.input.framebufferTexture, Glow.pass, (int)screenWidth, (int)screenHeight);
         Glow.doBlurPass(1, Glow.pass.framebufferTexture, Glow.output, (int)screenWidth, (int)screenHeight);
         Glow.blurProgram.uninit();
         ShaderStencilUtil.initStencil();
         ShaderStencilUtil.bindWriteStencilBuffer();
         this.drawTexturedQuad1(Glow.input.framebufferTexture, screenWidth, screenHeight);
         ShaderStencilUtil.bindReadStencilBuffer(0);
         this.drawTexturedQuad1(Glow.output.framebufferTexture, screenWidth, screenHeight);
         ShaderStencilUtil.uninitStencilBuffer();
         GlStateManager.bindTexture(0);
         GlStateManager.alphaFunc(516, 0.2F);
         GlStateManager.disableAlpha();
         GlStateManager.popMatrix();
         GlStateManager.disableBlend();
         Glow.input.framebufferClear();
         this.MC.getFramebuffer().bindFramebuffer(false);
      } else if (event instanceof EventGlowESP) {
         Glow.onGlowEvent((EventGlowESP)event);
      }

      if (event instanceof EventPostRender2D) {
         this.checkSetup();
         this.pass.framebufferClear();
         this.output.framebufferClear();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableTexture2D();
         ScaledResolution sr = new ScaledResolution(this.MC);
         double screenWidth = sr.getScaledWidth_double();
         double screenHeight = sr.getScaledHeight_double();
         GlStateManager.setActiveTexture(33984);
         this.outlineProgram.init();
         GL20.glUniform2f(this.outlineProgram.uniform("texelSize"), 1.0F / (float)this.MC.displayWidth, 1.0F / (float)this.MC.displayHeight);
         GL20.glUniform1i(this.outlineProgram.uniform("texture"), 0);
         float alpha = (float)Aqua.setmgr.getSetting("ESPShaderAlpha").getCurrentNumber();
         float width = (float)Aqua.setmgr.getSetting("ESPOutlineWidth").getCurrentNumber();
         GL20.glUniform1f(this.outlineProgram.uniform("outline_width"), width);
         GL20.glUniform1f(this.outlineProgram.uniform("alpha"), alpha / 255.0F);
         int[] rgb = Aqua.setmgr.getSetting("ESPAstolfoColors").isState()
            ? getRGB(SkyRainbow(20, 1.0F, 0.5F).getRGB())
            : (
               Aqua.setmgr.getSetting("ESPRainbow").isState()
                  ? getRGB(rainbowESP(0))
                  : (Aqua.setmgr.getSetting("ESPClientColor").isState() ? getRGB(this.getColor()) : getRGB(this.getColor2()))
            );
         int fadeColor = !Aqua.setmgr.getSetting("ESPFade").isState()
            ? new Color(rgb[0], rgb[1], rgb[2]).getRGB()
            : ColorUtils.getColorAlpha(
                  Arraylist.getGradientOffset(
                     new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()), 15.0
                  ),
                  255
               )
               .getRGB();
         GL20.glUniform3f(
            this.outlineProgram.uniform("mixColor"),
            (float)new Color(fadeColor).getRed() / 255.0F,
            (float)new Color(fadeColor).getGreen() / 255.0F,
            (float)new Color(fadeColor).getBlue() / 255.0F
         );
         this.doOutlinePass(0, this.input.framebufferTexture, this.output, (int)screenWidth, (int)screenHeight);
         this.outlineProgram.uninit();
         if (Aqua.setmgr.getSetting("ESPGlow").isState()) {
            drawGlowESP(() -> this.drawTexturedQuad1(this.output.framebufferTexture, screenWidth, screenHeight), false);
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

   public void doOutlinePass(int pass, int texture, Framebuffer out, int width, int height) {
      out.framebufferClear();
      out.bindFramebuffer(false);
      GL20.glUniform2f(this.outlineProgram.uniform("direction"), (float)(1 - pass), (float)pass);
      GL11.glBindTexture(3553, texture);
      this.outlineProgram.doRenderPass((float)width, (float)height);
   }

   public void checkSetup() {
      this.input.checkSetup(this.MC.displayWidth, this.MC.displayHeight);
      this.pass.checkSetup(this.MC.displayWidth, this.MC.displayHeight);
      this.output.checkSetup(this.MC.displayWidth, this.MC.displayHeight);
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

   public int getColor2() {
      try {
         return Aqua.setmgr.getSetting("ESPColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }

   public int getColor() {
      try {
         return Aqua.setmgr.getSetting("HUDColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }

   public static int[] getRGB(int hex) {
      int a = hex >> 24 & 0xFF;
      int r = hex >> 16 & 0xFF;
      int g = hex >> 8 & 0xFF;
      int b = hex & 0xFF;
      return new int[]{r, g, b, a};
   }

   public static int rainbowESP(int delay) {
      float rainbowSpeed = 25.0F;
      double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay)) / 25.0;
      rainbowState %= 360.0;
      return Color.getHSBColor((float)(rainbowState / 360.0), 0.9F, 1.0F).getRGB();
   }

   public static Color SkyRainbow(int counter, float bright, float st) {
      double v1 = Math.ceil((double)(System.currentTimeMillis() + (long)counter * 109L)) / 6.0;
      double var5;
      return Color.getHSBColor((double)((float)((var5 = v1 % 360.0) / 360.0)) < 0.5 ? -((float)(var5 / 360.0)) : (float)(var5 / 360.0), st, bright);
   }

   public void drawHizzyESP() {
      for(Object o : mc.theWorld.loadedEntityList) {
         Entity e = (Entity)o;
         if (e instanceof EntityPlayer && e != mc.thePlayer) {
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosY;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated(x, y - 0.2, z);
            GL11.glScalef(0.03F, 0.03F, 0.03F);
            GL11.glRotated((double)(-mc.getRenderManager().playerViewY), 0.0, 1.0, 0.0);
            GlStateManager.disableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(new ResourceLocation("Aqua/gui/max.png"));
            Gui.drawModalRectWithCustomSizedTexture(50, 90, 0.0F, 0.0F, -100, -100, -100.0F, -100.0F);
            GlStateManager.enableDepth();
            GL11.glPopMatrix();
         }
      }
   }

   public void draw2D() {
      GL11.glPushMatrix();
      boolean outline = true;
      this.collectEntities();
      float partialTicks = mc.timer.renderPartialTicks;
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int scaleFactor = scaledResolution.getScaleFactor();
      double scaling = (double)scaleFactor / Math.pow((double)scaleFactor, 2.0);
      GL11.glScaled(scaling, scaling, scaling);

      for(EntityPlayer player : mc.theWorld.playerEntities) {
         if ((player != mc.thePlayer || mc.gameSettings.thirdPersonView != 0) && !player.isInvisible() && this.isInViewFrustrum(player)) {
            double x1 = this.interpolate(player.posX, player.lastTickPosX, (double)partialTicks);
            double y1 = this.interpolate(player.posY, player.lastTickPosY, (double)partialTicks);
            double z1 = this.interpolate(player.posZ, player.lastTickPosZ, (double)partialTicks);
            double width = (double)player.width / 1.5;
            double height = (double)player.height + 0.2;
            AxisAlignedBB aabb = new AxisAlignedBB(x1 - width, y1, z1 - width, x1 + width, y1 + height, z1 + width);
            List vectors = Arrays.asList(
               new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
               new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
               new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
               new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
               new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
               new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
               new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
               new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
            );
            mc.entityRenderer.setupCameraTransform(partialTicks, 0);
            Vector4d position = null;
            int i1 = 0;

            for(int vectorsSize = vectors.size(); i1 < vectorsSize; ++i1) {
               Vector3d vector = (Vector3d)vectors.get(i1);
               vector = this.project2D(
                  scaleFactor,
                  vector.x - mc.getRenderManager().viewerPosX,
                  vector.y - mc.getRenderManager().viewerPosY,
                  vector.z - mc.getRenderManager().viewerPosZ
               );
               if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                  if (position == null) {
                     position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                  }

                  position.x = Math.min(vector.x, position.x);
                  position.y = Math.min(vector.y, position.y);
                  position.z = Math.max(vector.x, position.z);
                  position.w = Math.max(vector.y, position.w);
               }
            }

            if (position != null) {
               mc.entityRenderer.setupOverlayRendering();
               double posX = position.x;
               double posY = position.y;
               double endPosX = position.z;
               double endPosY = position.w;
               int color = Aqua.setmgr.getSetting("ESPClientColor").isState() ? this.getColor() : this.getColor2();
               int color2 = Color.red.getRGB();
               int black = Color.black.getRGB();
               if (outline) {
                  float health = player.getHealth();
                  float maxHealth = player.getMaxHealth();
                  Gui.drawRect2(posX - 1.2, endPosY - (double)(health / maxHealth) * (endPosY - posY), posX - 2.0, posY, Color.black.getRGB());
                  Gui.drawRect2(posX - 1.2, endPosY - (double)(health / maxHealth) * (endPosY - posY), posX - 2.0, endPosY, Color.green.getRGB());
                  Gui.drawRect2(posX - 1.0, posY, posX + 0.5, endPosY + 0.5, black);
                  Gui.drawRect2(posX - 1.0, posY - 0.5, endPosX + 0.5, posY + 0.5 + 0.5, black);
                  Gui.drawRect2(endPosX - 0.5 - 0.5, posY, endPosX + 0.5, endPosY + 0.5, black);
                  Gui.drawRect2(posX - 1.0, endPosY - 0.5 - 0.5, endPosX + 0.5, endPosY + 0.5, black);
                  Gui.drawRect2(posX - 0.5, posY, posX + 0.5 - 0.5, endPosY, color);
                  Gui.drawRect2(posX, endPosY - 0.5, endPosX, endPosY, color);
                  Gui.drawRect2(posX - 0.5, posY, endPosX, posY + 0.5, color);
                  Gui.drawRect2(endPosX - 0.5, posY, endPosX, endPosY, color);
               }
            }
         }
      }

      GL11.glPopMatrix();
   }

   public boolean isInViewFrustrum(Entity entity) {
      return this.isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
   }

   private boolean isInViewFrustrum(AxisAlignedBB bb) {
      Entity current = mc.getRenderViewEntity();
      this.frustum.setPosition(current.posX, current.posY, current.posZ);
      return this.frustum.isBoundingBoxInFrustum(bb);
   }

   private double interpolate(double current, double old, double scale) {
      return old + (current - old) * scale;
   }

   private Vector3d project2D(int scaleFactor, double x, double y, double z) {
      GL11.glGetFloat(2982, modelview);
      GL11.glGetFloat(2983, projection);
      GL11.glGetInteger(2978, viewport);
      return GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, this.vector)
         ? new Vector3d(
            (double)(this.vector.get(0) / (float)scaleFactor),
            (double)(((float)Display.getHeight() - this.vector.get(1)) / (float)scaleFactor),
            (double)this.vector.get(2)
         )
         : null;
   }

   private void collectEntities() {
      this.collectedEntities.clear();

      for(Entity entity : mc.theWorld.loadedEntityList) {
         if (entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP) && !entity.isDead) {
            this.collectedEntities.add(entity);
         }
      }
   }

   public void drawXaveESP() {
      for(Object o : mc.theWorld.loadedEntityList) {
         Entity e = (Entity)o;
         if (e instanceof EntityPlayer && e != mc.thePlayer) {
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosY;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosZ;
            GlStateManager.pushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);
            GlStateManager.translate(x, y + (double)(e.height / 2.0F), z);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(-0.098, -0.098, 0.098);
            Color color = new Color(this.getColor2());
            if (((EntityPlayer)e).hurtTime != 0) {
               GlStateManager.color(1.0F, 0.0F, 0.0F);
            } else {
               GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F);
            }

            float width = (float)(23.3 * (double)e.width / 2.0);
            float height = 12.0F;
            draw3DRect(width, height - 1.0F, width - 4.0F, height);
            draw3DRect(-width, height - 1.0F, -width + 4.0F, height);
            draw3DRect(-width, height, -width + 1.0F, height - 4.0F);
            draw3DRect(width, height, width - 1.0F, height - 4.0F);
            draw3DRect(width, -height, width - 4.0F, -height + 1.0F);
            draw3DRect(-width, -height, -width + 4.0F, -height + 1.0F);
            draw3DRect(-width, -height + 1.0F, -width + 1.0F, -height + 4.0F);
            draw3DRect(width, -height + 1.0F, width - 1.0F, -height + 4.0F);
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            draw3DRect(width, height, width - 4.0F, height + 0.2F);
            draw3DRect(-width, height, -width + 4.0F, height + 0.2F);
            draw3DRect(-width - 0.2F, height + 0.2F, -width, height - 4.0F);
            draw3DRect(width + 0.2F, height + 0.2F, width, height - 4.0F);
            draw3DRect(width + 0.2F, -height, width - 4.0F, -height - 0.2F);
            draw3DRect(-width - 0.2F, -height, -width + 4.0F, -height - 0.2F);
            draw3DRect(-width - 0.2F, -height, -width, -height + 4.0F);
            draw3DRect(width + 0.2F, -height, width, -height + 4.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(true);
            GlStateManager.enableCull();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
            GlStateManager.popMatrix();
         }
      }
   }

   public static void draw3DRect(float x1, float y1, float x2, float y2) {
      GL11.glBegin(7);
      GL11.glVertex2d((double)x2, (double)y1);
      GL11.glVertex2d((double)x1, (double)y1);
      GL11.glVertex2d((double)x1, (double)y2);
      GL11.glVertex2d((double)x2, (double)y2);
      GL11.glEnd();
   }
}
