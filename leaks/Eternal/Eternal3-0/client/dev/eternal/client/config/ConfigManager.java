package dev.eternal.client.config;

import com.google.gson.*;
import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.Property;
import dev.eternal.client.util.files.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;

public class ConfigManager {

  private static final Client client = Client.singleton();
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final List<Module> mods = client.moduleManager().stream()
      .toList();

  public static void removeConfig(Config config) {
    config.configFile().delete();
    client.configRepository().configList().remove(config);
  }

  public static void saveConfig(String name) {
    saveConfig(new Config(
        FileUtils.getFileFromFolder("SaveState", String.format("%s.cfg", name)),
        System.getProperty("user.name"),
        name,
        new Date(),
        client.clientSettings().version()
    ));
  }

  public static void saveConfig(Config config) {
    try {
      File file = config.configFile();
      FileUtils.writeToFile(file, Collections.singletonList(getConfigJson(config)));
      client.configRepository().configList().add(config);
    } catch (Exception ignored) {
    }
  }

  public static String getConfigJson(Config config) {
    JsonObject jsonObject = new JsonObject();
    JsonArray jsonArray = new JsonArray();
    for (Module module : mods) {
      JsonObject moduleObject = new JsonObject();
      JsonArray moduleArray = new JsonArray();
      moduleObject.addProperty("Name", module.moduleInfo().name());
      moduleObject.addProperty("Toggled", module.isEnabled());
      for (Property<?> setting : client.propertyManager().get(module)) {
        JsonObject settingObject = new JsonObject();
        settingObject.addProperty("Name", setting.name());
        settingObject.add("Value", setting.getJsonElement());
        moduleArray.add(settingObject);
      }
      moduleObject.add("Settings", moduleArray);
      jsonArray.add(moduleObject);
    }
    jsonObject.add("Name", new JsonPrimitive(config.name()));
    jsonObject.add("Author", new JsonPrimitive(config.author()));
    jsonObject.add("Version", new JsonPrimitive(config.version()));
    jsonObject.add("Creation date",
        new JsonPrimitive(SimpleDateFormat.getDateInstance().format(config.creation())));
    jsonObject.add("settings", jsonArray);
    return GSON.toJson(jsonObject);
  }

  public static void loadConfigOrElseSave(String name) {
    Config config = new Config(
        FileUtils.getFileFromFolder("SaveState", String.format("%s.cfg", name)),
        System.getProperty("user.name"),
        name,
        new Date(),
        client.clientSettings().version()
    );
    File file = config.configFile();
    if (FileUtils.readFromFile(file).size() > 0) {
      loadConfig(config);
    } else {
      saveConfig(config);
    }
  }

  @SneakyThrows
  public static void loadConfig(Config config) {
    try {
      File file = config.configFile();
      JsonElement element = JsonParser.parseReader(new FileReader(file));
      JsonObject configObject = element.getAsJsonObject();

      JsonArray jsonArray = configObject.getAsJsonArray("settings");

      jsonArray.forEach(moduleObject -> {
        JsonObject jsonObject = moduleObject.getAsJsonObject();
        Module module = client.moduleManager()
            .getByName(jsonObject.get("Name").getAsString());
        if (module == null) {
          return;
        }
        if (jsonObject.get("Toggled").getAsBoolean() != module.isEnabled()) {
          module.toggle();
        }
        jsonObject.getAsJsonArray("Settings").forEach(settingObject -> {
          JsonObject jsonObject1 = settingObject.getAsJsonObject();
          try {
            client.propertyManager().get(
                module,
                jsonObject1.get("Name").getAsString()
            ).loadJsonElement(jsonObject1.get("Value").getAsJsonPrimitive());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      });
    } catch (Exception ignored) {
    }
  }

  public static void loadBindsOrElseSave() {
    File file = FileUtils.getFileLocationFromFolder("SaveState", "binds.txt");
    if (file.exists()) {
      loadBinds();
    } else {
      saveBinds();
    }
  }

  @SneakyThrows
  public static Config fromFile(File file) {
    JsonElement element = JsonParser.parseReader(new FileReader(file));
    JsonObject configObject = element.getAsJsonObject();
    return new Config(file,
        configObject.get("Author").getAsString(),
        configObject.get("Name").getAsString(),
        DateFormat.getDateInstance().parse(configObject.get("Creation date").getAsString()),
        configObject.get("Version").getAsString());
  }

  public static void saveBinds() {
    try {
      File bindsFile = FileUtils.getFileFromFolder("SaveState", "binds.txt");
      JsonObject bindsObject = new JsonObject();

      JsonArray jsonArray = new JsonArray();
      mods.forEach(module -> {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", module.moduleInfo().name());
        jsonObject.addProperty("KeyBind", module.keyBind());
        jsonArray.add(jsonObject);
      });

      bindsObject.add("binds", jsonArray);

      FileUtils.writeToFile(bindsFile, Collections.singletonList(GSON.toJson(bindsObject)));
    } catch (Exception ignored) {
    }
  }

  @SneakyThrows
  public static void loadBinds() {
    try {
      File bindsFile = FileUtils.getFileFromFolder("SaveState", "binds.txt");

      JsonElement element = JsonParser.parseReader(new FileReader(bindsFile));
      JsonObject bindsObject = element.getAsJsonObject();
      JsonArray jsonArray = bindsObject.getAsJsonArray("binds");

      jsonArray.forEach(element1 -> {
        JsonObject jsonObject = element1.getAsJsonObject();
        Module module = client.moduleManager()
            .getByName(jsonObject.get("Name").getAsString());
        module.keyBind(jsonObject.get("KeyBind").getAsInt());
      });
    } catch (Exception ignored) {
    }
  }

}
