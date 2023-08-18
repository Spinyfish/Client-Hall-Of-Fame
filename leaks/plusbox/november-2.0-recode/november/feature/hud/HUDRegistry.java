/* November.lol © 2023 */
package lol.november.feature.hud;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import lol.november.feature.registry.Registry;
import lol.november.utility.reflect.Reflections;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class HUDRegistry implements Registry<HUDElement> {

  /**
   * The package representation of where hud elements should be
   */
  private static final String HUD_ELEMENTS_PACKAGE =
    "lol.november.feature.hud.impl";

  /**
   * A {@link LinkedHashMap} of the hud element class & its instance
   */
  @Getter(AccessLevel.NONE)
  private final Map<Class<? extends HUDElement>, HUDElement> elementInstanceMap =
    new LinkedHashMap<>();

  /**
   * The {@link Minecraft} game instance
   */
  private final Minecraft mc = Minecraft.getMinecraft();

  @Override
  @SneakyThrows
  public void init() {
    log.info("Discovering hud elements classes");

    // reflect all module classes in the package
    Reflections
      .getAllClassesInPackage(HUD_ELEMENTS_PACKAGE)
      .forEach(clazz -> {
        if (!HUDElement.class.isAssignableFrom(clazz)) return;

        try {
          // create new instance & add to this registry
          add((HUDElement) clazz.getConstructors()[0].newInstance());
        } catch (
          InstantiationException
          | IllegalAccessException
          | InvocationTargetException e
        ) {
          log.error("Failed to create hud element instance of " + clazz);
          e.printStackTrace();
        }
      });

    log.info("Discovered {} hud elements", size());
  }

  @Override
  public void add(HUDElement... elements) {
    for (HUDElement element : elements) {
      elementInstanceMap.put(element.getClass(), element);
    }
  }

  @Override
  public void remove(HUDElement... elements) {
    // lol
  }

  @Override
  public int size() {
    return elementInstanceMap.size();
  }

  @Override
  public Collection<HUDElement> values() {
    return elementInstanceMap.values();
  }
}
