package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventRender3D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ChinaHat extends Module {
   public static long lastFrame = 0L;
   private int ticks;

   public ChinaHat() {
      super("ChinaHat", Module.Type.Visual, "ChinaHat", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Blur", this, false));
      Aqua.setmgr.register(new Setting("Glow", this, false));
      Aqua.setmgr.register(new Setting("Colored", this, false));
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
         if (mc.gameSettings.thirdPersonView == 0) {
            return;
         }

         if (Aqua.setmgr.getSetting("ChinaHatGlow").isState()) {
            ShaderMultiplier.drawGlowESP(() -> this.render(), false);
         }

         if (Aqua.setmgr.getSetting("ChinaHatBlur").isState()) {
            Blur.drawBlurred(() -> this.render(), false);
         }

         if (Aqua.setmgr.getSetting("ChinaHatColored").isState()) {
            this.render();
         }
      }
   }

   public void render() {
      this.ticks = (int)((double)this.ticks + 0.004 * (double)(System.currentTimeMillis() - lastFrame));
      lastFrame = System.currentTimeMillis();
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glEnable(2832);
      GL11.glEnable(3042);
      GL11.glShadeModel(7425);
      GlStateManager.disableCull();
      GL11.glBegin(5);
      double x = mc.thePlayer.lastTickPosX
         + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * (double)mc.timer.renderPartialTicks
         - mc.getRenderManager().viewerPosX;
      double y = mc.thePlayer.lastTickPosY
         + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * (double)mc.timer.renderPartialTicks
         - mc.getRenderManager().viewerPosY
         + (double)mc.thePlayer.getEyeHeight()
         + 0.5;
      double z = mc.thePlayer.lastTickPosZ
         + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * (double)mc.timer.renderPartialTicks
         - mc.getRenderManager().viewerPosZ;
      double rad = 0.65F;
      int q = 64;
      boolean increaseCount = false;
      int var18 = 1024;
      increaseCount = true;

      for(float i = 0.0F; (double)i < (Math.PI * 2) + (increaseCount ? 0.01 : 0.0); i = (float)((double)i + (Math.PI * 4) / (double)var18)) {
         double vecX = x + 0.65F * Math.cos((double)i);
         double vecZ = z + 0.65F * Math.sin((double)i);
         Color color = new Color(Aqua.setmgr.getSetting("ChinaHatColor").getColor());
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.8F);
         GL11.glVertex3d(vecX, y - 0.25, vecZ);
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.8F);
         GL11.glVertex3d(x, y, z);
      }

      GL11.glEnd();
      GL11.glShadeModel(7424);
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GlStateManager.enableCull();
      GL11.glDisable(2848);
      GL11.glEnable(2832);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
      GL11.glColor3f(255.0F, 255.0F, 255.0F);
   }
}
