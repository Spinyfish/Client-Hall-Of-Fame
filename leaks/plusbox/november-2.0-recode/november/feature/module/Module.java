/* November.lol © 2023 */
package lol.november.feature.module;

import static lol.november.feature.keybind.KeyBind.INVALID_KEY;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import lol.november.November;
import lol.november.config.json.JsonSerializable;
import lol.november.feature.keybind.KeyBind;
import lol.november.feature.keybind.KeyBindType;
import lol.november.feature.setting.Setting;
import lol.november.feature.setting.SettingContainer;
import lol.november.feature.trait.Feature;
import lol.november.feature.trait.Toggle;
import lol.november.scripting.wrapper.Scripting;
import lol.november.utility.render.animation.Animation;
import lol.november.utility.render.animation.Easing;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;

/**
 * @author Gavin
 * @since 1.0.0
 */
@Getter
public class Module
  implements Feature, Toggle, SettingContainer, JsonSerializable {

  /**
   * The {@link Minecraft} game instance
   */
  protected static final Minecraft mc = Minecraft.getMinecraft();

  @Getter(AccessLevel.NONE)
  private final String name;

  private final String description;
  private final Category category;

  private boolean shown = true;

  private final Map<String, Setting<?>> settingMap = new LinkedHashMap<>();
  private final Setting<KeyBind> keyBind;

  private final Animation animation = new Animation(
    Easing.CUBIC_IN_OUT,
    250,
    false
  );

  public Module() {
    // create keybind object
    keyBind =
      new Setting<>("Key",
              new KeyBind(
                      KeyBindType.KEYBOARD,
                      INVALID_KEY,
                      bind -> {
                        if (bind.toggled()) {
                          enable();
                        } else {
                          disable();
                        }
                      }
              )
      );

    if (getClass().isAnnotationPresent(Scripting.class)) {
      name = null;
      description = null;
      category = null;
      return;
    }

    if (!getClass().isAnnotationPresent(Register.class)) {
      throw new RuntimeException(
        "@Register is not present on the top of " + getClass()
      );
    }

    Register register = getClass().getDeclaredAnnotation(Register.class);

    name = register.name();
    description = register.description();
    category = register.category();
  }

  /**
   * Loads this {@link Module}
   */
  @Override
  @SneakyThrows
  public void init() {
    November.instance().keybinds().register(name(), keyBind.getValue());
    settingMap.put(keyBind.getName(), keyBind);

    for (Field field : getClass().getDeclaredFields()) {
      if (!Setting.class.isAssignableFrom(field.getType())) continue;

      field.setAccessible(true);

      Setting<?> setting = (Setting<?>) field.get(this);
      settingMap.put(setting.getName(), setting);
    }
  }

  @Override
  public <T> Setting<T> get(String name) {
    return (Setting<T>) settingMap.getOrDefault(name, null);
  }

  @Override
  public Collection<Setting<?>> settings() {
    return settingMap.values();
  }

  @Override
  public void enable() {
    November.bus().subscribe(this);
    animation.setState(true);
  }

  @Override
  public void disable() {
    November.bus().unsubscribe(this);
    animation.setState(false);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public void setState(boolean state) {
    keyBind.getValue().setState(state);
  }

  @Override
  public boolean toggled() {
    return keyBind.getValue().toggled();
  }

  @Override
  public void fromJson(JsonElement element) {
    if (!element.isJsonObject()) return;
    JsonObject object = element.getAsJsonObject();

    shown = object.get("shown").getAsBoolean();

    if (!object.has("settings")) return;
    JsonElement ele = object.get("settings");
    if (!ele.isJsonObject()) return;

    JsonArray settingArray = ele.getAsJsonArray();
    for (JsonElement e : settingArray) {
      if (!e.isJsonObject()) continue;
      JsonObject o = e.getAsJsonObject();
      if (o.has("name")) {
        Setting<?> setting = settingMap.get(o.get("name").getAsString());
        if (setting != null) setting.fromJson(e);
      }
    }
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();

    object.addProperty("shown", shown);

    JsonArray array = new JsonArray();
    for (Setting<?> setting : settingMap.values()) {
      array.add(setting.toJson());
    }
    object.add("settings", array);

    return object;
  }
}
