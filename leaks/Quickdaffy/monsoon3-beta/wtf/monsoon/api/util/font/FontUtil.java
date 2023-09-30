/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.font.impl.FontRenderer;

public class FontUtil {
    public FontRenderer productSansSmall;
    public FontRenderer productSansSmaller;
    public FontRenderer productSans;
    public FontRenderer productSansMedium;
    public FontRenderer productSansBold;
    public FontRenderer productSansSmallBold;
    public FontRenderer greycliff40;
    public FontRenderer greycliff26;
    public FontRenderer greycliff19;
    public FontRenderer entypo14;
    public FontRenderer entypo18;
    public FontRenderer menuIcons;
    public FontRenderer minecraft;
    public FontRenderer comicSans;
    public FontRenderer comicSansSmall;
    public FontRenderer comicSansBold;
    public FontRenderer comicSansSmallBold;
    public FontRenderer comicSansMedium;
    public FontRenderer comicSansMediumBold;
    public FontRenderer ubuntuwu;
    public FontRenderer ubuntuwuSmall;
    public FontRenderer ubuntuwuMedium;

    public static Font getFont(String name, int size) {
        try {
            InputStream fontStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("monsoon/font/" + name)).getInputStream();
            Font font = Font.createFont(0, fontStream);
            fontStream.close();
            return font.deriveFont(0, size);
        }
        catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("plain", 0, size);
        }
    }

    public void bootstrap() {
        this.productSans = new FontRenderer(FontUtil.getFont("product_sans.ttf", 38));
        this.productSansBold = new FontRenderer(FontUtil.getFont("product_sans_bold.ttf", 38));
        this.productSansSmallBold = new FontRenderer(FontUtil.getFont("product_sans_bold.ttf", 30));
        this.productSansSmall = new FontRenderer(FontUtil.getFont("product_sans.ttf", 30));
        this.productSansSmaller = new FontRenderer(FontUtil.getFont("product_sans.ttf", 24));
        this.productSansMedium = new FontRenderer(FontUtil.getFont("product_sans.ttf", 34));
        this.greycliff40 = new FontRenderer(FontUtil.getFont("greycliff.ttf", 80));
        this.greycliff26 = new FontRenderer(FontUtil.getFont("greycliff.ttf", 52));
        this.greycliff19 = new FontRenderer(FontUtil.getFont("greycliff.ttf", 38));
        this.entypo14 = new FontRenderer(FontUtil.getFont("entypo.ttf", 28));
        this.entypo18 = new FontRenderer(FontUtil.getFont("entypo.ttf", 36));
        this.menuIcons = new FontRenderer(FontUtil.getFont("menu_icons.ttf", 180));
        this.minecraft = new FontRenderer(FontUtil.getFont("minecraft.otf", 38));
        this.comicSans = new FontRenderer(FontUtil.getFont("comic_sans.ttf", 38));
        this.comicSansMedium = new FontRenderer(FontUtil.getFont("comic_sans.ttf", 34));
        this.comicSansSmall = new FontRenderer(FontUtil.getFont("comic_sans.ttf", 30));
        this.comicSansBold = new FontRenderer(FontUtil.getFont("comic_sans_bold.ttf", 38));
        this.comicSansMediumBold = new FontRenderer(FontUtil.getFont("comic_sans_bold.ttf", 34));
        this.comicSansSmallBold = new FontRenderer(FontUtil.getFont("comic_sans_bold.ttf", 30));
        this.ubuntuwu = new FontRenderer(FontUtil.getFont("ubuntuwu.ttf", 48));
        this.ubuntuwuSmall = new FontRenderer(FontUtil.getFont("ubuntuwu.ttf", 40));
        this.ubuntuwuMedium = new FontRenderer(FontUtil.getFont("ubuntuwu.ttf", 44));
        Wrapper.setFont(this.productSans);
    }

    public static class UNICODES_UI {
        public static String TRASH = "a";
        public static String EYE = "b";
        public static String LOCK = "c";
        public static String UNLOCK = "d";
        public static String FILLED_HEART = "e";
        public static String EMPTY_HEART = "f";
        public static String PENCIL = "g";
        public static String INFO = "h";
        public static String MINUS = "i";
        public static String PLUS = "j";
        public static String YES = "k";
        public static String NO = "l";
        public static String SEARCH = "m";
        public static String LIGHT = "n";
        public static String HOME = "o";
        public static String SETTINGS = "p";
        public static String COPY = "q";
        public static String CIRCLE_FULL = "r";
        public static String CIRCLE_EMPTY = "s";
        public static String LEFT = "t";
        public static String RIGHT = "u";
        public static String UP = "v";
        public static String DOWN = "w";
        public static String CLOCK = "x";
        public static String TAG = "y";
        public static String BLOCK = "z";
        public static String USER = "A";
        public static String WARN = "B";
        public static String ERROR = "C";
        public static String LOAD = "D";
    }
}

