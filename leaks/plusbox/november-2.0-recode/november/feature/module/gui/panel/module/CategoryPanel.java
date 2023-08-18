/* November.lol © 2023 */
package lol.november.feature.module.gui.panel.module;

import java.awt.*;
import java.util.List;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.utility.render.RenderUtils;
import lol.november.utility.render.animation.Animation;
import lol.november.utility.render.animation.Easing;
import lol.november.utility.render.component.Component;
import lol.november.utility.render.component.Draggable;
import lol.november.utility.render.font.Fonts;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class CategoryPanel extends Draggable {

  private static final double PADDING = 1.0;
  private static final float RADIUS = 4.5f;

  private static final Color BG = new Color(97, 106, 243);
  private static final Color FG = new Color(35, 35, 35);

  private final Category category;

  private final Animation openAnimation = new Animation(
    Easing.CUBIC_IN_OUT,
    250,
    false
  );

  private boolean init;

  public CategoryPanel(Category category, List<Module> modules) {
    this.category = category;

    for (Module module : modules) {
      getComponents().add(new ModuleComponent(module));
    }
  }

  public void init() {
    if (init) return;
    init = true;

    toggle();
  }

  @Override
  public void draw(int mouseX, int mouseY, float mouseButton) {
    super.draw(mouseX, mouseY, mouseButton);

    RenderUtils.roundRect(
      getX(),
      getY(),
      getWidth(),
      getHeight(),
      RADIUS,
      BG.getRGB()
    );
    RenderUtils.roundRect(
      getX() + PADDING,
      getY() + PADDING,
      getWidth() - (PADDING * 2),
      getHeight() - (PADDING * 2),
      RADIUS - 0.5f,
      FG.getRGB()
    );

    Fonts.poppins.drawStringWithShadow(EnumChatFormatting.BOLD +
                    category.getDisplay(),
      getX() +
      (getWidth() / 2.0) -
      (Fonts.poppins.getStringWidth(EnumChatFormatting.BOLD + category.getDisplay()) / 2.0),
      Fonts.poppins.alignV(getY(), super.getHeight()),
      -1
    );

    RenderUtils.roundRect(
      getX() + PADDING,
      getY() + super.getHeight() + PADDING,
      getWidth() - (PADDING * 2),
      getHeight() - super.getHeight() - (PADDING * 2),
      RADIUS - 0.5f,
      FG.brighter().getRGB()
    );

    RenderUtils.scissor(getX(), getY(), getWidth(), getHeight());

    if (!toggled()) return;

    double y = getY() + super.getHeight();
    for (int i = 0; i < getComponents().size(); ++i) {
      Component component = getComponents().get(i);
      if (!(component instanceof ModuleComponent child)) continue;

      child.setX(getX() + PADDING);
      child.setY(y);
      child.setWidth(getWidth() - (PADDING * 2));
      child.setHeight(14.5);

      child.draw(mouseX, mouseY, mouseButton);
      y += child.getHeight();
    }

    RenderUtils.endScissor();
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);

    if (inBounds(mouseX, mouseY) && mouseButton == 1) {
      toggle();
      return;
    }

    if (!toggled()) return;
    for (Component child : getComponents()) {
      if (!child.visible()) continue;
      child.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int state) {
    super.mouseReleased(mouseX, mouseY, state);

    if (!toggled()) return;
    for (Component child : getComponents()) {
      if (!child.visible()) continue;
      child.mouseReleased(mouseX, mouseY, state);
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
    double baseHeight = super.getHeight();
    if (!toggled()) return baseHeight;

    double height = PADDING * 2;

    for (Component component : getComponents()) {
      height += component.getHeight();
    }

    return baseHeight + (height * openAnimation.getFactor()) - 1;
  }

  private void toggle() {
    openAnimation.setState(!openAnimation.getState());
  }

  private boolean toggled() {
    return openAnimation.getState();
  }

  public boolean isInit() {
    return init;
  }
}
