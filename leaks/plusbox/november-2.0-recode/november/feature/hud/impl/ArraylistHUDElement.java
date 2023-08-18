/* November.lol © 2023 */
package lol.november.feature.hud.impl;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import lol.november.November;
import lol.november.feature.hud.HUDElement;
import lol.november.feature.hud.Register;
import lol.november.feature.module.Module;
import lol.november.utility.render.ColorUtils;
import lol.november.utility.render.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 4.0.0
 */
@Register(value = "ArrayList", state = true)
public class ArraylistHUDElement extends HUDElement {

  private static final double PADDING = 4.0;

  private final Color colorOne = new Color(97, 106, 243);
  private final Color colorTwo = new Color(0, 156, 170);

  @Override
  public void render(ScaledResolution resolution, float partialTicks) {
    List<Module> toggled = November
      .instance()
      .modules()
      .values()
      .stream()
      .filter(module ->
        (module.toggled() || module.getAnimation().getFactor() > 0.0) &&
        module.isShown()
      )
      .sorted(
        Comparator.comparingInt(module ->
          -Fonts.poppins.getStringWidth(module.name())
        )
      )
      .toList();
    if (toggled.isEmpty()) return;

    double y = 4.0;
    for (int i = 0; i < toggled.size(); ++i) {
      Module module = toggled.get(i);

      double factor = module.getAnimation().getFactor();
      String name = module.name();

      glPushMatrix();

      double x = resolution.getScaledWidth_double() -
              (Fonts.poppins.getStringWidth(name) + PADDING);

      glTranslated(
              (x + Fonts.poppins.getStringWidth(name) / 2.0) * (1 - factor),
              (y + Fonts.poppins.FONT_HEIGHT / 2.0) * (1 - factor),
              0.0
      );

      glScaled(factor, factor, 0);

      Fonts.poppins.drawStringWithShadow(
        name,
        (float) (
          resolution.getScaledWidth_double() -
          (Fonts.poppins.getStringWidth(name) + PADDING)
        ),
        (float) y,
        ColorUtils
          .colorSwitch(colorOne, colorTwo, (i * 100), 6 / 100.0)
          .getRGB()
      );

      glPopMatrix();

      y += (Fonts.poppins.FONT_HEIGHT + PADDING) * factor;
    }
  }
}
