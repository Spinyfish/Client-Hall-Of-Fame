/* November.lol © 2023 */
package lol.november.utility.render.font;

import static java.awt.Font.TRUETYPE_FONT;

import java.awt.*;
import java.io.InputStream;
import lombok.SneakyThrows;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class Fonts {

  public static AWTFontRenderer poppins;

  public static void init() {
    poppins = new AWTFontRenderer(createFont("Poppins-Regular", 18));
  }

  @SneakyThrows
  private static Font createFont(String fileName, int size) {
    InputStream is =
      Fonts.class.getResourceAsStream(
          "/assets/november/font/" + fileName + ".ttf"
        );
    if (is == null) return null;

    Font font = Font.createFont(TRUETYPE_FONT, is);
    font = font.deriveFont((float) size);

    GraphicsEnvironment graphicsEnvironment =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    graphicsEnvironment.registerFont(font);

    is.close();
    return font;
  }

  private Fonts() {}
}
