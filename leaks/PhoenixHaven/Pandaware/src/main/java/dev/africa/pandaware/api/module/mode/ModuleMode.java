package dev.africa.pandaware.api.module.mode;

import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.interfaces.Toggleable;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.setting.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ModuleMode<T extends Module> implements EventListenable, MinecraftInstance, Toggleable {
    protected final String name;
    protected final T parent;

    private final Map<Setting<?>, String> settings = new LinkedHashMap<>();

    protected void registerSettings(Setting<?>... settings) {
        for (Setting<?> setting : settings) {
            this.settings.put(setting, setting.getName());
        }
    }

    public String getInformationSuffix() {
        return null;
    }
}