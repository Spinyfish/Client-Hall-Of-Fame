package io.github.raze.themes.system;

import java.awt.*;

public class BaseTheme {

    public String name;

    public Color background, darkBackground;
    public Color foreground, darkForeground;
    public Color accent, darkAccent;

    public BaseTheme(String name, Color background, Color darkBackground, Color foreground, Color darkForeground, Color accent, Color darkAccent) {
        this.name = name;
        this.background = background;
        this.darkBackground = darkBackground;
        this.foreground = foreground;
        this.darkForeground = darkForeground;
        this.accent = accent;
        this.darkAccent = darkAccent;
    }

    public String getName() {
        return name;
    }

    public Color getBackground() {
        return background;
    }

    public Color getDarkBackground() {
        return darkBackground;
    }

    public Color getForeground() {
        return foreground;
    }

    public Color getDarkForeground() {
        return darkForeground;
    }

    public Color getAccent() {
        return accent;
    }

    public Color getDarkAccent() {
        return darkAccent;
    }
}
