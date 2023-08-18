/* November.lol © 2023 */
package lol.november.feature.hud.impl;

import lol.november.feature.hud.HUDElement;
import lol.november.feature.hud.Register;
import lol.november.utility.render.ColorUtils;
import lol.november.utility.render.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(value = "Watermark", state = true)
public class WatermarkHUDElement extends HUDElement {
  private final Color COLOR_ONE = new Color(97, 106, 243);
  private final Color COLOR_TWO = new Color(0, 156, 170);

  @Override
  public void render(ScaledResolution resolution, float partialTicks) {
    // TODO: HUDElement should extend some sort of Component class

    if (Keyboard.isKeyDown(Keyboard.KEY_GRAVE)) {
      Fonts.init();
    }

    final String NAME = "November";
    float x = 0.0f;
    for (char c : NAME.toCharArray()) {
      Fonts.poppins.drawStringWithShadow(String.valueOf(c), 4.0f + x, 4.0f, ColorUtils.colorSwitch(COLOR_ONE, COLOR_TWO, 4.0f + (x * 0.1f), 6 / 100.0).getRGB());
      x += Fonts.poppins.getStringWidth(String.valueOf(c));
    }
  }
}
