package com.alan.clients.notification.impl;

import com.alan.clients.notification.Notification;

public final class WarningNotification extends Notification {

    public WarningNotification(final String title, final String content, final long delay) {
        super(title, content, delay);
    }

    @Override
    public void render(final int multiplierY) {

    }

    @Override
    public boolean isEnded() {
        return false;
    }
}
