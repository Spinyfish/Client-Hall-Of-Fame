package intent.AquaDev.aqua.notifications;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class NotificationManager {
   private static List<Notification> notis = new ArrayList<>();

   public static void addNotificationToQueue(Notification noti) {
      notis.add(noti);
   }

   public static void render() {
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      List<Notification> toRemove = new ArrayList<>();
      int notiY = sr.getScaledHeight() / 4 - 150;

      try {
         for(Notification noti : notis) {
            notiY += 35;
            noti.draw(notiY);
            if (!noti.isShowing()) {
               toRemove.add(noti);
            }
         }

         toRemove.forEach(notification -> notis.remove(notification));
      } catch (Exception var5) {
      }
   }

   public static void render2() {
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      List<Notification> toRemove = new ArrayList<>();
      int notiY = sr.getScaledHeight() / 4 - 150;

      try {
         for(Notification noti : notis) {
            notiY += 35;
            noti.draw2(notiY);
            if (!noti.isShowing()) {
               toRemove.add(noti);
            }
         }
      } catch (Exception var5) {
      }

      toRemove.forEach(notification -> notis.remove(notification));
   }
}
