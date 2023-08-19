package cc.swift.config;

import cc.swift.Swift;
import cc.swift.module.Module;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@AllArgsConstructor
@Getter
@Setter
public abstract class Config {
    private final String name;
    private final JsonObject json;

    public final void loadConfig() {
        for (JsonElement element : json.get("modules").getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Module module = Swift.INSTANCE.getModuleManager().getModule(object.get("name").getAsString());
            if (module != null) {
                module.loadModuleJson(object);
            }
        }
    }
}
