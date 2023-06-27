package rip.athena.client.utils.font;

import java.util.*;
import rip.athena.client.utils.font.impl.athena.*;

public class FontManager
{
    private static final HashMap<Integer, FontRenderer> INTERNATIONAL;
    private static final HashMap<Integer, FontRenderer> MONTSERRAT_MAP;
    private static final HashMap<Integer, FontRenderer> ROBOTO_MAP;
    private static final HashMap<Integer, FontRenderer> LIGHT_MAP;
    private static final HashMap<Integer, FontRenderer> NUNITO;
    private static final HashMap<Integer, FontRenderer> NUNITO_BOLD;
    private static final HashMap<Integer, FontRenderer> MUSEO_SANS;
    private static final HashMap<Integer, FontRenderer> NUNITO_LIGHT_MAP;
    private static final HashMap<Integer, FontRenderer> POPPINS_BOLD;
    private static final HashMap<Integer, FontRenderer> POPPINS_SEMI_BOLD;
    private static final HashMap<Integer, FontRenderer> POPPINS_MEDIUM;
    private static final HashMap<Integer, FontRenderer> POPPINS_REGULAR;
    private static final HashMap<Integer, FontRenderer> POPPINS_LIGHT;
    private static final HashMap<Integer, FontRenderer> QUICKSAND_MAP_MEDIUM;
    private static final HashMap<Integer, FontRenderer> QUICKSAND_MAP_LIGHT;
    private static final HashMap<Integer, FontRenderer> TAHOMA_BOLD;
    private static final HashMap<Integer, FontRenderer> TAHOMA;
    private static final HashMap<Integer, FontRenderer> ICONS_1;
    private static final HashMap<Integer, FontRenderer> ICONS_2;
    private static final HashMap<Integer, FontRenderer> ICONS_3;
    private static final HashMap<Integer, FontRenderer> DREAMSCAPE;
    private static final HashMap<Integer, FontRenderer> DREAMSCAPE_NO_AA;
    private static final HashMap<Integer, FontRenderer> SOMATIC;
    private static final HashMap<Integer, FontRenderer> BIKO;
    private static final HashMap<Integer, FontRenderer> MONTSERRAT_HAIRLINE;
    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_BOLD;
    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_REGULAR;
    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_MEDIUM;
    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_LIGHT;
    private static final HashMap<Integer, FontRenderer> SF_UI_PRO;
    private static final HashMap<Integer, FontRenderer> HACK;
    
    public static Font getMontserratMedium(final int size) {
        return get(FontManager.MONTSERRAT_MAP, size, "Montserrat-Medium", true, true);
    }
    
    public static Font getMontserratHairline(final int size) {
        return get(FontManager.MONTSERRAT_HAIRLINE, size, "Montserrat-Hairline", true, true);
    }
    
    public static Font getInternational(final int size) {
        return get(FontManager.INTERNATIONAL, size, "NotoSans-Regular", true, true, false, true);
    }
    
    public static Font getRobotoLight(final int size) {
        return get(FontManager.ROBOTO_MAP, size, "Roboto-Light", true, true);
    }
    
    public static Font getLight(final int size) {
        return get(FontManager.LIGHT_MAP, size, "Light", true, true);
    }
    
    public static Font getSFUIPro(final int size) {
        return get(FontManager.SF_UI_PRO, size, "SF-UI-Pro", true, true);
    }
    
    public static Font getPoppinsBold(final int size) {
        return get(FontManager.POPPINS_BOLD, size, "Poppins-Bold", true, true);
    }
    
    public static Font getPoppinsSemiBold(final int size) {
        return get(FontManager.POPPINS_SEMI_BOLD, size, "Poppins-SemiBold", true, true);
    }
    
    public static Font getPoppinsMedium(final int size) {
        return get(FontManager.POPPINS_MEDIUM, size, "Poppins-Medium", true, true);
    }
    
    public static Font getPoppinsRegular(final int size) {
        return get(FontManager.POPPINS_REGULAR, size, "Poppins-Regular", true, true);
    }
    
    public static Font getPoppinsLight(final int size) {
        return get(FontManager.POPPINS_LIGHT, size, "Poppins-Light", true, true);
    }
    
    public static Font getNunito(final int size) {
        return get(FontManager.PRODUCT_SANS_REGULAR, size, "product_sans_regular", true, true);
    }
    
    public static Font getNunitoBold(final int size) {
        return get(FontManager.PRODUCT_SANS_BOLD, size, "product_sans_bold", true, true);
    }
    
    public static Font getMuseo(final int size) {
        return get(FontManager.MUSEO_SANS, size, "MuseoSans_900", true, true);
    }
    
    public static Font getNunitoLight(final int size) {
        return get(FontManager.PRODUCT_SANS_LIGHT, size, "product_sans_light", true, true);
    }
    
    public static Font getQuicksandMedium(final int size) {
        return get(FontManager.QUICKSAND_MAP_MEDIUM, size, "Quicksand-Medium", true, true);
    }
    
    public static Font getQuicksandLight(final int size) {
        return get(FontManager.QUICKSAND_MAP_LIGHT, size, "Quicksand-Light", true, true);
    }
    
