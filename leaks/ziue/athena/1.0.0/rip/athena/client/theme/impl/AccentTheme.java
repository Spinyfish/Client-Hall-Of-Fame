package rip.athena.client.theme.impl;

import rip.athena.client.utils.render.*;
import java.awt.*;
import net.minecraft.util.*;
import java.util.*;
import javax.vecmath.*;

public enum AccentTheme implements ColorUtil
{
    ATHENA("Athena", new Color(7, 64, 170), new Color(4, 83, 97), EnumChatFormatting.DARK_PURPLE, false, new KeyColors[] { KeyColors.PURPLE, KeyColors.RED }), 
    SAPPHIRE("Sapphire", new Color(0, 85, 255), new Color(0, 170, 255), EnumChatFormatting.BLUE, false, new KeyColors[] { KeyColors.DARK_BLUE }), 
    RUBY("Ruby", new Color(255, 0, 85), new Color(255, 0, 170), EnumChatFormatting.RED, false, new KeyColors[] { KeyColors.RED }), 
    EMERALD("Emerald", new Color(0, 255, 85), new Color(0, 255, 170), EnumChatFormatting.GREEN, false, new KeyColors[] { KeyColors.DARK_GREEN }), 
    SUNFLOWER_SKY("Sunflower Sky", new Color(255, 204, 0), new Color(0, 170, 255), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.YELLOW, KeyColors.DARK_BLUE }), 
    SKYFIRE("Skyfire", new Color(0, 153, 255), new Color(255, 68, 0), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.DARK_BLUE, KeyColors.RED }), 
    CITRINE("Citrine", new Color(255, 255, 0), new Color(255, 200, 0), EnumChatFormatting.GOLD, false, new KeyColors[] { KeyColors.YELLOW }), 
    LAVENDER_FIELDS("Lavender Fields", new Color(170, 102, 255), new Color(204, 153, 255), EnumChatFormatting.LIGHT_PURPLE, false, new KeyColors[] { KeyColors.PURPLE, KeyColors.PINK }), 
    PEACH_MANGO("Peach Mango", new Color(255, 153, 102), new Color(255, 187, 68), EnumChatFormatting.GOLD, false, new KeyColors[] { KeyColors.ORANGE, KeyColors.YELLOW }), 
    OCEAN_BREEZE("Ocean Breeze", new Color(0, 187, 255), new Color(68, 136, 255), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.AQUA, KeyColors.DARK_BLUE }), 
    ROSE_GOLD("Rose Gold", new Color(255, 102, 102), new Color(255, 204, 153), EnumChatFormatting.GOLD, false, new KeyColors[] { KeyColors.PINK, KeyColors.ORANGE }), 
    LEMON_LIME("Lemon Lime", new Color(217, 255, 0), new Color(0, 255, 68), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.LIME, KeyColors.YELLOW }), 
    MISTY_ROSE("Misty Rose", new Color(255, 228, 225), new Color(240, 128, 128), EnumChatFormatting.RED, false, new KeyColors[] { KeyColors.PINK }), 
    SERENE("Serene", new Color(183, 234, 220), new Color(92, 172, 149), EnumChatFormatting.GREEN, false, new KeyColors[] { KeyColors.DARK_GREEN, KeyColors.DARK_GREEN }), 
    SUNRISE("Sunrise", new Color(252, 186, 152), new Color(252, 97, 85), EnumChatFormatting.GREEN, false, new KeyColors[] { KeyColors.ORANGE, KeyColors.RED }), 
    COCOA("Cocoa", new Color(167, 129, 105), new Color(90, 58, 42), EnumChatFormatting.GRAY, false, new KeyColors[] { KeyColors.GRAY, KeyColors.GRAY }), 
    GARDEN("Garden", new Color(152, 226, 187), new Color(51, 160, 81), EnumChatFormatting.GREEN, false, new KeyColors[] { KeyColors.DARK_GREEN, KeyColors.LIME }), 
    MOONLIGHT("Moonlight", new Color(56, 59, 80), new Color(13, 22, 50), EnumChatFormatting.DARK_BLUE, false, new KeyColors[] { KeyColors.DARK_BLUE, KeyColors.PURPLE }), 
    SUNSET("Sunset", new Color(255, 163, 108), new Color(255, 85, 68), EnumChatFormatting.YELLOW, false, new KeyColors[] { KeyColors.ORANGE, KeyColors.YELLOW }), 
    AMETHYST("Amethyst", new Color(164, 148, 213), new Color(97, 83, 140), EnumChatFormatting.LIGHT_PURPLE, false, new KeyColors[] { KeyColors.PURPLE, KeyColors.PURPLE }), 
    TROPICAL("Tropical", new Color(0, 191, 165), new Color(255, 149, 0), EnumChatFormatting.GREEN, false, new KeyColors[] { KeyColors.DARK_GREEN, KeyColors.YELLOW }), 
    AUBERGINE("Aubergine", new Color(170, 7, 107), new Color(97, 4, 95), EnumChatFormatting.DARK_PURPLE, false, new KeyColors[] { KeyColors.PURPLE, KeyColors.RED }), 
    AQUA("Aqua", new Color(185, 250, 255), new Color(79, 199, 200), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.AQUA }), 
    BANANA("Banana", new Color(253, 236, 177), new Color(255, 255, 255), EnumChatFormatting.YELLOW, false, new KeyColors[] { KeyColors.YELLOW }), 
    BLEND("Blend", new Color(71, 148, 253), new Color(71, 253, 160), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.AQUA, KeyColors.LIME }), 
    BLOSSOM("Blossom", new Color(226, 208, 249), new Color(49, 119, 115), EnumChatFormatting.DARK_AQUA, false, new KeyColors[] { KeyColors.PINK, KeyColors.GRAY }), 
    BUBBLEGUM("Bubblegum", new Color(243, 145, 216), new Color(152, 165, 243), EnumChatFormatting.LIGHT_PURPLE, false, new KeyColors[] { KeyColors.PINK, KeyColors.PURPLE }), 
    CANDY_CANE("Candy Cane", new Color(255, 255, 255), new Color(255, 0, 0), EnumChatFormatting.RED, false, new KeyColors[] { KeyColors.RED }), 
    CHERRY("Cherry", new Color(187, 55, 125), new Color(251, 211, 233), EnumChatFormatting.RED, false, new KeyColors[] { KeyColors.RED, KeyColors.PURPLE, KeyColors.PINK }), 
    CHRISTMAS("Christmas", new Color(255, 64, 64), new Color(255, 255, 255), new Color(64, 255, 64), EnumChatFormatting.RED, true, new KeyColors[] { KeyColors.RED, KeyColors.LIME }), 
    CORAL("Coral", new Color(244, 168, 150), new Color(52, 133, 151), EnumChatFormatting.DARK_AQUA, false, new KeyColors[] { KeyColors.PINK, KeyColors.ORANGE, KeyColors.DARK_BLUE }), 
    DIGITAL_HORIZON("Digital Horizon", new Color(95, 195, 228), new Color(229, 93, 135), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.AQUA, KeyColors.RED, KeyColors.PINK }), 
    EXPRESS("Express", new Color(173, 83, 137), new Color(60, 16, 83), EnumChatFormatting.DARK_PURPLE, false, new KeyColors[] { KeyColors.PURPLE, KeyColors.PINK }), 
    LIME_WATER("Lime Water", new Color(18, 255, 247), new Color(179, 255, 171), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.AQUA, KeyColors.LIME }), 
    LUSH("Lush", new Color(168, 224, 99), new Color(86, 171, 47), EnumChatFormatting.GREEN, false, new KeyColors[] { KeyColors.LIME, KeyColors.DARK_GREEN }), 
    HALOGEN("Halogen", new Color(255, 65, 108), new Color(255, 75, 43), EnumChatFormatting.RED, false, new KeyColors[] { KeyColors.RED, KeyColors.ORANGE }), 
    HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230), EnumChatFormatting.LIGHT_PURPLE, false, new KeyColors[] { KeyColors.PINK, KeyColors.DARK_BLUE, KeyColors.AQUA }), 
    MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226), EnumChatFormatting.BLUE, false, new KeyColors[] { KeyColors.DARK_BLUE, KeyColors.PURPLE }), 
    MAY("May", new Color(253, 219, 245), new Color(238, 79, 238), EnumChatFormatting.LIGHT_PURPLE, false, new KeyColors[] { KeyColors.PINK, KeyColors.PURPLE }), 
    ORANGE_JUICE("Orange Juice", new Color(252, 74, 26), new Color(247, 183, 51), EnumChatFormatting.GOLD, false, new KeyColors[] { KeyColors.ORANGE, KeyColors.YELLOW }), 
    PASTEL("Pastel", new Color(243, 155, 178), new Color(207, 196, 243), EnumChatFormatting.LIGHT_PURPLE, false, new KeyColors[] { KeyColors.PINK }), 
    PUMPKIN("Pumpkin", new Color(241, 166, 98), new Color(255, 216, 169), new Color(227, 139, 42), EnumChatFormatting.GOLD, true, new KeyColors[] { KeyColors.ORANGE }), 
    SATIN("Satin", new Color(215, 60, 67), new Color(140, 23, 39), EnumChatFormatting.RED, false, new KeyColors[] { KeyColors.RED }), 
    SNOWY_SKY("Snowy Sky", new Color(1, 171, 179), new Color(234, 234, 234), new Color(18, 232, 232), EnumChatFormatting.AQUA, true, new KeyColors[] { KeyColors.AQUA, KeyColors.GRAY }), 
    STEEL_FADE("Steel Fade", new Color(66, 134, 244), new Color(55, 59, 68), EnumChatFormatting.BLUE, false, new KeyColors[] { KeyColors.DARK_BLUE, KeyColors.GRAY }), 
    SUNDAE("Sundae", new Color(206, 74, 126), new Color(122, 44, 77), EnumChatFormatting.RED, false, new KeyColors[] { KeyColors.PINK, KeyColors.PURPLE, KeyColors.RED }), 
    SUNKIST("Sunkist", new Color(242, 201, 76), new Color(242, 153, 74), EnumChatFormatting.YELLOW, false, new KeyColors[] { KeyColors.YELLOW, KeyColors.ORANGE }), 
    WATER("Water", new Color(12, 232, 199), new Color(12, 163, 232), EnumChatFormatting.AQUA, false, new KeyColors[] { KeyColors.AQUA, KeyColors.DARK_BLUE }), 
    WINTER("Winter", Color.WHITE, Color.WHITE, EnumChatFormatting.GRAY, false, new KeyColors[] { KeyColors.GRAY, KeyColors.GRAY }), 
    WOOD("Wood", new Color(79, 109, 81), new Color(170, 139, 87), new Color(240, 235, 206), EnumChatFormatting.DARK_GREEN, true, new KeyColors[] { KeyColors.DARK_GREEN });
    
    private final String theme;
    private final Color firstColor;
    private final Color secondColor;
    private final Color thirdColor;
    private final EnumChatFormatting chatAccentColor;
    private final ArrayList<KeyColors> keyColors;
    private final boolean triColor;
    
    private AccentTheme(final String theme, final Color firstColor, final Color secondColor, final EnumChatFormatting chatAccentColor, final boolean triColor, final KeyColors[] keyColors) {
        this.theme = theme;
        this.thirdColor = firstColor;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<KeyColors>(Arrays.asList(keyColors));
        this.triColor = triColor;
    }
    
    private AccentTheme(final String theme, final Color firstColor, final Color secondColor, final Color thirdColor, final EnumChatFormatting chatAccentColor, final boolean triColor, final KeyColors[] keyColors) {
        this.theme = theme;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.thirdColor = thirdColor;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<KeyColors>(Arrays.asList(keyColors));
        this.triColor = triColor;
    }
    
    public Color getFirstColor() {
        return this.firstColor;
    }
    
    public Color getSecondColor() {
        return this.secondColor;
    }
    
    public Color getThirdColor() {
        return this.thirdColor;
    }
    
    public Color getAccentColor(final Vector2d screenCoordinates) {
        if (!this.triColor) {
            return ColorUtil.mixColors(this.getFirstColor(), this.getSecondColor(), this.getBlendFactor(screenCoordinates));
        }
        final double blendFactor = this.getBlendFactor(screenCoordinates);
        if (blendFactor <= 0.5) {
            return ColorUtil.mixColors(this.getSecondColor(), this.getFirstColor(), blendFactor * 2.0);
        }
        return ColorUtil.mixColors(this.getThirdColor(), this.getSecondColor(), (blendFactor - 0.5) * 2.0);
    }
    
    public double getWaveFactor(final Vector2d screenCoordinates) {
        final double waveLength = 200.0;
        final double waveSpeed = 0.01;
        final double offsetX = screenCoordinates.getX() / waveLength - System.currentTimeMillis() * waveSpeed;
        final double waveOffset = Math.sin(offsetX);
        return (waveOffset + 1.0) / 2.0;
    }
    
    private Color getColorFromWave(final double waveFactor) {
        final int red = (int)(this.getFirstColor().getRed() + waveFactor * (this.getSecondColor().getRed() - this.getFirstColor().getRed()));
        final int green = (int)(this.getFirstColor().getGreen() + waveFactor * (this.getSecondColor().getGreen() - this.getFirstColor().getGreen()));
        final int blue = (int)(this.getFirstColor().getBlue() + waveFactor * (this.getSecondColor().getBlue() - this.getFirstColor().getBlue()));
        return new Color(red, green, blue);
    }
    
    public Color getAccentColorWave(final Vector2d screenCoordinates) {
        final double waveIntensity = 0.5;
        final double blendFactor = this.getBlendFactor(screenCoordinates) * waveIntensity;
        if (!this.triColor) {
            return ColorUtil.mixColors(this.getFirstColor(), this.getSecondColor(), blendFactor);
        }
        final Color color1 = ColorUtil.mixColors(this.getSecondColor(), this.getFirstColor(), blendFactor);
        final Color color2 = ColorUtil.mixColors(this.getThirdColor(), this.getSecondColor(), blendFactor);
        if (blendFactor <= 0.5) {
            return ColorUtil.mixColors(color2, color1, blendFactor * 2.0);
        }
        return ColorUtil.mixColors(color1, color2, (blendFactor - 0.5) * 2.0);
    }
    
    public Color getAccentColorWave() {
        return this.getAccentColorWave(new Vector2d(0.0, 0.0));
    }
    
    public Color getAccentColor() {
        return this.getAccentColor(new Vector2d(0.0, 0.0));
    }
    
    @Deprecated
    public int getRound() {
        return 4;
    }
    
    public Color getDropShadow() {
        return new Color(0, 0, 0, 160);
    }
    
    public double getBlendFactor(final Vector2d screenCoordinates) {
        return Math.sin(System.currentTimeMillis() / 600.0 + screenCoordinates.getX() * 0.005 + screenCoordinates.getY() * 0.06) * 0.5 + 0.5;
    }
    
    @Deprecated
    public Color getBackgroundShade() {
        return new Color(0, 0, 0, 100);
    }
    
    public String getTheme() {
        return this.theme;
    }
    
    public EnumChatFormatting getChatAccentColor() {
        return this.chatAccentColor;
    }
    
    public ArrayList<KeyColors> getKeyColors() {
        return this.keyColors;
    }
    
    public boolean isTriColor() {
        return this.triColor;
    }
    
    public enum KeyColors
    {
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
        
        public Color getColor() {
            return this.color;
        }
        
        private KeyColors(final Color color) {
            this.color = color;
        }
    }
}
