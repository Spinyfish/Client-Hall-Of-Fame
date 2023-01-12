package dev.eternal.client.font;

import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import lombok.SneakyThrows;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontManager {

  private static final Map<String, TrueTypeFontRenderer> fonts = new HashMap<>();

  public static TrueTypeFontRenderer getFontRenderer(FontType fontType, float size) {
    String combined = fontType.pathName() + ":" + size;
    if (!fonts.containsKey(combined)) {
      fonts.put(combined, createRenderer(fontType.pathName(), size));
    }
    return fonts.get(combined);
  }

  @SneakyThrows
  private static TrueTypeFontRenderer createRenderer(String path, float size) {
    return new TrueTypeFontRenderer(Font.createFont(Font.TRUETYPE_FONT,
        Objects.requireNonNull(FontManager.class.getResourceAsStream("/assets/minecraft/eternal/font/" + path + ".ttf"))).deriveFont(Font.PLAIN, size), true);
  }
}
