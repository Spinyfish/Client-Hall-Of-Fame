package rip.athena.client.font;

import java.awt.*;
import java.io.*;

public class FontManager
{
    public static FontUtils normal;
    public static FontUtils normal2;
    public static FontUtils big;
    public static FontUtils small;
    public static FontUtils tiny;
    public static FontUtils robotoL15;
    public static FontUtils robotoL18;
    public static FontUtils robotoL20;
    public static FontUtils robotoL40;
    public static FontUtils roboto12;
    public static FontUtils roboto15;
    public static FontUtils roboto16;
    public static FontUtils baloo16;
    public static FontUtils baloo17;
    public static FontUtils baloo30;
    public static FontUtils SFBOLD;
    public static FontUtils vision16;
    public static FontUtils vision30;
    public static FontUtils icon10;
    public static FontUtils icon14;
    public static FontUtils icon15;
    public static FontUtils icon16;
    public static FontUtils icon18;
    public static FontUtils icon20;
    public static FontUtils icon25;
    public static FontUtils icon30;
    public static FontUtils icon40;
    public static FontUtils icon35;
    public static FontUtils font1;
    public static FontUtils font2;
    public static FontUtils font3;
    public static FontUtils font4;
    public static FontUtils font5;
    public static FontUtils sans13;
    public static FontUtils sans14;
    public static FontUtils sans16;
    public static FontUtils sans18;
    public static FontUtils hudetc;
    public static FontUtils productsans;
    public static FontUtils productsans2;
    public static FontUtils sans20;
    public static FontUtils sans24;
    public static FontUtils poppinsBold20;
    public static FontUtils poppins16;
    public static FontUtils poppins18;
    public static FontUtils comfortaa;
    public static FontUtils wqy18;
    public static FontUtils wqy15;
    public static FontUtils tahoma9;
    public static FontUtils tahoma11;
    public static FontUtils tahoma11bold;
    public static FontUtils tahoma13;
    public static FontUtils tahoma13bold;
    public static FontUtils productsans16;
    public static FontUtils ModernaSansLight;
    
    public static Font getFont(final String name, final int size) {
        Font font;
        try {
            final InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/Athena/font/" + name);
            font = Font.createFont(0, is);
            font = font.deriveFont(0, (float)size);
        }
        catch (Exception ex) {
            System.out.println("Error loading font " + name);
            font = new Font("Arial", 0, size);
        }
        return font;
    }
    
