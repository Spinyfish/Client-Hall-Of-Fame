package rip.athena.client.gui.notifications;

import net.minecraft.util.*;
import rip.athena.client.*;
import rip.athena.client.events.types.render.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import java.util.*;
import rip.athena.client.events.*;
import java.awt.*;

public class NotificationManager
{
    public static final ResourceLocation LOGO;
    private List<Notification> notifications;
    private Thread removalThread;
    
    public NotificationManager() {
        this.notifications = new ArrayList<Notification>();
        (this.removalThread = new Thread(new NotiRemovalThread(this.notifications))).start();
        Athena.INSTANCE.getEventBus().register(this);
    }
    
    @SubscribeEvent
    public void drawOverlay(final RenderEvent event) {
        if (event.getRenderType() != RenderType.INGAME_OVERLAY) {
            return;
        }
        int y = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        for (final Notification noti : this.notifications) {
            y = noti.draw(y);
        }
    }
    
    public void showNotification(final String text, final Color color) {
        boolean contains = false;
        for (final Notification noti : this.notifications) {
            if (noti.getText().equalsIgnoreCase(text) && !noti.isDead()) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            this.notifications.add(new Notification(text, color));
        }
    }
    
    static {
        LOGO = new ResourceLocation("Athena/logo/Athena.png");
    }
}
