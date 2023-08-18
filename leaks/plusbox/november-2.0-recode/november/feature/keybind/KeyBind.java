/* November.lol © 2023 */
package lol.november.feature.keybind;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import lol.november.config.json.JsonSerializable;
import lol.november.feature.trait.Toggle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class KeyBind implements Toggle, JsonSerializable {

  /**
   * The "invalid key" that if the "key" field is equal to this, should not be handled
   */
  public static final int INVALID_KEY = -1;

  private KeyBindType type;
  private int key;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private boolean state;

  private Consumer<KeyBind> inhibitor;

  /**
   * Creates a {@link KeyBind}
   *
   * @param type the {@link KeyBindType} of device this bind should be toggled with
   * @param key  the keyboard keycode from {@link org.lwjgl.input.Keyboard} or a mouse button code
   */
  public KeyBind(KeyBindType type, int key) {
    this(type, key, null);
  }

  /**
   * Creates a {@link KeyBind}
   *
   * @param type      the {@link KeyBindType} of device this bind should be toggled with
   * @param key       the keyboard keycode from {@link org.lwjgl.input.Keyboard} or a mouse button code
   * @param inhibitor the action for when this {@link KeyBind} is toggled
   */
  public KeyBind(KeyBindType type, int key, Consumer<KeyBind> inhibitor) {
    this.type = type;
    this.key = key;
    this.inhibitor = inhibitor;
  }

  @SneakyThrows
  @Override
  public void enable() {
    throw new IllegalAccessException("fuck off");
  }

  @SneakyThrows
  @Override
  public void disable() {
    throw new IllegalAccessException("fuck off");
  }

  @Override
  public boolean toggled() {
    return state;
  }

  @Override
  public void setState(boolean state) {
    this.state = state;
    if (inhibitor != null) inhibitor.accept(this);
  }

  @Override
  public void fromJson(JsonElement element) {
    if (!element.isJsonObject()) return;
    JsonObject object = element.getAsJsonObject();

    key = object.get("key").getAsInt();
    type = KeyBindType.from(object.get("type").getAsString());
    setState(object.get("state").getAsBoolean());
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();

    object.addProperty("key", key);
    object.addProperty("type", type.getName());
    object.addProperty("state", state);

    return object;
  }
}
