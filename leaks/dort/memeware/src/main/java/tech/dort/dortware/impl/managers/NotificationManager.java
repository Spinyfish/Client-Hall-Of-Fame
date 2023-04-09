package tech.dort.dortware.impl.managers;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.manager.Manager;
import tech.dort.dortware.api.notification.AbstractNotification;
import tech.dort.dortware.impl.events.RenderHUDEvent;
import tech.dort.dortware.impl.notification.Error;
import tech.dort.dortware.impl.notification.Info;
import tech.dort.dortware.impl.notification.Warning;
import tech.dort.dortware.impl.notification.type.NotificationType;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager extends Manager<AbstractNotification> {

    public NotificationManager() {
        super(new CopyOnWriteArrayList<>());
    }

    @Override
    public void onCreated() {
        Client.INSTANCE.getEventBus().register(this);
    }

    /**
     * Renders all current notifications.
     *
     * @param event - Ignore this, Called by the {@code EventBus}
     */
    @Subscribe
    public void renderAll(RenderHUDEvent event) {
        this.getObjects().forEach(AbstractNotification::update);
    }

    /**
     * Queues a notification for rendering
     *
     * @param message - The message to display
     * @param type    - The notification's {@code NotificationType}
     */
    public void postNotification(String message, NotificationType type) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        int width = resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(message) - 15;
        int height = resolution.getScaledHeight() + getObjects().size() * -21;
        long endTime = System.currentTimeMillis() + 1500L;
        AbstractNotification notification = null;
        switch (type) {
            case ERROR:
                notification = new Error(message, width, height, endTime);
                break;
            case INFO:
                notification = new Info(message, width, height, endTime);
                break;
            case WARNING:
                notification = new Warning(message, width, height, endTime);
                break;
        }
        this.add(notification);
    }

}
