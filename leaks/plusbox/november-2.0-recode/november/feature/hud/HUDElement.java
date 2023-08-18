/* November.lol © 2023 */
package lol.november.feature.hud;

import com.google.gson.JsonElement;
import lol.november.config.json.JsonSerializable;
import lol.november.feature.trait.Feature;
import lol.november.feature.trait.Toggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Gavin
 * @since 2.0.0
 */
// TODO: component impl
public abstract class HUDElement implements Feature, Toggle, JsonSerializable {

  /**
   * The {@link Minecraft} game instance
   */
  protected static final Minecraft mc = Minecraft.getMinecraft();

  private final String name;
  private boolean state;

  public HUDElement() {
    if (!getClass().isAnnotationPresent(Register.class)) {
      throw new RuntimeException(
        "@Register is not present on the top of " + getClass()
      );
    }

    Register register = getClass().getDeclaredAnnotation(Register.class);

    name = register.value();
    state = register.state();
  }

  /**
   * Renders this {@link HUDElement}
   *
   * @param resolution   the {@link ScaledResolution} object
   * @param partialTicks the render partial ticks
   */
  public abstract void render(ScaledResolution resolution, float partialTicks);

  @Override
  public void enable() {}

  @Override
  public void disable() {}

  @Override
  public boolean toggled() {
    return state;
  }

  @Override
  public void setState(boolean state) {
    this.state = state;
    if (state) {
      enable();
    } else {
      disable();
    }
  }

  @Override
  public void fromJson(JsonElement element) {}

  @Override
  public JsonElement toJson() {
    return null;
  }

  @Override
  public String name() {
    return name;
  }
}
