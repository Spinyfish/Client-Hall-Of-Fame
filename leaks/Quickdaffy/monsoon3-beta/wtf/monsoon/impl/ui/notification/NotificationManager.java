/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.ui.notification;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.module.hud.NotificationsModule;
import wtf.monsoon.impl.ui.notification.Notification;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class NotificationManager {
    private final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList();

    public void notify(NotificationType type, String title, String description) {
        if (!Wrapper.getModule(NotificationsModule.class).isEnabled() && (title.contains("Enabled Module") || title.contains("Disabled Module"))) {
            return;
        }
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        this.notifications.add(new Notification(scaledResolution.getScaledWidth() - 205, scaledResolution.getScaledHeight() - 30 * this.notifications.size(), type, title, description));
    }

    public void render() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        this.notifications.removeIf(Notification::shouldNotificationHide);
        float offset = 0.0f;
        for (Notification notification : this.notifications) {
            notification.setX((float)scaledResolution.getScaledWidth() - (float)((double)(notification.getWidth() + 5.0f) * notification.getAnimation().getAnimationFactor()));
            notification.setY((float)scaledResolution.getScaledHeight() - offset * 1.1f - notification.getHeight() - 5.0f);
            notification.draw(0.0f, 0.0f, 0);
            offset = (float)((double)offset + (double)notification.getHeight() * notification.getAnimation().getAnimationFactor());
        }
    }
}

