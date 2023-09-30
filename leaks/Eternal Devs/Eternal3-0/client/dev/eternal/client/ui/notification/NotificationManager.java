package dev.eternal.client.ui.notification;

import dev.eternal.client.util.animate.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationManager {

  private static final Minecraft mc = Minecraft.getMinecraft();
  private static final List<Notification> notificationList = new CopyOnWriteArrayList<>();

  public static void pushNotification(Notification notification) {
    notification.position().setY(30 * (notificationList.size() + 1) - 20);
    notificationList.add(notification);
  }

  public static void renderNotifications() {
    final ScaledResolution sr = new ScaledResolution(mc);
    final float xPos = sr.getScaledWidth() - 10;
    final float yPos = sr.getScaledHeight() - 10;

    AtomicInteger yOffset = new AtomicInteger(0);

    notificationList.removeIf(notification -> notification.position().getX() < -1);
    notificationList.forEach(notification -> {
      final int yOff = yOffset.getAndIncrement();
      final float width = notification.width();
      final float height = notification.height();
      final Position position = notification.position();

//      if((System.currentTimeMillis() - notification.getInitTime()) > notification.getTime())
//        pos.interpolate(xPos+13, (float) pos.getY());
//      else
//        pos.interpolate(xPos - width, yPos - yOff*50 - height);

      position.interpolate(
          (System.currentTimeMillis() - notification.initTime() > notification.time())
              ? -5
              : width + 48,
          30 * (yOff + 1) - 20);

      notification.render();
    });
  }

}