    public static Font getTahomaBold(final int size) {
        return get(FontManager.TAHOMA_BOLD, size, "TahomaBold", true, true);
    }
    
    public static Font getTahoma(final int size) {
        return get(FontManager.TAHOMA, size, "Tahoma", true, true);
    }
    
    public static Font getDreamscape(final int size) {
        return get(FontManager.DREAMSCAPE, size, "Dreamscape", true, true);
    }
    
    public static Font getSomatic(final int size) {
        return get(FontManager.SOMATIC, size, "Somatic-Rounded", true, true);
    }
    
    public static Font getDreamscapeNoAA(final int size) {
        return get(FontManager.DREAMSCAPE_NO_AA, size, "Dreamscape", true, false);
    }
    
    public static Font getIconsOne(final int size) {
        return get(FontManager.ICONS_1, size, "Icon-1", true, true);
    }
    
    public static Font getIconsThree(final int size) {
        return get(FontManager.ICONS_3, size, "Icon-3", true, true);
    }
    
    public static Font getIconsTwo(final int size) {
        return get(FontManager.ICONS_2, size, "Icon-2", true, true);
    }
    
    public static Font getBiko(final int size) {
        return get(FontManager.BIKO, size, "Biko_Regular", true, true);
    }
    
    public static Font getProductSansBold(final int size) {
        return get(FontManager.PRODUCT_SANS_BOLD, size, "product_sans_bold", true, true);
    }
    
    public static Font getProductSansRegular(final int size) {
        return get(FontManager.PRODUCT_SANS_REGULAR, size, "product_sans_regular", true, true);
    }
    
    public static Font getProductSansMedium(final int size) {
        return get(FontManager.PRODUCT_SANS_MEDIUM, size, "product_sans_medium", true, true);
    }
    
    public static Font getProductSansLight(final int size) {
        return get(FontManager.PRODUCT_SANS_LIGHT, size, "product_sans_light", true, true);
    }
    
    public static Font getHack(final int size) {
        return get(FontManager.HACK, size, "Hack-Regular", true, true);
    }
    
    private static Font get(final HashMap<Integer, FontRenderer> map, final int size, final String name, final boolean fractionalMetrics, final boolean AA) {
        return get(map, size, name, fractionalMetrics, AA, false, false);
    }
    
    private static Font get(final HashMap<Integer, FontRenderer> map, final int size, final String name, final boolean fractionalMetrics, final boolean AA, final boolean otf, final boolean international) {
        if (!map.containsKey(size)) {
            final java.awt.Font font = FontUtil.getResource("Athena/gui/font/" + name + (otf ? ".otf" : ".ttf"), size);
            if (font != null) {
                map.put(size, new FontRenderer(font, fractionalMetrics, AA, international));
            }
        }
        return map.get(size);
    }
    
    static {
        INTERNATIONAL = new HashMap<Integer, FontRenderer>();
        MONTSERRAT_MAP = new HashMap<Integer, FontRenderer>();
        ROBOTO_MAP = new HashMap<Integer, FontRenderer>();
        LIGHT_MAP = new HashMap<Integer, FontRenderer>();
        NUNITO = new HashMap<Integer, FontRenderer>();
        NUNITO_BOLD = new HashMap<Integer, FontRenderer>();
        MUSEO_SANS = new HashMap<Integer, FontRenderer>();
        NUNITO_LIGHT_MAP = new HashMap<Integer, FontRenderer>();
        POPPINS_BOLD = new HashMap<Integer, FontRenderer>();
        POPPINS_SEMI_BOLD = new HashMap<Integer, FontRenderer>();
        POPPINS_MEDIUM = new HashMap<Integer, FontRenderer>();
        POPPINS_REGULAR = new HashMap<Integer, FontRenderer>();
        POPPINS_LIGHT = new HashMap<Integer, FontRenderer>();
        QUICKSAND_MAP_MEDIUM = new HashMap<Integer, FontRenderer>();
        QUICKSAND_MAP_LIGHT = new HashMap<Integer, FontRenderer>();
        TAHOMA_BOLD = new HashMap<Integer, FontRenderer>();
        TAHOMA = new HashMap<Integer, FontRenderer>();
        ICONS_1 = new HashMap<Integer, FontRenderer>();
        ICONS_2 = new HashMap<Integer, FontRenderer>();
        ICONS_3 = new HashMap<Integer, FontRenderer>();
        DREAMSCAPE = new HashMap<Integer, FontRenderer>();
        DREAMSCAPE_NO_AA = new HashMap<Integer, FontRenderer>();
        SOMATIC = new HashMap<Integer, FontRenderer>();
        BIKO = new HashMap<Integer, FontRenderer>();
        MONTSERRAT_HAIRLINE = new HashMap<Integer, FontRenderer>();
        PRODUCT_SANS_BOLD = new HashMap<Integer, FontRenderer>();
        PRODUCT_SANS_REGULAR = new HashMap<Integer, FontRenderer>();
        PRODUCT_SANS_MEDIUM = new HashMap<Integer, FontRenderer>();
        PRODUCT_SANS_LIGHT = new HashMap<Integer, FontRenderer>();
        SF_UI_PRO = new HashMap<Integer, FontRenderer>();
        HACK = new HashMap<Integer, FontRenderer>();
    }
}
