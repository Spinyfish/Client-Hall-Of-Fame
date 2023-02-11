package Reality.Realii.guis.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Iterator;

public class NotificationsManager {
    public static ArrayList<Notification> notifications = new ArrayList<Notification>();

    public static ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public static void addNotification(Notification n) {
        notifications.add(n);
    }

    public static void renderNotifications() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float y = sr.getScaledHeight() - 30;
        Iterator<Notification> it = notifications.iterator();
        while (it.hasNext()) {
            it.next().render(y);
            y -= 25;
        }

    }

    public static void update() {
        ArrayList<Notification> temp = new ArrayList<>();

        for (Notification no : notifications){
            temp.add(no);
            no.update();
//            System.out.println(no.timer.delay(no.lastTime * 1000) + " " );
            if(no.timer != null) {
                if (no.timer.delay(no.lastTime * 1000)) {
                    no.setBack = true;
//                    no.timer.reset();
                    if(no.x <= 0.1f) {
                        temp.remove(no);
                    }
                }
            }


        }
        notifications.clear();
        for (Notification no : temp) {
            notifications.add(no);
        }
    }
}
