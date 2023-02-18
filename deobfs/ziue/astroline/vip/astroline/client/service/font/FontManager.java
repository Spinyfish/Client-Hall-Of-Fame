/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.font.FontUtils
 */
package vip.astroline.client.service.font;

import java.awt.Font;
import java.io.InputStream;
import vip.astroline.client.service.font.FontUtils;

public class FontManager {
    public static FontUtils normal = new FontUtils("Arial.ttf", 0, 16, 7, false);
    public static FontUtils normal2 = new FontUtils("ArialBold.ttf", 0, 16, 7, false);
    public static FontUtils big = new FontUtils("Arial.ttf", 0, 24, 7, false);
    public static FontUtils small = new FontUtils("Roboto.ttf", 0, 16, 7, false);
    public static FontUtils tiny = new FontUtils("Roboto.ttf", 0, 14, 7, false);
    public static FontUtils robotoL15 = new FontUtils("RobotoLight.ttf", 0, 15, 7, false);
    public static FontUtils robotoL18 = new FontUtils("RobotoLight.ttf", 0, 18, 7, false);
    public static FontUtils robotoL20 = new FontUtils("RobotoLight.ttf", 0, 20, 7, false);
    public static FontUtils robotoL40 = new FontUtils("RobotoLight.ttf", 0, 40, 7, false);
    public static FontUtils roboto12 = new FontUtils("Roboto.ttf", 0, 12, 7, false);
    public static FontUtils roboto15 = new FontUtils("Roboto.ttf", 0, 15, 7, false);
    public static FontUtils roboto16 = new FontUtils("Roboto.ttf", 0, 16, 7, false);
    public static FontUtils baloo16 = new FontUtils("Baloo.ttf", 0, 16, 7, false);
    public static FontUtils baloo17 = new FontUtils("SFBOLD.ttf", 1, 17, 7, false);
    public static FontUtils SFBOLD = new FontUtils("SFBOLD.ttf", 1, 30, 7, false);
    public static FontUtils vision16 = new FontUtils("Vision.otf", 1, 26, 7, false, 5);
    public static FontUtils vision30 = new FontUtils("Vision.otf", 1, 30, 7, false, 5);
    public static FontUtils icon10 = new FontUtils("Icon.ttf", 0, 10, 7, false);
    public static FontUtils icon14 = new FontUtils("Icon.ttf", 0, 14, 7, false);
    public static FontUtils icon15 = new FontUtils("Icon.ttf", 0, 15, 7, false);
    public static FontUtils icon16 = new FontUtils("Icon.ttf", 0, 16, 7, false);
    public static FontUtils icon18 = new FontUtils("Icon.ttf", 0, 18, 7, false);
    public static FontUtils icon20 = new FontUtils("Icon.ttf", 0, 20, 7, false);
    public static FontUtils icon25 = new FontUtils("Icon.ttf", 0, 25, 7, false);
    public static FontUtils icon30 = new FontUtils("Icon.ttf", 0, 30, 7, false);
    public static FontUtils icon40 = new FontUtils("Icon.ttf", 0, 40, 7, false);
    public static FontUtils icon35 = new FontUtils("Icon2.ttf", 0, 35, 7, false);
    public static FontUtils font1 = new FontUtils("Vision.otf", 0, 40, 7, false, 7);
    public static FontUtils font2 = new FontUtils("Comfortaa.ttf", 0, 15, 7, false);
    public static FontUtils font3 = new FontUtils("Comfortaa.ttf", 0, 10, 7, false);
    public static FontUtils font4 = new FontUtils("Icon.ttf", 0, 150, 7, false);
    public static FontUtils font5 = new FontUtils("Comfortaa.ttf", 0, 12, 7, false);
    public static FontUtils sans13 = new FontUtils("sans.ttf", 0, 13, 7, false);
    public static FontUtils sans14 = new FontUtils("sans.ttf", 0, 14, 7, false);
    public static FontUtils sans16 = new FontUtils("sans.ttf", 0, 16, 7, false);
    public static FontUtils sans18 = new FontUtils("sans.ttf", 0, 18, 7, false);
    public static FontUtils hudetc = new FontUtils("Arial.ttf", 0, 18, 7, false);
    public static FontUtils productsans = new FontUtils("astrofont.ttf", 0, 18, 7, false);
    public static FontUtils productsans2 = new FontUtils("astrofont.ttf", 0, 26, 7, false);
    public static FontUtils sans20 = new FontUtils("sans.ttf", 0, 20, 7, false);
    public static FontUtils sans24 = new FontUtils("sans.ttf", 0, 24, 7, false);
    public static FontUtils poppinsBold20 = new FontUtils("PoppinsSemiBold.ttf", 0, 20, 7, false);
    public static FontUtils poppins16 = new FontUtils("PoppinsRegular.ttf", 0, 16, 7, false);
    public static FontUtils poppins18 = new FontUtils("PoppinsRegular.ttf", 0, 18, 7, false);
    public static FontUtils comfortaa = new FontUtils("Comfortaa.ttf", 0, 16, 7, false);
    public static FontUtils wqy18 = roboto15;
    public static FontUtils wqy15 = roboto15;
    public static FontUtils tahoma9 = new FontUtils("tahoma.ttf", 0, 9, 7, false);
    public static FontUtils tahoma11 = new FontUtils("tahoma.ttf", 0, 11, 7, false);
    public static FontUtils tahoma11bold = new FontUtils("tahomabold.ttf", 1, 11, 7, false);
    public static FontUtils tahoma13 = new FontUtils("tahoma.ttf", 0, 13, 7, false);
    public static FontUtils tahoma13bold = new FontUtils("tahomabold.ttf", 1, 13, 7, false);
    public static FontUtils productsans16 = new FontUtils("astrofont.ttf", 0, 16, 7, false);

    public static Font getFont(String name, int size) {
        Font font;
        try {
            InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/astroline/font/" + name);
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            System.out.println("Error loading font " + name);
            font = new Font("Arial", 0, size);
        }
        return font;
    }
}