    static {
        FontManager.normal = new FontUtils("Arial.ttf", 0, 16, 7, false);
        FontManager.normal2 = new FontUtils("ArialBold.ttf", 0, 16, 7, false);
        FontManager.big = new FontUtils("Arial.ttf", 0, 24, 7, false);
        FontManager.small = new FontUtils("Roboto.ttf", 0, 16, 7, false);
        FontManager.tiny = new FontUtils("Roboto.ttf", 0, 14, 7, false);
        FontManager.robotoL15 = new FontUtils("Arial.ttf", 0, 15, 7, false);
        FontManager.robotoL18 = new FontUtils("Arial.ttf", 0, 18, 7, false);
        FontManager.robotoL20 = new FontUtils("RobotoLight.ttf", 0, 20, 7, false);
        FontManager.robotoL40 = new FontUtils("Arial.ttf", 0, 40, 7, false);
        FontManager.roboto12 = new FontUtils("Arial.ttf", 0, 12, 7, false);
        FontManager.roboto15 = new FontUtils("Arial.ttf", 0, 15, 7, false);
        FontManager.roboto16 = new FontUtils("Arial.ttf", 0, 16, 7, false);
        FontManager.baloo16 = new FontUtils("Arial.ttf", 0, 16, 7, false);
        FontManager.baloo17 = new FontUtils("Arial.ttf", 1, 17, 7, false);
        FontManager.baloo30 = new FontUtils("Arial.ttf", 0, 30, 7, false);
        FontManager.SFBOLD = new FontUtils("Arial.ttf", 1, 30, 7, false);
        FontManager.vision16 = new FontUtils("Vision.otf", 1, 26, 7, false, 5);
        FontManager.vision30 = new FontUtils("Vision.otf", 1, 30, 7, false, 5);
        FontManager.icon10 = new FontUtils("Icon.ttf", 0, 10, 7, false);
        FontManager.icon14 = new FontUtils("Icon.ttf", 0, 14, 7, false);
        FontManager.icon15 = new FontUtils("Icon.ttf", 0, 15, 7, false);
        FontManager.icon16 = new FontUtils("Icon.ttf", 0, 16, 7, false);
        FontManager.icon18 = new FontUtils("Icon.ttf", 0, 18, 7, false);
        FontManager.icon20 = new FontUtils("Icon.ttf", 0, 20, 7, false);
        FontManager.icon25 = new FontUtils("Icon.ttf", 0, 25, 7, false);
        FontManager.icon30 = new FontUtils("Icon.ttf", 0, 30, 7, false);
        FontManager.icon40 = new FontUtils("Icon.ttf", 0, 40, 7, false);
        FontManager.icon35 = new FontUtils("Icon2.ttf", 0, 35, 7, false);
        FontManager.font1 = new FontUtils("Vision.otf", 0, 40, 7, false, 7);
        FontManager.font2 = new FontUtils("Arial.ttf", 0, 15, 7, false);
        FontManager.font3 = new FontUtils("Arial.ttf", 0, 10, 7, false);
        FontManager.font4 = new FontUtils("Icon.ttf", 0, 150, 7, false);
        FontManager.font5 = new FontUtils("Arial.ttf", 0, 12, 7, false);
        FontManager.sans13 = new FontUtils("Arial.ttf", 0, 13, 7, false);
        FontManager.sans14 = new FontUtils("Arial.ttf", 0, 14, 7, false);
        FontManager.sans16 = new FontUtils("Arial.ttf", 0, 16, 7, false);
        FontManager.sans18 = new FontUtils("Arial.ttf", 0, 18, 7, false);
        FontManager.hudetc = new FontUtils("Arial.ttf", 0, 18, 7, false);
        FontManager.productsans = new FontUtils("astrofont.ttf", 0, 18, 7, false);
        FontManager.productsans2 = new FontUtils("astrofont.ttf", 0, 26, 7, false);
        FontManager.sans20 = new FontUtils("sans.ttf", 0, 20, 7, false);
        FontManager.sans24 = new FontUtils("sans.ttf", 0, 24, 7, false);
        FontManager.poppinsBold20 = new FontUtils("Arial.ttf", 0, 20, 7, false);
        FontManager.poppins16 = new FontUtils("Arial.ttf", 0, 16, 7, false);
        FontManager.poppins18 = new FontUtils("Arial.ttf", 0, 18, 7, false);
        FontManager.comfortaa = new FontUtils("Arial.ttf", 0, 16, 7, false);
        FontManager.wqy18 = FontManager.roboto15;
        FontManager.wqy15 = FontManager.roboto15;
        FontManager.tahoma9 = new FontUtils("Arial.ttf", 0, 9, 7, false);
        FontManager.tahoma11 = new FontUtils("Arial.ttf", 0, 11, 7, false);
        FontManager.tahoma11bold = new FontUtils("Arial.ttf", 1, 11, 7, false);
        FontManager.tahoma13 = new FontUtils("Arial.ttf", 0, 13, 7, false);
        FontManager.tahoma13bold = new FontUtils("Arial.ttf", 1, 13, 7, false);
        FontManager.productsans16 = new FontUtils("Arial.ttf", 0, 16, 7, false);
        FontManager.ModernaSansLight = new FontUtils("ModernaSans-Light.ttf", 0, 16, 7, false);
    }
}
