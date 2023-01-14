package cafe.corrosion.font;

import cafe.corrosion.util.font.type.FontType;
import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontManager {
    private final Map<String, TTFFontRenderer> fonts = new HashMap();

    public TTFFontRenderer getFontRenderer(FontType fontType, float size) {
        String combined = fontType.getPathName() + ":" + size;
        if (!this.fonts.containsKey(combined)) {
            this.fonts.put(combined, this.createRenderer(fontType.getPathName(), size));
        }

        return (TTFFontRenderer)this.fonts.get(combined);
    }

    private TTFFontRenderer createRenderer(String path, float size) {
        try {
            return new TTFFontRenderer(Font.createFont(0, (InputStream)Objects.requireNonNull(this.getClass().getResourceAsStream("/assets/minecraft/corrosion/fonts/" + path + ".ttf"))).deriveFont(0, size), true);
        } catch (Throwable var4) {
            throw var4;
        }
    }
}
