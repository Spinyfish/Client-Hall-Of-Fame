package me.jinthium.scripting.api.bindings;

import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.impl.Client;

public class NotificationBinding {

    public void post(NotificationType notificationType, String title, String content) {
        Client.INSTANCE.getNotificationManager().post(title, content, notificationType);
    }

    public void post(NotificationType notificationType, String title, String content, long time) {
        Client.INSTANCE.getNotificationManager().post(title, content, notificationType, time);
    }

}
