package com.alan.clients.util.font;

import com.alan.clients.util.font.impl.rise.FontRenderer;
import com.alan.clients.util.font.impl.rise.FontUtil;
import net.minecraft.client.Minecraft;

import java.util.HashMap;

public class FontManager {

    // FOR ANYONE WHO VISITS THIS CLASS: CREATE A HASHMAP FOR EACH FONT AND BASICALLY COPY THE GIVEN METHOD

    private static final HashMap<Integer, FontRenderer> INTERNATIONAL = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> MONTSERRAT_MAP = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> ROBOTO_MAP = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> LIGHT_MAP = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> NUNITO = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> NUNITO_BOLD = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> MUSEO_SANS = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> NUNITO_LIGHT_MAP = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> POPPINS_BOLD = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> POPPINS_SEMI_BOLD = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> POPPINS_MEDIUM = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> POPPINS_REGULAR = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> POPPINS_LIGHT = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> QUICKSAND_MAP_MEDIUM = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> QUICKSAND_MAP_LIGHT = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> TAHOMA_BOLD = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> TAHOMA = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> ICONS_1 = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> ICONS_2 = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> ICONS_3 = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> DREAMSCAPE = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> DREAMSCAPE_NO_AA = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> SOMATIC = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> BIKO = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> MONTSERRAT_HAIRLINE = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_BOLD = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_REGULAR = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_MEDIUM = new HashMap<>();
    private static final HashMap<Integer, FontRenderer> PRODUCT_SANS_LIGHT = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> SF_UI_PRO = new HashMap<>();

    private static final HashMap<Integer, FontRenderer> HACK = new HashMap<>();

    // COPY THIS METHOD FOR EACH METHOD AND REPLACE FONTNAME WITH THE USED FONT FILE NAME
    public static Font getMontserratMedium(final int size) {
        return get(MONTSERRAT_MAP, size, "Montserrat-Medium", true, true);
    }

    public static Font getMontserratHairline(final int size) {
        return get(MONTSERRAT_HAIRLINE, size, "Montserrat-Hairline", true, true);
    }

    public static Font getInternational(int size) {
        return get(INTERNATIONAL, size, "NotoSans-Regular", true, true, false, true);
    }

    public static Font getRobotoLight(final int size) {
        return get(ROBOTO_MAP, size, "Roboto-Light", true, true);
    }

    public static Font getLight(final int size) {
        return get(LIGHT_MAP, size, "Light", true, true);
    }

    public static Font getSFUIPro(final int size) {
        return get(SF_UI_PRO, size, "SF-UI-Pro", true, true);
    }

    public static Font getPoppinsBold(final int size) {
        return get(POPPINS_BOLD, size, "Poppins-Bold", true, true);
    }

    public static Font getPoppinsSemiBold(final int size) {
        return get(POPPINS_SEMI_BOLD, size, "Poppins-SemiBold", true, true);
    }

    public static Font getPoppinsMedium(final int size) {
        return get(POPPINS_MEDIUM, size, "Poppins-Medium", true, true);
    }

    public static Font getPoppinsRegular(final int size) {
        return get(POPPINS_REGULAR, size, "Poppins-Regular", true, true);
    }

    public static Font getPoppinsLight(final int size) {
        return get(POPPINS_LIGHT, size, "Poppins-Light", true, true);
    }

    public static Font getNunito(final int size) {
        return get(PRODUCT_SANS_REGULAR, size, "product_sans_regular", true, true);
    }

    public static Font getNunitoBold(final int size) {
        return get(PRODUCT_SANS_BOLD, size, "product_sans_bold", true, true);
    }

    public static Font getMuseo(final int size) {
        return get(MUSEO_SANS, size, "MuseoSans_900", true, true);
    }

    public static Font getNunitoLight(final int size) {
        return get(PRODUCT_SANS_LIGHT, size, "product_sans_light", true, true);
    }

    public static Font getQuicksandMedium(final int size) {
        return get(QUICKSAND_MAP_MEDIUM, size, "Quicksand-Medium", true, true);
    }

    public static Font getQuicksandLight(final int size) {
        return get(QUICKSAND_MAP_LIGHT, size, "Quicksand-Light", true, true);
    }

    public static Font getTahomaBold(final int size) {
        return get(TAHOMA_BOLD, size, "TahomaBold", true, true);
    }

    public static Font getTahoma(final int size) {
        return get(TAHOMA, size, "Tahoma", true, true);
    }

    public static Font getDreamscape(final int size) {
        return get(DREAMSCAPE, size, "Dreamscape", true, true);
    }

    public static Font getSomatic(final int size) {
        return get(SOMATIC, size, "Somatic-Rounded", true, true);
    }

    public static Font getDreamscapeNoAA(final int size) {
        return get(DREAMSCAPE_NO_AA, size, "Dreamscape", true, false);
    }

    public static Font getIconsOne(final int size) {
        return get(ICONS_1, size, "Icon-1", true, true);
    }

    public static Font getIconsThree(final int size) {
        return get(ICONS_3, size, "Icon-3", true, true);
    }

    public static Font getIconsTwo(final int size) {
        return get(ICONS_2, size, "Icon-2", true, true);
    }

    public static Font getBiko(final int size) {
        return get(BIKO, size, "Biko_Regular", true, true);
    }

    public static Font getProductSansBold(final int size) {
        return get(PRODUCT_SANS_BOLD, size, "product_sans_bold", true, true);
    }

    public static Font getProductSansRegular(final int size) {
        return get(PRODUCT_SANS_REGULAR, size, "product_sans_regular", true, true);
    }

    public static Font getProductSansMedium(final int size) {
        return get(PRODUCT_SANS_MEDIUM, size, "product_sans_medium", true, true);
    }

    public static Font getProductSansLight(final int size) {
        return get(PRODUCT_SANS_LIGHT, size, "product_sans_light", true, true);
    }

    public static Font getHack(final int size) {
        return get(HACK, size, "Hack-Regular", true, true);
    }

    public static Font getMinecraft() {
        return Minecraft.getMinecraft().fontRendererObj;
    }

    private static Font get(HashMap<Integer, FontRenderer> map, int size, String name, boolean fractionalMetrics, boolean AA) {
        return get(map, size, name, fractionalMetrics, AA, false, false);
    }

    private static Font get(HashMap<Integer, FontRenderer> map, int size, String name, boolean fractionalMetrics, boolean AA, boolean otf, boolean international) {
        if (!map.containsKey(size)) {
            final java.awt.Font font = FontUtil.getResource("rise/font/" + name + (otf ? ".otf" : ".ttf"), size);

            if (font != null) {
                map.put(size, new FontRenderer(font, fractionalMetrics, AA, international));
            }
        }

        return map.get(size);
    }
}