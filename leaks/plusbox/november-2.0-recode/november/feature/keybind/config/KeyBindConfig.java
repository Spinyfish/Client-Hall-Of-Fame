/* November.lol © 2023 */
package lol.november.feature.keybind.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import lol.november.config.Config;
import lol.november.feature.keybind.KeyBind;
import lol.november.feature.keybind.KeyBindManager;
import lol.november.utility.fs.FileUtils;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class KeyBindConfig extends Config {

  private final KeyBindManager keybinds;

  public KeyBindConfig(KeyBindManager keybinds) {
    super(new File(FileUtils.directory, "binds.json"));
    this.keybinds = keybinds;
  }

  @Override
  public void save() {
    if (!getFile().exists()) createIfNotExists();

    JsonObject object = new JsonObject();

    for (String key : keybinds.getKeyBinds().keySet()) {
      KeyBind bind = keybinds.getKeyBinds().get(key);
      object.add(key, bind.toJson());
    }

    try {
      FileUtils.writeFile(getFile(), FileUtils.gson.toJson(object));
    } catch (Exception e) {
      log.error("Failed to save {}", getFile());
      e.printStackTrace();
    }
  }

  @Override
  public void load() {
    if (!getFile().exists()) return;

    String content;
    try {
      content = FileUtils.readFile(getFile());
    } catch (Exception e) {
      log.error("Failed to load {}", getFile());
      e.printStackTrace();
      return;
    }

    if (content == null || content.isEmpty()) return;

    JsonObject object = FileUtils.jsonParser.parse(content).getAsJsonObject();
    for (String name : keybinds.getKeyBinds().keySet()) {
      JsonElement element = object.get(name);
      if (element != null) keybinds.get(name).fromJson(element);
    }
  }
}
