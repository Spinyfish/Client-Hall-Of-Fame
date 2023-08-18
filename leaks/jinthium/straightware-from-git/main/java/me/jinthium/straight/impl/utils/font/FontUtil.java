package me.jinthium.straight.impl.utils.font;

import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontUtil implements MinecraftInstance {
    //These are for the icon font for ease of access
    public final static String
            BUG = "a",
            LIST = "b",
            BOMB = "c",
            EYE = "d",
            PERSON = "e",
            WHEELCHAIR = "f",
            SCRIPT = "g",
            SKIP_LEFT = "h",
            PAUSE = "i",
            PLAY = "j",
            SKIP_RIGHT = "k",
            SHUFFLE = "l",
            INFO = "m",
            SETTINGS = "n",
            CHECKMARK = "o",
            XMARK = "p",
            TRASH = "q",
            WARNING = "r",
            FOLDER = "s",
            LOAD = "t",
            SAVE = "u",
            UPVOTE_OUTLINE = "v",
            UPVOTE = "w",
            DOWNVOTE_OUTLINE = "x",
            DOWNVOTE = "y",
            DROPDOWN_ARROW = "z",
            PIN = "s",
            EDIT = "A",
            SEARCH = "B",
            UPLOAD = "C",
            REFRESH = "D",
            ADD_FILE = "E",
            STAR_OUTLINE = "F",
            STAR = "G";


    private static final HashMap<FontType, Map<Integer, CustomFont>> customFontMap = new HashMap<>();

    public static void setupFonts() {
        for (FontType type : FontType.values()) {
            type.setup();
            HashMap<Integer, CustomFont> fontSizes = new HashMap<>();

            if (type.hasBold()) {
                for (int size : type.getSizes()) {
                    CustomFont font = new CustomFont(type.fromSize(size));
                    font.setBoldFont(new CustomFont(type.fromBoldSize(size)));

                    fontSizes.put(size, font);
                }
            } else {
                for (int size : type.getSizes()) {
                    fontSizes.put(size, new CustomFont(type.fromSize(size)));
                }
            }

            customFontMap.put(type, fontSizes);
        }
    }



    public enum FontType {
        NORMAL("circular-std-medium-500",12, 14, 16, 17, 18, 19, 20, 22, 24, 26, 28, 32, 40, 80),
        ICON_FONT("fontello", 18, 20, 64);

        private final ResourceLocation location, boldLocation;
        private Font font, boldFont;
        private final int[] sizes;

        FontType(String fontName, String boldName, int... sizes) {
            this.location = new ResourceLocation("straight/fonts/" + fontName + ".ttf");
            this.boldLocation = new ResourceLocation("straight/fonts/" + boldName + ".ttf");
            this.sizes = sizes;
        }

        FontType(String fontName, int... sizes) {
            this.location = new ResourceLocation("straight/fonts/" + fontName + ".ttf");
            this.boldLocation = null;
            this.sizes = sizes;
        }

        public int[] getSizes() {
            return sizes;
        }

        public boolean hasBold() {
            return boldLocation != null;
        }

        public Font fromSize(int size) {
            return font.deriveFont(Font.PLAIN, size);
        }

        private Font fromBoldSize(int size) {
            return boldFont.deriveFont(Font.PLAIN, size);
        }

        public void setup() {
            font = getFontData(location);
            if (boldLocation != null) {
                boldFont = getFontData(boldLocation);
            }
        }

        public CustomFont size(int size) {
            return customFontMap.get(this).computeIfAbsent(size, k -> null);
        }

        public CustomFont boldSize(int size) {
            return customFontMap.get(this).get(size).getBoldFont();
        }
    }

    private static Font getFontData(ResourceLocation location) {
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font");
            return new Font("default", Font.PLAIN, +10);
        }
    }

}