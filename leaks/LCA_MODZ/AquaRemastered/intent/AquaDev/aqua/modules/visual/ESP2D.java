package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import events.listeners.EventRender3D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ESP2D extends Module {
   private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
   private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
   private final Frustum frustum = new Frustum();
   private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
   private final ArrayList<Entity> collectedEntities = new ArrayList<>();

   public ESP2D() {
      super("ESP2D", Module.Type.Visual, "ESP2D", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Glow", this, true));
      Aqua.setmgr.register(new Setting("Fade", this, false));
      Aqua.setmgr.register(new Setting("GifBackground", this, false));
      Aqua.setmgr.register(new Setting("GifMode", this, "Aqua", new String[]{"Aqua", "Rias", "Anime", "Rias2"}));
      Aqua.setmgr.register(new Setting("Mode", this, "Real2D", new String[]{"Real2D", "RiasGif"}));
      Aqua.setmgr.register(new Setting("Color", this));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventRender3D) {
         String var2 = Aqua.setmgr.getSetting("ESP2DMode").getCurrentMode();
         byte var3 = -1;
         switch(var2.hashCode()) {
            case -1434969541:
               if (var2.equals("RiasGif")) {
                  var3 = 0;
               }
            default:
               switch(var3) {
                  case 0:
                     this.drawGifESP();
               }
         }
      }

      if (e instanceof EventPostRender2D && Aqua.setmgr.getSetting("ESP2DGlow").isState()) {
         String var4 = Aqua.setmgr.getSetting("ESP2DMode").getCurrentMode();
         byte var6 = -1;
         switch(var4.hashCode()) {
            case -1851106160:
               if (var4.equals("Real2D")) {
                  var6 = 0;
               }
            default:
               switch(var6) {
                  case 0:
                     Shadow.drawGlow(() -> this.draw2D(), false);
               }
         }
      }

      if (e instanceof EventRender2D) {
         String var5 = Aqua.setmgr.getSetting("ESP2DMode").getCurrentMode();
         byte var7 = -1;
         switch(var5.hashCode()) {
            case -1851106160:
               if (var5.equals("Real2D")) {
                  var7 = 0;
               }
            default:
               switch(var7) {
                  case 0:
                     this.draw2D();
               }
         }
      }
   }

   public void drawGifESP() {
      for(EntityPlayer player : mc.theWorld.playerEntities) {
         if ((player != mc.thePlayer || mc.gameSettings.thirdPersonView != 0) && !player.isInvisible()) {
            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosX;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosY;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)mc.timer.renderPartialTicks - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glTranslated(x, y - 0.2, z);
            GL11.glScalef(0.03F, 0.03F, 0.03F);
            GL11.glRotated((double)(-mc.getRenderManager().playerViewY), 0.0, 1.0, 0.0);
            GlStateManager.disableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtil.drawGif(50, 90, -100, -100, "rias");
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
               int color = this.getColor2();
               int color2 = Color.red.getRGB();
               int black = Color.black.getRGB();
               float health = player.getHealth();
               float maxHealth = player.getMaxHealth();
               if (Aqua.setmgr.getSetting("ESP2DGifBackground").isState()) {
                  String var35 = Aqua.setmgr.getSetting("ESP2DGifMode").getCurrentMode();
                  switch(var35) {
                     case "Aqua":
                        RenderUtil.drawGif(
                           (int)((float)posX - 2.0F), (int)posY, (int)((double)((float)endPosX) - posX + 2.0), (int)((double)((float)endPosY) - posY), "aqua"
                        );
                        break;
                     case "Rias":
                        RenderUtil.drawGif(
                           (int)((float)posX - 2.0F), (int)posY, (int)((double)((float)endPosX) - posX + 2.0), (int)((double)((float)endPosY) - posY), "rias"
                        );
                        break;
                     case "Anime":
                        RenderUtil.drawGif(
                           (int)((float)posX - 2.0F), (int)posY, (int)((double)((float)endPosX) - posX + 2.0), (int)((double)((float)endPosY) - posY), "anime"
                        );
                        break;
                     case "Rias2":
                        RenderUtil.drawGif(
                           (int)((float)posX - 2.0F), (int)posY, (int)((double)((float)endPosX) - posX + 2.0), (int)((double)((float)endPosY) - posY), "rias2"
                        );
                  }
               }

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

   public int getColor2() {
      try {
         return Aqua.setmgr.getSetting("ESP2DColor").getColor();
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
}
