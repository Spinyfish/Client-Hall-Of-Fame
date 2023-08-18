/* November.lol © 2023 */
package lol.november.feature.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.*;
import java.util.function.Supplier;
import lol.november.config.json.JsonSerializable;
import lol.november.feature.trait.Feature;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @param <T> the setting type
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class Setting<T> implements Feature, JsonSerializable {

  private final String name;
  private T value;

  @Getter(AccessLevel.NONE)
  private Supplier<Boolean> visibility = () -> true;

  private T min, max, scale;

  /**
   * Creates a setting
   *
   * @param name  the name of the setting
   * @param value the default value of the setting
   */
  public Setting(String name, T value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Creates a setting
   *
   * @param visibility the setting visibility
   * @param name       the name of the setting
   * @param value      the default value of the setting
   */
  public Setting(Supplier<Boolean> visibility, String name, T value) {
    this.visibility = visibility;
    this.name = name;
    this.value = value;
  }

  /**
   * Creates a setting
   *
   * @param name  the name of the setting
   * @param value the default value of the setting
   * @param scale the amount to go up when changing this setting
   * @param min   the minimum number value
   * @param max   the maximum number value
   */
  public Setting(String name, T value, Number scale, Number min, Number max) {
    this.name = name;
    this.value = value;
    this.scale = (T) scale;
    this.min = (T) min;
    this.max = (T) max;
  }

  /**
   * Creates a setting
   *
   * @param visibility the setting visibility
   * @param name       the name of the setting
   * @param value      the default value of the setting
   * @param scale      the amount to go up when changing this setting
   * @param min        the minimum number value
   * @param max        the maximum number value
   */
  public Setting(
    Supplier<Boolean> visibility,
    String name,
    T value,
    Number scale,
    Number min,
    Number max
  ) {
    this.visibility = visibility;
    this.name = name;
    this.value = value;
    this.scale = (T) scale;
    this.min = (T) min;
    this.max = (T) max;
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * Checks if this {@link Setting} is visible
   *
   * @return if the setting is visible
   */
  public boolean visible() {
    return visibility.get();
  }

  public void nextEnum() {
    if (!(value instanceof Enum<?> e)) return;

    Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();

    int next = enumOrdinal() + 1;
    if (constants.length <= next) next = 0;

    setValue((T) constants[next]);
  }

  public void previousEnum() {
    if (!(value instanceof Enum<?> e)) return;

    Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();

    int next = enumOrdinal() - 1;
    if (next < 0) next = constants.length - 1;

    setValue((T) constants[next]);
  }

  public int enumOrdinal() {
    if (!(value instanceof Enum<?> e)) return -1;
    return e.ordinal();
  }

  @Override
  public void fromJson(JsonElement element) {
    if (!element.isJsonObject()) return;
    JsonObject object = element.getAsJsonObject();

    if (
      !object.has("name") || !object.get("name").getAsString().equals(name)
    ) return;

    if (!object.has("value")) return;

    JsonElement ele = object.get("value");
    if (value instanceof Enum<?> e) {
      value = (T) Enum.valueOf(e.getDeclaringClass(), ele.getAsString());
    } else if (value instanceof Color) {
      if (!ele.isJsonObject()) return;
      JsonObject colorObject = ele.getAsJsonObject();

      value =
        (T) new Color(
          colorObject.get("r").getAsFloat(),
          colorObject.get("g").getAsFloat(),
          colorObject.get("b").getAsFloat(),
          colorObject.get("a").getAsFloat()
        );
    } else if (value instanceof Number number) {
      if (number instanceof Double) {
        value = (T) (Double) ele.getAsDouble();
      } else if (number instanceof Float) {
        value = (T) (Float) ele.getAsFloat();
      } else if (number instanceof Integer) {
        value = (T) (Integer) ele.getAsInt();
      }
    } else if (value instanceof Boolean) {
      value = (T) (Boolean) ele.getAsBoolean();
    }
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();

    object.addProperty("name", name);

    if (value instanceof Enum<?> e) {
      object.addProperty("value", e.toString());
    } else if (value instanceof Color color) {
      JsonObject o = new JsonObject();
      o.addProperty("r", color.getRed());
      o.addProperty("g", color.getGreen());
      o.addProperty("b", color.getBlue());
      o.addProperty("a", color.getAlpha());

      object.add("value", o);
    } else if (value instanceof Number number) {
      if (number instanceof Double) {
        object.addProperty("value", number.doubleValue());
      } else if (number instanceof Float) {
        object.addProperty("value", number.floatValue());
      } else if (number instanceof Integer) {
        object.addProperty("value", number.intValue());
      }
    } else if (value instanceof Boolean bool) {
      object.addProperty("value", bool);
    }

    return object;
  }
}
