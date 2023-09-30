package dev.tenacity.utils.font;

import dev.tenacity.utils.Utils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontUtil implements Utils {
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


    @Getter
    public enum FontType {
        LITHIUM("tenacity", "tenacity-bold", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        TAHOMA("tahoma", "tahoma-bold", 10, 12, 14, 16, 18, 27),
        RUBIK("rubik", "rubik-bold", 13, 18),
        NEVERLOSE("neverlose", 12, 18, 22),
        ICON("icon", 16, 20, 26, 35, 40);

        private final ResourceLocation location, boldLocation;
        private Font font, boldFont;
        private final int[] sizes;

        FontType(String fontName, String boldName, int... sizes) {
            this.location = new ResourceLocation("Tenacity/Fonts/" + fontName + ".ttf");
            this.boldLocation = new ResourceLocation("Tenacity/Fonts/" + boldName + ".ttf");
            this.sizes = sizes;
        }

        FontType(String fontName, int... sizes) {
            this.location = new ResourceLocation("Tenacity/Fonts/" + fontName + ".ttf");
            this.boldLocation = null;
            this.sizes = sizes;
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
