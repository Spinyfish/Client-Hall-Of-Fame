/* November.lol © 2023 */
package lol.november.utility.render.component.setting;

import java.awt.*;
import lol.november.feature.setting.Setting;
import lol.november.utility.render.ColorUtils;
import lol.november.utility.render.RenderUtils;
import lol.november.utility.render.animation.Animation;
import lol.november.utility.render.animation.Easing;
import lol.november.utility.render.component.Component;
import lol.november.utility.render.font.Fonts;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class BooleanComponent extends Component {

  private static final double PADDING = 1.0;
  private static final float RADIUS = 3.5f;

  private static final Color BG = new Color(97, 106, 243);
  private static final Color FG = new Color(50, 50, 50);

  private final Setting<Boolean> setting;

  private final Animation toggleAnimation = new Animation(
    Easing.CUBIC_IN_OUT,
    250,
    false
  );

  public BooleanComponent(Setting<Boolean> setting) {
    this.setting = setting;
  }

  @Override
  public void draw(int mouseX, int mouseY, float mouseButton) {
    if (
      setting.getValue() != toggleAnimation.getState()
    ) toggleAnimation.setState(setting.getValue());

    double alignedY = Fonts.poppins.alignV(getY(), getHeight());

    Fonts.poppins.drawStringWithShadow(
      setting.getName(),
      getX() + (PADDING * 3),
      alignedY,
      -1
    );

    double d = getHeight() - (PADDING * 4);
    RenderUtils.roundRect(
      getX() + getWidth() - d - (PADDING * 3),
      getY() + (PADDING * 2),
      d,
      d,
      RADIUS,
      FG.getRGB()
    );

    if (toggleAnimation.getFactor() > 0.0) {
      RenderUtils.roundRect(
        getX() + getWidth() - d - (PADDING * 2),
        getY() + (PADDING * 2),
        d,
        d,
        RADIUS,
        ColorUtils.alpha(
          BG.getRGB(),
          (int) (255.0 * toggleAnimation.getFactor())
        )
      );
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (inBounds(mouseX, mouseY) && mouseButton == 0) {
      setting.setValue(!setting.getValue());
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int state) {}

  @Override
  public void keyTyped(char typedChar, int keyCode) {}

  @Override
  public boolean visible() {
    return setting.visible();
  }
}
