package com.alan.clients.notification;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.notification.impl.ErrorNotification;
import com.alan.clients.notification.impl.InfoNotification;
import com.alan.clients.notification.impl.WarningNotification;
import com.alan.clients.util.interfaces.InstanceAccess;

import java.util.ArrayList;

public final class NotificationManager extends ArrayList<Notification> implements InstanceAccess {
    public void init() {
        Client.INSTANCE.getEventBus().register(this);
    }

    public void register(final String title, final String content, final NotificationType type) {
        final long length = nunitoNormal.width(content) * 30L;
        final Notification notification;

        switch (type) {
            case WARNING:
                notification = new WarningNotification(title, content, length);
                break;
            case ERROR:
                notification = new ErrorNotification(title, content, length);
                break;
            default:
                notification = new InfoNotification(title, content, length);
                break;
        }

        this.add(notification);
    }

    @EventLink()
    public final Listener<LimitedRender2DEvent> onLimitedRenderEvent = event -> {
        int i = 0;

        for (final Notification notification : this) {
            notification.render(i);
            ++i;
        }

        this.removeIf(Notification::isEnded);
    };
}
