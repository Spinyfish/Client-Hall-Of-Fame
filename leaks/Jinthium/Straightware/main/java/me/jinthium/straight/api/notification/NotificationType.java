package me.jinthium.straight.api.notification;


import me.jinthium.straight.api.clickgui.FontAwesomeIcons;

import java.awt.*;


public enum NotificationType {
    INFO("3", Color.blue),
    WARNING("4" ,Color.yellow),
    ERROR("2", Color.red);

    private final String icon;
    private final Color notiColor;

    NotificationType(String icon, Color notiColor) {
        this.icon = icon;
        this.notiColor = notiColor;
    }

    public String getIcon() {
        return icon;
    }

    public Color getNotiColor() {
        return notiColor;
    }
}
