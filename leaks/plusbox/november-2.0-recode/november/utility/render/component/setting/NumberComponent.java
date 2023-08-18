/* November.lol © 2023 */
package lol.november.utility.render.component.setting;

import java.awt.*;
import lol.november.feature.setting.Setting;
import lol.november.utility.math.MathUtils;
import lol.november.utility.render.RenderUtils;
import lol.november.utility.render.component.Component;
import lol.november.utility.render.font.Fonts;
import org.lwjgl.input.Mouse;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class NumberComponent extends Component {

  private static final double PADDING = 1.0;
  private static final float RADIUS = 3.5f;

  private static final Color BG = new Color(97, 106, 243);

  private final Setting<Number> setting;
  private final double difference;
  private boolean dragging = false;

  public NumberComponent(Setting<Number> setting) {
    this.setting = setting;
    difference =
      setting.getMax().doubleValue() - setting.getMin().doubleValue();
  }

  @Override
  public void draw(int mouseX, int mouseY, float mouseButton) {
    if (dragging) {
      if (
        !Mouse.isButtonDown(0) ||
        !inBounds(
          mouseX,
          mouseY,
          getX() - PADDING,
          getY(),
          getWidth() + (PADDING * 2),
          getHeight()
        )
      ) {
        dragging = false;
      }

      if (dragging) {
        setValue(mouseX);
      }
    }

    double part =
      (setting.getValue().doubleValue() - setting.getMin().doubleValue()) /
      difference;
    double barWidth = setting.getValue().doubleValue() <
      setting.getMin().doubleValue()
      ? 0.0
      : getWidth() * part;

    RenderUtils.rect(getX(), getY(), barWidth, getHeight(), BG.getRGB());

    double alignedY = Fonts.poppins.alignV(getY(), super.getHeight());
    Fonts.poppins.drawStringWithShadow(
      setting.getName(),
      getX() + (PADDING * 3),
      alignedY,
      -1
    );
    String format = setting.getValue().toString();
    Fonts.poppins.drawStringWithShadow(
      format,
      (
        (getX() + getWidth()) -
        (Fonts.poppins.getStringWidth(format) + (PADDING * 3))
      ),
      alignedY,
      0xAAAAAA
    );
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (inBounds(mouseX, mouseY) && mouseButton == 0) {
      dragging = true;
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int state) {
    if (state == 0 && dragging) {
      dragging = false;
    }
  }

  @Override
  public void keyTyped(char typedChar, int keyCode) {}

  private void setValue(double mouseX) {
    double value =
      setting.getMin().doubleValue() +
      difference *
      (mouseX - getX()) /
      getWidth();
    double percision = 1.0 / setting.getScale().doubleValue();
    value = Math.round(value * percision) / percision;
    value = MathUtils.round(value, 2);

    if (value > setting.getMax().doubleValue()) {
      value = setting.getMax().doubleValue();
    }

    if (value < setting.getMin().doubleValue()) {
      value = setting.getMin().doubleValue();
    }

    if (setting.getValue() instanceof Integer) {
      setting.setValue((int) value);
    } else if (setting.getValue() instanceof Double) {
      setting.setValue(value);
    } else if (setting.getValue() instanceof Float) {
      setting.setValue((float) value);
    }
  }
}
