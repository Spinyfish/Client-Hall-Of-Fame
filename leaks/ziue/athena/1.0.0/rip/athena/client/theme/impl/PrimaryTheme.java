package rip.athena.client.theme.impl;

import rip.athena.client.utils.render.*;
import java.awt.*;

public enum PrimaryTheme implements ColorUtil
{
    DARK("Dark", new Color(30, 31, 35, 255).getRGB(), new Color(43, 44, 48, 255).getRGB(), new Color(35, 35, 35, 255).getRGB(), -1), 
    WHITE("White", new Color(255, 255, 255, 255).getRGB(), new Color(100, 100, 100, 255).getRGB(), new Color(50, 50, 50, 255).getRGB(), new Color(0, 0, 0).getRGB()), 
    TRANSPARENT("Gradient", new Color(200, 200, 200, 80).getRGB(), new Color(150, 150, 150, 100).getRGB(), new Color(100, 100, 100, 255).getRGB(), new Color(255, 255, 255).getRGB());
    
    private final String theme;
    private final int firstColor;
    private final int secondColor;
    private final int thirdColor;
    private final int textColor;
    
    private PrimaryTheme(final String theme, final int firstColor, final int secondColor, final int thirdColor, final int textColor) {
        this.theme = theme;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.thirdColor = thirdColor;
        this.textColor = textColor;
    }
    
    public String getTheme() {
        return this.theme;
    }
    
    public int getFirstColor() {
        return this.firstColor;
    }
    
    public int getSecondColor() {
        return this.secondColor;
    }
    
    public int getThirdColor() {
        return this.thirdColor;
    }
    
    public int getTextColor() {
        return this.textColor;
    }
}
