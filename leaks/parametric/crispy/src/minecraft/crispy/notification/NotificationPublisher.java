package crispy.notification;


import crispy.Crispy;
import crispy.fonts.decentfont.FontUtil;
import crispy.fonts.decentfont.MinecraftFontRenderer;
import crispy.fonts.greatfont.TTFFontRenderer;
import crispy.util.animation.AnimationUtils;
import crispy.util.animation.Translate;
import crispy.util.render.RenderUtils;
import crispy.util.render.gui.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NotificationPublisher {
   private static final List<Notification> NOTIFICATIONS = new CopyOnWriteArrayList<>();

   public static void publish(ScaledResolution sr) {
      if (!NOTIFICATIONS.isEmpty()) {
         int srScaledHeight = ScaledResolution.getScaledHeight();
         int scaledWidth = ScaledResolution.getScaledWidth() - 3;
         int y = srScaledHeight - 50;
         Minecraft mc = Minecraft.getMinecraft();

         TTFFontRenderer fr = Crispy.INSTANCE.getFontManager().getFont("clean 16");
         TTFFontRenderer icon = Crispy.INSTANCE.getFontManager().getFont("noto 16");
         TTFFontRenderer bold = Crispy.INSTANCE.getFontManager().getFont("bold 16");

         for(Iterator<Notification> var7 = NOTIFICATIONS.iterator(); var7.hasNext(); y -= 30) {
            Notification notification = var7.next();

            Translate translate = notification.getTranslate();
            String iconChar = "";
            switch (notification.getType()) {
               case INFO: {
                  iconChar = "c";
                  break;
               }
               case WARNING: {
                  iconChar = "i";
                  break;
               }
               case SUCCESS: {
                  iconChar = "y";
                  break;
               }
               case ERROR: {
                  iconChar = "n";
                  break;
               }

            }
            int width = notification.getWidth();
            if (!notification.getTimer().elapsed(notification.getTime())) {
               notification.scissorBoxWidth = AnimationUtils.animate(width, notification.scissorBoxWidth, 0.1D);
               if(Math.abs(translate.getY() - y) >= 1) {
                  translate.interpolate(scaledWidth - width, y, 0.15D);
               }
            } else {
               notification.scissorBoxWidth = AnimationUtils.animate(0.0D, notification.scissorBoxWidth, 0.1D);
               if (notification.scissorBoxWidth < 1.0D) {
                  NOTIFICATIONS.remove(notification);
               }

               y += 30;
            }

            float translateX = (float)translate.getX();
            float translateY = (float)translate.getY();
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtils.prepareScissorBox((float)((double)scaledWidth - notification.scissorBoxWidth), translateY, (float)scaledWidth, translateY + 35);
            RenderUtil.drawRoundedRect(translateX, (double)translateY + 6, scaledWidth, translateY + 31,5, new Color(0, 0, 0, 100).getRGB());
            RenderUtil.glColor(notification.getType().getColor());

            RenderUtil.drawRoundedRect(translateX, translateY + 31.0F - 2.0F, (double)translateX + (double)width * ((double)((long)notification.getTime() - notification.getTimer().getElapsedTime()) / (double)notification.getTime()), translateY + 31,2, notification.getType().getColor());
            bold.drawStringWithShadow(notification.getTitle(), translateX + 4.0F, translateY + 8.0F, -1);
            fr.drawStringWithShadow(notification.getContent(), translateX + 4, translateY + 19.0F, -1);
            icon.drawStringWithShadow(iconChar, scaledWidth - 10, translateY + 10, notification.getType().getColor());
            GL11.glDisable(3089);
            GL11.glPopMatrix();
         }

      }
   }



   public static void queue(String title, String content, NotificationType type) {
      Minecraft mc = Minecraft.getMinecraft();

      TTFFontRenderer fr = Crispy.INSTANCE.getFontManager().getFont("clean 16");
      NOTIFICATIONS.add(new Notification(title, content, type, fr));
   }
   public static void queue(String title, String content, NotificationType type, int time) {
      Minecraft mc = Minecraft.getMinecraft();

      TTFFontRenderer fr = Crispy.INSTANCE.getFontManager().getFont("clean 16");
      NOTIFICATIONS.add(new Notification(title, content, type, fr, time));
   }
}
