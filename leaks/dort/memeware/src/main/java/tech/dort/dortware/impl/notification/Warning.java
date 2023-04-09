package tech.dort.dortware.impl.notification;

import tech.dort.dortware.api.notification.AbstractNotification;

public class Warning extends AbstractNotification {

    public Warning(String message, int x, int y, long endTime) {
        super(message, x, y, endTime);
    }

    @Override
    public void update() {

    }
}
