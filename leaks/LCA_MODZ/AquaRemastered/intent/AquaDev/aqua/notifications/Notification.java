package intent.AquaDev.aqua.notifications;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Notification extends Gui {
   private boolean showing = false;
   private boolean started = false;
   private String name;
   private String description;
   public int height = 30;
   private long wait = 2L;
   private boolean doneWaiting = false;
   private Notification.NotificationType type;
   private long animationStart;
   private int notificationWidth = 150;
   private TimeUtil timer = new TimeUtil();

   public Notification(String name, String description, long wait, Notification.NotificationType type) {
      this.name = name;
      this.description = description;
      this.type = type;
      int nameWidth = Aqua.INSTANCE.novoline.getStringWidth(name);
      int descWidth = Aqua.INSTANCE.novoline.getStringWidth(description);
      if (nameWidth > 170 || descWidth > 170) {
         if (nameWidth > descWidth) {
            this.notificationWidth = nameWidth + 20;
         } else {
            this.notificationWidth = descWidth + 20;
         }
      }

      this.animationStart = System.currentTimeMillis();
      this.started = this.showing = true;
   }

   public void draw2(int y) {
      if (this.isShowing()) {
         double animationTime = 400.0;
         double waitTime = 1000.0;
         this.doneWaiting = (double)(System.currentTimeMillis() - this.animationStart) > animationTime + waitTime;
         double animationProgress = this.doneWaiting
            ? 1.0 - ((double)System.currentTimeMillis() - ((double)this.animationStart + animationTime + waitTime)) / animationTime
            : Math.min(1.0, (double)(System.currentTimeMillis() - this.animationStart) / animationTime);
         if (this.doneWaiting && animationProgress <= 0.1) {
            this.setShowing(false);
         }

         ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
         float notiX = (float)sr.getScaledWidth() / 2.0F + 75.0F - (float)this.notificationWidth;
         int bgColor = Integer.MIN_VALUE;
         switch(this.type) {
            case INFO:
               bgColor = new Color(20, 20, 40, 150).getRGB();
               break;
            case WARNING:
               bgColor = new Color(145, 75, 20, 150).getRGB();
               break;
            case ERROR:
               bgColor = new Color(75, 20, 20, 150).getRGB();
               break;
            default:
               bgColor = new Color(20, 20, 20, 150).getRGB();
         }

         RenderUtil.drawRoundedRect2Alpha(
            (double)(notiX + 20.0F),
            (double)y * animationProgress,
            (double)(this.notificationWidth - 42),
            (double)this.height * animationProgress,
            3.0,
            new Color(0, 0, 0, 70)
         );
         GlStateManager.resetColor();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         GL11.glEnable(3089);
         RenderUtil.scissor(
            (double)(notiX + 20.0F), (double)y * animationProgress, (double)(this.notificationWidth - 42), (double)this.height * animationProgress
         );
         Aqua.INSTANCE.novoline.drawString(this.name, notiX + 58.0F, (float)((double)y * animationProgress + 3.0), Color.white.getRGB());
         Aqua.INSTANCE.novoline.drawCenteredString(this.description, notiX + 73.0F, (float)((double)y * animationProgress + 16.0), Color.white.getRGB());
         GL11.glDisable(3089);
         GlStateManager.popMatrix();
         GlStateManager.resetColor();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (this.timer.hasReached(this.wait)) {
            this.doneWaiting = true;
         }
      }
   }

   public void draw(int y) {
      if (this.isShowing()) {
         double animationTime = 400.0;
         double waitTime = 1000.0;
         this.doneWaiting = (double)(System.currentTimeMillis() - this.animationStart) > animationTime + waitTime;
         double animationProgress = this.doneWaiting
            ? 1.0 - ((double)System.currentTimeMillis() - ((double)this.animationStart + animationTime + waitTime)) / animationTime
            : Math.min(1.0, (double)(System.currentTimeMillis() - this.animationStart) / animationTime);
         if (this.doneWaiting && animationProgress <= 0.1) {
            this.setShowing(false);
         }

         ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
         float notiX = (float)sr.getScaledWidth() / 2.0F + 75.0F - (float)this.notificationWidth;
         int bgColor = Integer.MIN_VALUE;
         switch(this.type) {
            case INFO:
               bgColor = new Color(20, 20, 40, 150).getRGB();
               break;
            case WARNING:
               bgColor = new Color(145, 75, 20, 150).getRGB();
               break;
            case ERROR:
               bgColor = new Color(75, 20, 20, 150).getRGB();
               break;
            default:
               bgColor = new Color(20, 20, 20, 150).getRGB();
         }

         if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect(
                     (double)(notiX + 20.0F),
                     (double)y * animationProgress,
                     (double)(this.notificationWidth - 42),
                     (double)this.height * animationProgress,
                     3.0,
                     new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB()
                  ),
               false
            );
         }

         if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
            Blur.drawBlurred(
               () -> RenderUtil.drawRoundedRect(
                     (double)(notiX + 20.0F),
                     (double)y * animationProgress,
                     (double)(this.notificationWidth - 42),
                     (double)this.height * animationProgress,
                     3.0,
                     new Color(0, 0, 0, 170).getRGB()
                  ),
               false
            );
         }

         if (this.timer.hasReached(this.wait)) {
            this.doneWaiting = true;
         }
      }
   }

   public boolean isShowing() {
      return this.showing;
   }

   public void setShowing(boolean showing) {
      this.showing = showing;
   }

   public boolean isStarted() {
      return this.started;
   }

   public void setStarted(boolean started) {
      this.started = started;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Notification.NotificationType getType() {
      return this.type;
   }

   public static enum NotificationType {
      INFO,
      ERROR,
      WARNING,
      NONE;
   }
}
