package rip.athena.client.gui.notifications;

import java.util.*;

public class NotiRemovalThread implements Runnable
{
    private List<Notification> notifications;
    
    public NotiRemovalThread(final List<Notification> notifications) {
        this.notifications = notifications;
    }
    
    @Override
    public void run() {
        while (true) {
            this.notifications.removeIf(noti -> noti.isDead());
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
