/* November.lol © 2023 */
package lol.november.utility.render.component.setting;

import lol.november.feature.keybind.KeyBind;
import lol.november.feature.setting.Setting;
import lol.november.utility.render.animation.Animation;
import lol.november.utility.render.animation.Easing;
import lol.november.utility.render.component.Component;
import lol.november.utility.render.font.Fonts;
import lol.november.utility.string.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class KeyBindComponent extends Component {

  private static final double PADDING = 1.0;
  private static final float RADIUS = 3.5f;

  private static final Color BG = new Color(97, 106, 243);
  private static final Color FG = new Color(50, 50, 50);

  private final Setting<KeyBind> setting;
  private boolean listening;

  private final Animation toggleAnimation = new Animation(
    Easing.CUBIC_IN_OUT,
    250,
    false
  );

  public KeyBindComponent(Setting<KeyBind> setting) {
    this.setting = setting;
  }

  @Override
  public void draw(int mouseX, int mouseY, float mouseButton) {
    double alignedY = Fonts.poppins.alignV(getY(), getHeight());

    Fonts.poppins.drawStringWithShadow(
            setting.getName(),
            getX() + (PADDING * 3),
            alignedY,
            -1
    );

    String key = setting.getValue().getKey() == -1 ? "None" : Keyboard.getKeyName(setting.getValue().getKey());
    String value = listening ? "Listening..." : key.contains("KEY_") ? StringUtils.formatCase(key.replace("KEY_", "")) : key;
    Fonts.poppins.drawStringWithShadow(
            value,
            getX() +
                    getWidth() -
                    Fonts.poppins.getStringWidth(value) -
                    (PADDING * 3),
            alignedY,
            0xAAAAAA
    );
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (inBounds(mouseX, mouseY) && mouseButton == 0) {
      listening = !listening;
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int state) {}

  @Override
  public void keyTyped(char typedChar, int keyCode) {
    if (listening) {
      setting.getValue().setKey(keyCode);
      listening = false;
    }
  }

  @Override
  public boolean visible() {
    return setting.visible();
  }
}
