package com.alan.clients.ui.theme;

import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.vector.Vector2d;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static net.minecraft.util.EnumChatFormatting.*;

/**
 * @author Hazsi
 * @since 10/11/2022
 */
@Getter
public enum Themes implements ColorUtil {

    // Primary accent needs to be the first color
    AUBERGINE("Aubergine", new Color(170, 7, 107), new Color(97, 4, 95), DARK_PURPLE, KeyColors.PURPLE, KeyColors.RED),
    AQUA("Aqua", new Color(185, 250, 255), new Color(79, 199, 200), EnumChatFormatting.AQUA, KeyColors.AQUA),
    BANANA("Banana", new Color(253, 236, 177), new Color(255, 255, 255), YELLOW, KeyColors.YELLOW),
    BLEND("Blend", new Color(71, 148, 253), new Color(71, 253, 160), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.LIME),
    BLOSSOM("Blossom", new Color(226, 208, 249), new Color(49, 119, 115), DARK_AQUA, KeyColors.PINK, KeyColors.GRAY),
    BUBBLEGUM("Bubblegum", new Color(243, 145, 216), new Color(152, 165, 243), LIGHT_PURPLE, KeyColors.PINK, KeyColors.PURPLE),
    CANDY_CANE("Candy Cane", new Color(255, 255, 255), new Color(255, 0, 0), RED, KeyColors.RED),
    CHERRY("Cherry", new Color(187, 55, 125), new Color(251, 211, 233), RED, KeyColors.RED, KeyColors.PURPLE, KeyColors.PINK),
    CHRISTMAS("Christmas", new Color(255, 64, 64), new Color(255, 255, 255), new Color(64, 255, 64), RED, KeyColors.RED, KeyColors.LIME),
    CORAL("Coral", new Color(244, 168, 150), new Color(52, 133, 151), DARK_AQUA, KeyColors.PINK, KeyColors.ORANGE, KeyColors.DARK_BLUE),
    DIGITAL_HORIZON("Digital Horizon", new Color(95, 195, 228), new Color(229, 93, 135), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.RED, KeyColors.PINK),
    EXPRESS("Express", new Color(173, 83, 137), new Color(60, 16, 83), DARK_PURPLE, KeyColors.PURPLE, KeyColors.PINK),
    LIME_WATER("Lime Water", new Color(18, 255, 247), new Color(179, 255, 171), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.LIME),
    LUSH("Lush", new Color(168, 224, 99), new Color(86, 171, 47), GREEN, KeyColors.LIME, KeyColors.DARK_GREEN),
    HALOGEN("Halogen", new Color(255, 65, 108), new Color(255, 75, 43), RED, KeyColors.RED, KeyColors.ORANGE),
    HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230), LIGHT_PURPLE, KeyColors.PINK, KeyColors.DARK_BLUE, KeyColors.AQUA),
    MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226), BLUE, KeyColors.DARK_BLUE, KeyColors.PURPLE),
    MAY("May", new Color(253, 219, 245), new Color(238, 79, 238), LIGHT_PURPLE, KeyColors.PINK, KeyColors.PURPLE),
    ORANGE_JUICE("Orange Juice", new Color(252, 74, 26), new Color(247, 183, 51), GOLD, KeyColors.ORANGE, KeyColors.YELLOW),
    PASTEL("Pastel", new Color(243, 155, 178), new Color(207, 196, 243), LIGHT_PURPLE, KeyColors.PINK),
    PUMPKIN("Pumpkin", new Color(241, 166, 98), new Color(255, 216, 169), new Color(227, 139, 42), GOLD, KeyColors.ORANGE),
    SATIN("Satin", new Color(215, 60, 67), new Color(140, 23, 39), RED, KeyColors.RED),
    SNOWY_SKY("Snowy Sky", new Color(1, 171, 179), new Color(234, 234, 234), new Color(18, 232, 232), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.GRAY),
    STEEL_FADE("Steel Fade", new Color(66, 134, 244), new Color(55, 59, 68), BLUE, KeyColors.DARK_BLUE, KeyColors.GRAY),
    SUNDAE("Sundae", new Color(206, 74, 126), new Color(122, 44, 77), RED, KeyColors.PINK, KeyColors.PURPLE, KeyColors.RED),
    SUNKIST("Sunkist", new Color(242, 201, 76), new Color(242, 153, 74), YELLOW, KeyColors.YELLOW, KeyColors.ORANGE),
    WATER("Water", new Color(12, 232, 199), new Color(12, 163, 232), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.DARK_BLUE),
    WINTER("Winter", Color.WHITE, Color.WHITE, GRAY, KeyColors.GRAY, KeyColors.GRAY),
    WOOD("Wood", new Color(79, 109, 81), new Color(170, 139, 87), new Color(240, 235, 206), DARK_GREEN, KeyColors.DARK_GREEN);

    private final String themeName;
    private final Color firstColor, secondColor, thirdColor;
    private final EnumChatFormatting chatAccentColor;
    private final ArrayList<KeyColors> keyColors;
    private final boolean triColor;

    // Constructor for bicolor themes (only two colors)
    Themes(String themeName, Color firstColor, Color secondColor, EnumChatFormatting chatAccentColor, KeyColors... keyColors) {
        this.themeName = themeName;
        this.firstColor = this.thirdColor = firstColor;
        this.secondColor = secondColor;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
        this.triColor = false;
    }

    // Constructor for tricolor themes (three colors)
    Themes(String themeName, Color firstColor, Color secondColor, Color thirdColor, EnumChatFormatting chatAccentColor, KeyColors... keyColors) {
        this.themeName = themeName;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.thirdColor = thirdColor;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
        this.triColor = true;
    }

    /**
     * Calculates the accent color at a specific screen point. Depending on the position on the screen, the
     * accent has a slightly different color (blending between the two or three accent colors) to create an
     * interesting gradient effect.
     *
     * @param screenCoordinates The screen coordinates to calculate the accent color for
     * @return The determined target color for the provided coordinates, between the two or three accent colors of the theme
     * @author Hazsi
     * @since 10/11/2022
     */
    public Color getAccentColor(Vector2d screenCoordinates) {

        // Three color blending
        if (this.triColor) {
            double blendFactor = this.getBlendFactor(screenCoordinates);

            // Blend between first and second color
            if (blendFactor <= 0.5) return ColorUtil.mixColors(getSecondColor(), getFirstColor(), blendFactor * 2D);
                // Blend between second and third color
            else return ColorUtil.mixColors(getThirdColor(), getSecondColor(), (blendFactor - 0.5) * 2D);
        }

        // Two color blending
        return ColorUtil.mixColors(getFirstColor(), getSecondColor(), getBlendFactor(screenCoordinates));
    }

    public Color getAccentColor() {
        return getAccentColor(new Vector2d(0.0, 0.0));
    }

    @Deprecated
    public int getRound() {
        return 4;
    }

    public Color getDropShadow() {
        return new Color(0, 0, 0, 160);
    }

    /**
     * Determines the blending factor between the themes two accent colors at a specific screen coordinate
     *
     * @param screenCoordinates The screen coordinate to calculate the blend factor for
     * @return The blending factor, in a range of [0, 1] (inclusive) between the two accent colors of the theme
     * @author Hazsi
     * @since 10/11/2022
     */
    public double getBlendFactor(Vector2d screenCoordinates) {
        return Math.sin(System.currentTimeMillis() / 600.0D
                + screenCoordinates.getX() * 0.005D
                + screenCoordinates.getY() * 0.06D
        ) * 0.5D + 0.5D;
    }

    @Deprecated
    public Color getBackgroundShade() {
        return new Color(0, 0, 0, 100);
    }

    @Getter
    @AllArgsConstructor
    public enum KeyColors {
        RED(new Color(255, 50, 50)),
        ORANGE(new Color(255, 128, 50)),
        YELLOW(new Color(255, 255, 50)),
        LIME(new Color(128, 255, 50)),
        DARK_GREEN(new Color(50, 128, 50)),
        AQUA(new Color(50, 200, 255)),
        DARK_BLUE(new Color(50, 100, 200)),
        PURPLE(new Color(128, 50, 255)),
        PINK(new Color(255, 128, 255)),
        GRAY(new Color(100, 100, 110));

        private final Color color;
    }
}