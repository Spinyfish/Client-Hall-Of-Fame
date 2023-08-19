package cc.swift.config;

import cc.swift.Swift;
import cc.swift.config.impl.LocalConfig;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@SuppressWarnings("deprecation")
public final class ConfigManager {

    private final File directory;
    private final HashMap<String, Config> configs = new HashMap<>();

    public ConfigManager(File directory) {
        this.directory = directory;
    }

    public Config getConfig(String name) {
        return configs.get(name.toLowerCase());
    }

    private final Gson gson = new GsonBuilder().create();

    public void loadConfigs() {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("what the fricking frickle");
            }
        }
        if (!directory.isDirectory()) {
            throw new RuntimeException("what the hecking heck");
        }
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            try {
                configs.put(FilenameUtils.getBaseName(file.getName().toLowerCase()), new LocalConfig(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean deleteConfig(Config config) {
        if (config instanceof LocalConfig) {
            return ((LocalConfig) config).getFile().delete();
        }
        return false;
    }

    public void saveConfig(String name) {
        try {
            File file = new File(directory, name + ".json");
            if (!file.exists()) {
                file.createNewFile();
            }

            JsonObject json = new JsonObject();
            JsonArray modules = new JsonArray();
            for (Module module : Swift.INSTANCE.getModuleManager().getModules().values()) {
                modules.add(module.getModuleJson());
            }
            json.add("modules", modules);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(json));
                writer.flush();
            }

            ChatUtil.printChatMessage("Created config " + name);
            configs.put(name, new LocalConfig(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveBinds() {
        try {
            File file = new File(directory, "binds.json");
            if (!file.exists()) {
                file.createNewFile();
            }

            JsonObject json = new JsonObject();
            JsonArray modules = new JsonArray();
            for (Module module : Swift.INSTANCE.getModuleManager().getModules().values()) {
                modules.add(module.getKeybindJson());
            }

            json.add("modules", modules);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(json));
                writer.flush();
            }

        } catch (IOException e) {
            ChatUtil.printChatMessage("An error occurred while attempting to save keybinds.");
            throw new RuntimeException(e);
        }
    }

    public void loadBinds() {
        File file = new File(directory, "binds.json");
        if (!file.exists()) {
            return;
        }

        JsonElement parsed;
        try {
            parsed = JsonParser.parseReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;

        }
        JsonObject json = parsed.getAsJsonObject();

        for (JsonElement element : json.get("modules").getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Module module = Swift.INSTANCE.getModuleManager().getModule(object.get("name").getAsString());
            if (module != null) {
                module.loadKeybindJson(object);
            }
        }
    }
}
