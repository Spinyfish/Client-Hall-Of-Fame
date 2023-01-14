package cafe.corrosion.config;

import cafe.corrosion.Corrosion;
import cafe.corrosion.config.base.Config;
import cafe.corrosion.config.base.impl.LocalConfig;
import cafe.corrosion.task.ExportModulesTask;
import cafe.corrosion.task.LoadModulesTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;

public class ConfigManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Map<String, Config> configurations = new HashMap();
    private final File directory;
    private final Path modulesPath;

    public ConfigManager() {
        this.directory = new File("Corrosion" + File.separator + "configs" + File.separator);
        this.modulesPath = Paths.get("modules.json");
    }

    public void loadConfigs() {
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }

        Arrays.stream((Object[])Objects.requireNonNull(this.directory.listFiles())).filter((file) -> {
            return file.getName().endsWith(".json");
        }).forEach((file) -> {
            String name = file.getName().replace(".json", "");
            this.configurations.put(name, new LocalConfig(Paths.get(file.getPath())));
            System.out.println("Successfully loaded configuration " + name + "!");
        });
    }

    public Config getConfig(String name, boolean web) {
        return (Config)this.configurations.get(name + (web ? "-web" : ""));
    }

    public void addConfig(String name, Config config) {
        this.configurations.put(name + "-web", config);
    }

    public void exportSettings() {
        (new ExportModulesTask(this.modulesPath, (JsonObject)null)).run();
    }

    public void loadDefault() {
        try {
            Path path = Paths.get("modules.json");
            if (path.toFile().exists()) {
                byte[] bytes = Files.readAllBytes(path);
                JsonArray configData = (JsonArray)(new Gson()).fromJson(new String(bytes), JsonArray.class);
                List<JsonObject> objects = new ArrayList();
                configData.forEach((element) -> {
                    objects.add(element.getAsJsonObject());
                });
                (new LoadModulesTask(objects)).run();
            }
        } catch (Throwable var5) {
            throw var5;
        }
    }

    public List<String> getConfigNames() {
        return (List)this.configurations.keySet().stream().sorted((o1, o2) -> {
            return Boolean.compare(o1.endsWith("-web"), o2.endsWith("-web"));
        }).collect(Collectors.toList());
    }

    public void saveConfig(String name) {
        Path path = Paths.get(this.directory.getPath() + File.separator + name + ".json");
        JsonObject data = new JsonObject();
        data.addProperty("author", "User");
        data.addProperty("version", Corrosion.INSTANCE.getVersion().toString());
        (new ExportModulesTask(path, data)).run();
        this.configurations.put(name, new LocalConfig(path));
    }

    public Map<String, Config> getConfigurations() {
        return this.configurations;
    }
}
