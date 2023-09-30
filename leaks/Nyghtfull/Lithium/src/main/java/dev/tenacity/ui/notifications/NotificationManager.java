package dev.tenacity.ui.notifications;

import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.render.NotificationsMod;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    @Getter
    @Setter
    private static float toggleTime = 2;

    @Getter
    private static final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public static void post(NotificationType type, String title, String description) {
        post(new Notification(type, title, description));
    }

    public static void post(NotificationType type, String title, String description, float time) {
        post(new Notification(type, title, description, time));
    }

    public static void post(Notification notification) {
        if (Tenacity.INSTANCE.isEnabled(NotificationsMod.class)) {
            notifications.add(notification);
        }
    }

}
