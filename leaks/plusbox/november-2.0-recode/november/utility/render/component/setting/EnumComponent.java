/* November.lol © 2023 */
package lol.november.utility.render.component.setting;

import java.util.StringJoiner;
import lol.november.feature.setting.Setting;
import lol.november.utility.render.component.Component;
import lol.november.utility.render.font.Fonts;
import lol.november.utility.string.StringUtils;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class EnumComponent extends Component {

  private final Setting<Enum<?>> setting;

  public EnumComponent(Setting<Enum<?>> setting) {
    this.setting = setting;
  }

  @Override
  public void draw(int mouseX, int mouseY, float mouseButton) {
    double PADDING = 1.0;

    double alignedY = Fonts.poppins.alignV(getY(), getHeight());

    Fonts.poppins.drawStringWithShadow(
      setting.getName(),
      getX() + (PADDING * 3),
      alignedY,
      -1
    );

    String format = StringUtils.formatCase(setting.getValue().name());
    Fonts.poppins.drawStringWithShadow(
      format,
      getX() +
      getWidth() -
      Fonts.poppins.getStringWidth(format) -
      (PADDING * 3),
      alignedY,
      0xAAAAAA
    );
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (inBounds(mouseX, mouseY)) {
      if (mouseButton == 0) {
        setting.nextEnum();
      } else if (mouseButton == 1) {
        setting.previousEnum();
      }
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
