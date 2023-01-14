package cafe.corrosion.config.base.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.config.base.Config;
import cafe.corrosion.config.dynamic.DynamicConfigLoader;
import cafe.corrosion.task.LoadModulesTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class DynamicConfig extends Config {
    public DynamicConfig(String name) {
        (new Thread(() -> {
            JsonArray configData = DynamicConfigLoader.loadToMemory(name);
            List<JsonObject> objects = new ArrayList();
            configData.forEach((element) -> {
                JsonObject object = element.getAsJsonObject();
                if (object.has("author") && object.has("version")) {
                    this.clientVersion = object.get("version").getAsString();
                    this.author = object.get("author").getAsString();
                } else {
                    objects.add(object);
                }

                this.configData = objects;
            });
            Corrosion.INSTANCE.getConfigManager().addConfig(name, this);
        })).start();
    }

    public void load(String name) {
        (new LoadModulesTask(this.configData)).run();
    }
}
