package me.kansio.client.utils.render;

import java.awt.*;

public enum ColorPalette {

    RED(new Color(244, 67, 54)),
    PINK(new Color(233, 30, 99)),
    PURPLE(new Color(156, 39, 176)),
    DEEP_PURPLE(new Color(103, 58, 183)),
    INDIGO(new Color(63, 81, 181)),
    BLUE(new Color(33, 150, 243)),
    LIGHT_BLUE(new Color(3, 169, 244)),
    CYAN(new Color(0, 188, 212)),
    TEAL(new Color(0, 150, 136)),
    GREEN(new Color(76, 175, 80)),
    LIGHT_GREEN(new Color(139, 195, 74)),
    LIME(new Color(205, 220, 57)),
    YELLOW(new Color(255, 235, 59)),
    AMBER(new Color(255, 193, 7)),
    ORANGE(new Color(255, 152, 0)),
    DEEP_ORANGE(new Color(255, 87, 34)),
    BROWN(new Color(121, 85, 72)),
    GREY(new Color(158, 158, 158)),
    DARK_GREY(new Color(38, 38, 38)),
    BLUE_GREY(new Color(96, 125, 139));

    private final Color color;

    ColorPalette(Color var) {
        color =  var;
    }

    public final Color getColor() {
        return color;
    }

}
