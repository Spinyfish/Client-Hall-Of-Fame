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
    AZURE("Azure", new Color(239, 50, 217), new Color(137, 255, 253), LIGHT_PURPLE, KeyColors.PINK, KeyColors.AQUA),
    BANANA("Banana", new Color(253, 236, 177), new Color(255, 255, 255), YELLOW, KeyColors.YELLOW),
    BLASTER("Blaster", new Color(255, 172, 94), new Color(199, 121, 208), new Color(75, 192, 200), LIGHT_PURPLE, KeyColors.ORANGE, KeyColors.PINK, KeyColors.AQUA),
    BLEND("Blend", new Color(71, 148, 253), new Color(71, 253, 160), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.LIME),
    BLOSSOM("Blossom", new Color(226, 208, 249), new Color(49, 119, 115), DARK_AQUA, KeyColors.PINK, KeyColors.GRAY),
    BUBBLEGUM("Bubblegum", new Color(243, 145, 216), new Color(152, 165, 243), LIGHT_PURPLE, KeyColors.PINK, KeyColors.PURPLE),
    CANDY_CANE("Candy Cane", new Color(255, 0, 0), new Color(255, 255, 255), RED, KeyColors.RED),
    CANTALOUPE("Cantaloupe", new Color(248, 255, 174), new Color(67, 198, 172), YELLOW, KeyColors.YELLOW, KeyColors.LIME, KeyColors.DARK_GREEN),
    CARBON_FIBRE("Carbon Fibre", new Color(77, 79, 81), new Color(35, 37, 38), GRAY, KeyColors.GRAY),
    CHERRY("Cherry", new Color(187, 55, 125), new Color(251, 211, 233), RED, KeyColors.RED, KeyColors.PURPLE, KeyColors.PINK),
    CHRISTMAS("Christmas", new Color(255, 64, 64), new Color(255, 255, 255), new Color(64, 255, 64), RED, KeyColors.RED, KeyColors.LIME),
    CORAL("Coral", new Color(244, 168, 150), new Color(52, 133, 151), DARK_AQUA, KeyColors.PINK, KeyColors.ORANGE, KeyColors.DARK_BLUE),
    DIGITAL_HORIZON("Digital Horizon", new Color(95, 195, 228), new Color(229, 93, 135), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.RED, KeyColors.PINK),
    EXPRESS("Express", new Color(173, 83, 137), new Color(60, 16, 83), DARK_PURPLE, KeyColors.PURPLE, KeyColors.PINK),
    JELLO_SHOT("Jello Shot", new Color(36, 254, 65), new Color(253, 252, 71), YELLOW, KeyColors.LIME, KeyColors.YELLOW),
    LATTE("Latte", new Color(158, 118, 118), new Color(255, 248, 234), WHITE, KeyColors.GRAY),
    LIME_WATER("Lime Water", new Color(18, 255, 247), new Color(179, 255, 171), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.LIME),
    LUSH("Lush", new Color(168, 224, 99), new Color(86, 171, 47), GREEN, KeyColors.LIME, KeyColors.DARK_GREEN),
    HALOGEN("Halogen", new Color(255, 65, 108), new Color(255, 75, 43), RED, KeyColors.RED, KeyColors.ORANGE),
    HAZARD("Hazard", new Color(255, 250, 0), new Color(255, 255, 255), YELLOW, KeyColors.YELLOW),
    HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230), LIGHT_PURPLE, KeyColors.PINK, KeyColors.DARK_BLUE, KeyColors.AQUA),
    MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226), BLUE, KeyColors.DARK_BLUE, KeyColors.PURPLE),
    MAY("May", new Color(238, 79, 238), new Color(253, 219, 245), LIGHT_PURPLE, KeyColors.PINK, KeyColors.PURPLE),
    MINTY("Minty", new Color(148, 235, 194), new Color(31, 64, 55), GREEN, KeyColors.LIME, KeyColors.DARK_GREEN),
    ORANGE_JUICE("Orange Juice", new Color(252, 74, 26), new Color(247, 183, 51), GOLD, KeyColors.ORANGE, KeyColors.YELLOW),
    OUTRUN("Outrun", new Color(239, 77, 160), new Color(7, 0, 82), LIGHT_PURPLE, KeyColors.PINK, KeyColors.PURPLE, KeyColors.DARK_BLUE),
    OVERDRIVE("Overdrive", new Color(131, 57, 179), new Color(253, 29, 29), new Color(252, 176, 69), DARK_PURPLE, KeyColors.PURPLE, KeyColors.RED, KeyColors.ORANGE),
    PASTEL("Pastel", new Color(243, 155, 178), new Color(207, 196, 243), LIGHT_PURPLE, KeyColors.PINK),
    PINE("Pine", new Color(206, 212, 106), new Color(7, 85, 59), DARK_GREEN, KeyColors.LIME, KeyColors.DARK_GREEN),
    PUMPKIN("Pumpkin", new Color(241, 166, 98), new Color(255, 216, 169), new Color(227, 139, 42), GOLD, KeyColors.ORANGE),
    POLARIZED("Polarized", new Color(173, 239, 209), new Color(0, 32, 64), BLUE, KeyColors.DARK_BLUE),
    SATIN("Satin", new Color(215, 60, 67), new Color(140, 23, 39), RED, KeyColors.RED),
    SNOWY_SKY("Snowy Sky", new Color(1, 171, 179), new Color(234, 234, 234), new Color(111, 115, 123), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.GRAY),
    STEEL_FADE("Steel Fade", new Color(66, 134, 244), new Color(55, 59, 68), BLUE, KeyColors.DARK_BLUE, KeyColors.GRAY),
    SUNDAE("Sundae", new Color(206, 74, 126), new Color(28, 28, 27), RED, KeyColors.PINK, KeyColors.PURPLE, KeyColors.RED),
    SUNKIST("Sunkist", new Color(242, 201, 76), new Color(242, 153, 74), YELLOW, KeyColors.YELLOW, KeyColors.ORANGE),
    SWEET_MORNING("Sweet Morning", new Color(255, 95, 109), new Color(255, 195, 113), RED, KeyColors.RED, KeyColors.YELLOW, KeyColors.ORANGE),
    SYNCHRONIZED("Synchronized", new Color(247, 255, 0), new Color(219, 54, 164), YELLOW, KeyColors.YELLOW, KeyColors.PINK),
    TERMINAL("Terminal", new Color(15, 155, 15), new Color(25, 30, 25), DARK_GREEN, KeyColors.DARK_GREEN, KeyColors.GRAY),
    TITANIUM("Titanium", new Color(133, 147, 152), new Color(40, 48, 72), GRAY, KeyColors.GRAY),
    WATER("Water", new Color(12, 232, 199), new Color(12, 163, 232), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.DARK_BLUE),
    WATERMELON("Watermelon", new Color(236, 68, 155), new Color(153, 244, 67), LIGHT_PURPLE, KeyColors.PINK, KeyColors.LIME),
    WINTER_STORM("Winter Storm", new Color(230, 218, 218), new Color(39, 64, 70), GRAY, KeyColors.PINK, KeyColors.GRAY),
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