/*
 * Copyright Felix Hans from NotificationManager coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.notifications;

import java.util.ArrayList;

public class NotificationManager {

    public static ArrayList<Notification> notifications = new ArrayList<>();

    private static final ArrayList<Notification> toDelete = new ArrayList<>();

    public static void sendNotification(final String title, final String description, long... stayTime) {
        notifications.add(new Notification(title, description, stayTime));
    }

    public static void renderNotifications(final int x, final int y) {
        for (int i = 0; i < notifications.size(); ++i) {
            Notification notification = notifications.get(i);
            if (notification.hasTimeReached())
                notifications.remove(i);

          //  notification.doRenderNotification(x, y);

        }
    }

}
