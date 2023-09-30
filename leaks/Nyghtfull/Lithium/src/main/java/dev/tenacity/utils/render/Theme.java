package dev.tenacity.utils.render;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.ModeSetting;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum Theme {
    SPEARMINT("Spearmint", new Color(97, 194, 162), new Color(65, 130, 108)),
    JADE_GREEN("Jade Green", new Color(0, 168, 107), new Color(0, 105, 66)),
    GREEN_SPIRIT("Green Spirit", new Color(0, 135, 62), new Color(159, 226, 191), true),
    ROSY_PINK("Rosy Pink", new Color(255, 102, 204), new Color(191, 77, 153)),
    MAGENTA("Magenta", new Color(213, 63, 119), new Color(157, 68, 110)),
    HOT_PINK("Hot Pink", new Color(231, 84, 128), new Color(172, 79, 198), true),
    LAVENDER("Lavender", new Color(219, 166, 247), new Color(152, 115, 172)),
    AMETHYST("Amethyst", new Color(144, 99, 205), new Color(98, 67, 140)),
    PURPLE_FIRE("Purple Fire", new Color(104, 71, 141), new Color(177, 162, 202), true),
    SUNSET_PINK("Sunset Pink", new Color(255, 145, 20), new Color(245, 105, 231), true),
    BLAZE_ORANGE("Blaze Orange", new Color(255, 169, 77), new Color(255, 130, 0)),
    PINK_BLOOD("Pink Blood", new Color(228, 0, 70), new Color(255, 166, 201), true),
    PASTEL("Pastel", new Color(255, 109, 106), new Color(191, 82, 80)),
    NEON_RED("Neon Red", new Color(210, 39, 48), new Color(184, 25, 42)),
    RED_COFFEE("Red Coffee", Color.BLACK, new Color(225, 34, 59)),
    DEEP_OCEAN("Deep Ocean", new Color(60, 82, 145), new Color(0, 20, 64), true),
    CHAMBRAY_BLUE("Chambray Blue", new Color(33, 46, 182), new Color(60, 82, 145)),
    MINT_BLUE("Mint Blue", new Color(66, 158, 157), new Color(40, 94, 93)),
    PACIFIC_BLUE("Pacific Blue", new Color(5, 169, 199), new Color(4, 115, 135)),
    TROPICAL_ICE("Tropical Ice", new Color(102, 255, 209), new Color(6, 149, 255), true),
    TENACITY("Tenacity", Tenacity.INSTANCE.getClientColor(), Tenacity.INSTANCE.getAlternateClientColor(), true),
    CUSTOM_THEME("Custom Theme", HUDMod.color1.getColor(), HUDMod.color2.getColor());

    private static final Map<String, Theme> themeMap = new HashMap<>();

    private final String name;
    private final Pair<Color, Color> colors;
    private final boolean  gradient;

    Theme(String name, Color color, Color colorAlt) {
        this(name, color, colorAlt, false);
    }

    Theme(String name, Color color, Color colorAlt, boolean gradient) {
        this.name = name;
        colors = Pair.of(color, colorAlt);
        this.gradient = gradient;
    }

    public static void init() {
        Arrays.stream(values()).forEach(theme -> themeMap.put(theme.getName(), theme));
    }

    public Pair<Color, Color> getColors() {
        if (this.equals(Theme.CUSTOM_THEME)) {
            if (HUDMod.color1.isRainbow()) {
                return Pair.of(HUDMod.color1.getColor(), HUDMod.color1.getAltColor());
            } else return Pair.of(HUDMod.color1.getColor(), HUDMod.color2.getColor());
        } else return colors;
    }

    public static Pair<Color, Color> getThemeColors(String name) {
        return get(name).getColors();
    }

    public static ModeSetting getModeSetting(String name, String defaultValue) {
        return new ModeSetting(name, defaultValue, Arrays.stream(Theme.values()).map(Theme::getName).toArray(String[]::new));
    }

    public static Theme get(String name) {
        return themeMap.get(name);
    }

    public static Theme getCurrentTheme() {
        return Theme.get(HUDMod.theme.getMode());
    }
}
