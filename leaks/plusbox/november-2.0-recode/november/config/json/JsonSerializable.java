/* November.lol © 2023 */
package lol.november.config.json;

import com.google.gson.JsonElement;

/**
 * @author Gavin
 * @since 2.0.0
 */
public interface JsonSerializable {
  /**
   * De-serializes content from a file to load into this method
   *
   * @param element the {@link JsonElement} to configure from
   */
  void fromJson(JsonElement element);

  /**
   * Serializes this object into a {@link JsonElement}
   *
   * @return the serialized object into a {@link JsonElement}
   */
  JsonElement toJson();
}
