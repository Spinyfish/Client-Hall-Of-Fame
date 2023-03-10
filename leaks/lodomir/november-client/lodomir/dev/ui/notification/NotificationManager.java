/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.notification;

import java.util.concurrent.LinkedBlockingQueue;
import lodomir.dev.ui.notification.Notification;

public class NotificationManager {
    private static LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue();
    private static Notification currentNotification = null;

    public static void show(Notification notification) {
        pendingNotifications.add(notification);
    }

    public static void update() {
        if (currentNotification != null && !currentNotification.isShown()) {
            currentNotification = null;
        }
        if (currentNotification == null && !pendingNotifications.isEmpty()) {
            currentNotification = pendingNotifications.poll();
            currentNotification.show();
        }
    }

    public static void render() {
        NotificationManager.update();
        if (currentNotification != null) {
            currentNotification.render();
        }
    }
}

