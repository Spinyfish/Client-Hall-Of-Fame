package igbt.astolfy.ui.Notifications;

import java.util.ArrayList;

import igbt.astolfy.module.visuals.Hud;

public class NotificationManager {

    private static ArrayList<Notification> currentNotifications = new ArrayList<>();
    private static ArrayList<Notification> removeNotifications = new ArrayList<>();

    public static void showNotification(Notification notification){
        currentNotifications.add(notification);
        notification.showNotification();
    }

    public static void removeNotification(Notification notification){
        removeNotifications.add(notification);
    }

    public static void update() {
        if(removeNotifications.size() > 0)
            currentNotifications.removeAll(removeNotifications);
        int current = 0;
        for(Notification notification : currentNotifications){
            if(Hud.notificationType.getCurrentValue().equalsIgnoreCase("Left") && current >= 6){
                notification.showNotification();
            }else
            notification.renderNotification(current);
            current++;
        }
    }

}
