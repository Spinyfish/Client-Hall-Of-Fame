package me.jinthium.straight.impl.utils.render;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.game.RunGameLoopEvent;
import org.lwjglx.Sys;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public enum TaskUtil {
    INSTANCE;

    private final CopyOnWriteArrayList<TrayIcon> icons = new CopyOnWriteArrayList<>();
    private final SystemTray tray = SystemTray.getSystemTray();
    private final Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
    private TrayIcon trayIcon;
    private long initTime = 0L;

    public void sendNotification(MessageType messageType, String title, String content) {
        try {
            initTime = System.currentTimeMillis();
            trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);
            icons.add(trayIcon);
            trayIcon.displayMessage(title, content, messageType.getMessageType());
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Callback
    final EventCallback<RunGameLoopEvent> runGameLoopEventEventCallback = event -> {
        if(System.currentTimeMillis() - initTime >= 5000 && !icons.isEmpty()){
            for(TrayIcon icon : icons){
                tray.remove(icon);
                icons.remove(icon);
            }
            trayIcon = null;
        }
    };

    public enum MessageType {
        NONE(TrayIcon.MessageType.NONE),
        WARNING(TrayIcon.MessageType.WARNING),
        INFO(TrayIcon.MessageType.INFO),
        ERROR(TrayIcon.MessageType.ERROR);

        private final TrayIcon.MessageType messageType;

        MessageType(TrayIcon.MessageType messageType) {
            this.messageType = messageType;
        }

        public TrayIcon.MessageType getMessageType() {
            return messageType;
        }
    }
}
