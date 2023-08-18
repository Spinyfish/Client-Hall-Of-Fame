/* November.lol © 2023 */
package lol.november.feature.module.gui.panel.module;

import java.awt.*;

import lol.november.November;
import lol.november.feature.keybind.KeyBind;
import lol.november.feature.module.Module;
import lol.november.feature.setting.Setting;
import lol.november.utility.render.RenderUtils;
import lol.november.utility.render.animation.Animation;
import lol.november.utility.render.animation.Easing;
import lol.november.utility.render.component.Component;
import lol.november.utility.render.component.setting.BooleanComponent;
import lol.november.utility.render.component.setting.EnumComponent;
import lol.november.utility.render.component.setting.KeyBindComponent;
import lol.november.utility.render.component.setting.NumberComponent;
import lol.november.utility.render.font.Fonts;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class ModuleComponent extends Component {

  private static final double PADDING = 1.0;
  private static final float RADIUS = 3.0f;

  private static final Color BG = new Color(97, 106, 243);
  private static final Color FG = new Color(35, 35, 35);

  private final Module module;

  private final Animation hoverAnimation = new Animation(
    Easing.CUBIC_IN_OUT,
    250,
    false
  );
  private final Animation toggleAnimation = new Animation(
    Easing.CUBIC_IN_OUT,
    250,
    false
  );
  private final Animation openAnimation = new Animation(
    Easing.CUBIC_IN_OUT,
    225,
    false
  );

  public ModuleComponent(Module module) {
    this.module = module;

    for (Setting<?> setting : module.settings()) {
      if (setting.getValue() instanceof KeyBind) {
        getComponents().add(new KeyBindComponent((Setting<KeyBind>) setting));
      } else if (setting.getValue() instanceof Enum<?>) {
        getComponents().add(new EnumComponent((Setting<Enum<?>>) setting));
      } else if (setting.getValue() instanceof Boolean) {
        getComponents().add(new BooleanComponent((Setting<Boolean>) setting));
      } else if (setting.getValue() instanceof Number) {
        getComponents().add(new NumberComponent((Setting<Number>) setting));
      }
    }
  }

  @Override
  public void draw(int mouseX, int mouseY, float mouseButton) {
    boolean hovering = inBounds(mouseX, mouseY);
    if (hovering != hoverAnimation.getState()) hoverAnimation.setState(
      hovering
    );

    if (
      module.toggled() != toggleAnimation.getState()
    ) toggleAnimation.setState(module.toggled());

    RenderUtils.rect(
            getX(),
            getY(),
            getWidth(),
            getHeight() * 0.5f,
            FG.brighter().getRGB()
    );

    RenderUtils.roundRect(
            getX(),
            getY(),
            getWidth(),
            getHeight(),
            4.0f,
            FG.brighter().getRGB()
    );

    if (toggleAnimation.getFactor() != 0.0) {
      if (November.instance().modules().valuesInCategory(module.getCategory()).stream().toList().indexOf(module) != November.instance().modules().valuesInCategory(module.getCategory()).size() - 1) {
        RenderUtils.rect(
                getX(),
                getY(),
                getWidth() * toggleAnimation.getFactor(),
                getHeight(),
                BG.getRGB()
        );
      } else {
        RenderUtils.rect(
              getX(),
              getY(),
              getWidth() * toggleAnimation.getFactor(),
              getHeight() * 0.5f,
              BG.getRGB()
        );

        RenderUtils.roundRect(
                getX(),
                getY(),
                getWidth() * toggleAnimation.getFactor(),
                getHeight(),
                RADIUS,
                BG.getRGB()
        );
      }
    }

    Fonts.poppins.drawStringWithShadow(
      module.name(),
      getX() + (PADDING * 4),
      Fonts.poppins.alignV(getY(), super.getHeight()),
      -1
    );

    if (openAnimation.getFactor() <= 0.0) return;

    RenderUtils.roundRect(
      getX() + PADDING,
      getY() + super.getHeight() + PADDING,
      getWidth() - (PADDING * 2),
      getHeight() - super.getHeight() - (PADDING * 2),
      3.5f,
      FG.getRGB()
    );

    double y = getY() + super.getHeight() + PADDING;
    for (Component component : getComponents()) {
      if (!component.visible()) continue;

      component.setX(getX() + PADDING);
      component.setY(y);
      component.setHeight(14.0);
      component.setWidth(getWidth() - (PADDING * 2));

      component.draw(mouseX, mouseY, mouseButton);

      y += component.getHeight();
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (
      inBounds(mouseX, mouseY, getX(), getY(), getWidth(), super.getHeight())
    ) {
      if (mouseButton == 0) {
        module.setState(!module.toggled());
      } else if (mouseButton == 1 && !module.getSettingMap().isEmpty()) {
        openAnimation.setState(!openAnimation.getState());
      }
    }

    if (openAnimation.getFactor() <= 0.0) return;
    for (Component component : getComponents()) {
      if (!component.visible()) continue;
      component.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int state) {
    if (openAnimation.getFactor() <= 0.0) return;
    for (Component component : getComponents()) {
      if (!component.visible()) continue;
      component.mouseReleased(mouseX, mouseY, state);
    }
  }

  @Override
  public void keyTyped(char typedChar, int keyCode) {
    for (Component component : getComponents()) {
      component.keyTyped(typedChar, keyCode);
    }
  }

  @Override
  public double getHeight() {
    double baseHeight = PADDING * 4;
    for (Component component : getComponents()) {
      if (!component.visible()) continue;
      baseHeight += component.getHeight();
    }

    return super.getHeight() + (baseHeight * openAnimation.getFactor());
  }

  public Module getModule() {
    return module;
  }
}
