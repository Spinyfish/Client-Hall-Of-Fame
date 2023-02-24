package me.kansio.client.config;

import lombok.Getter;
import lombok.Setter;
import me.kansio.client.gui.notification.Notification;
import me.kansio.client.gui.notification.NotificationManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    @Getter @Setter private String name;
    @Getter @Setter private String author;
    @Getter @Setter private String lastUpdated;
    @Getter @Setter private File file;
    @Getter @Setter private boolean isOnline;

    public Config(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public Config(String name, String author, String lastUpdated, boolean online, File file) {
        this.author = author;
        this.lastUpdated = lastUpdated;
        this.name = name;
        this.file = file;
        this.isOnline = online;
    }

    public void rename(String newName) {
        try {
            Path original = Paths.get(file.getCanonicalPath());
            Path to = Paths.get(file.getPath());

            Files.move(original, to);
        } catch (Exception e) {
            NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.ERROR, "Error!", "Couldn't rename config!", 1));
            e.printStackTrace();
        }
    }

}
