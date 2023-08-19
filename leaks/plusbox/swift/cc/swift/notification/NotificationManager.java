package cc.swift.notification;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public class NotificationManager {
    private final ArrayList<Notification> activeNotifications = new ArrayList<>();

    public void addNotification(Notification notification) {
        activeNotifications.add(notification);
    }

    public void render(ScaledResolution sr) {
        for (int i = 0; i < activeNotifications.size(); i++) {
            Notification notification = activeNotifications.get(i);
            notification.render(sr.getScaledWidth() - 10, sr.getScaledHeight() - 10 - (i * 50));
        }
        activeNotifications.removeIf(notification -> !notification.isShown());
    }
}