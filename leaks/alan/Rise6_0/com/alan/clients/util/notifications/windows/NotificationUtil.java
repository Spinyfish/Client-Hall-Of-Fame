package com.alan.clients.util.notifications.windows;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

public class NotificationUtil {
    public static void sendNotification(final MessageType messageType, final String title, final String content) {
        try {
            final SystemTray tray = SystemTray.getSystemTray();
            final Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            final TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);
            trayIcon.displayMessage(title, content, messageType.getMessageType());
        } catch (final AWTException e) {
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    @Getter
    public enum MessageType {
        NONE(TrayIcon.MessageType.NONE),
        WARNING(TrayIcon.MessageType.WARNING),
        INFO(TrayIcon.MessageType.INFO),
        ERROR(TrayIcon.MessageType.ERROR);

        private final TrayIcon.MessageType messageType;
    }
}
