package tech.dort.dortware.impl.notification;

import tech.dort.dortware.api.notification.AbstractNotification;

public class Error extends AbstractNotification {

    public Error(String message, int x, int y, long endTime) {
        super(message, x, y, endTime);
    }

    @Override
    public void update() {

    }
}
