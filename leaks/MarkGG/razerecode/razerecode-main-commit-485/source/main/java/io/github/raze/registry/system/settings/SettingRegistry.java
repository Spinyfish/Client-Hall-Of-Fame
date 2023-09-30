package io.github.raze.registry.system.settings;

import io.github.raze.modules.system.BaseModule;
import io.github.raze.settings.system.BaseSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingRegistry {

    public List<BaseSetting> settings;

    public List<BaseSetting> getSettings() {
        return settings;
    }

    public SettingRegistry() {
        this.settings = new ArrayList<>();
    }

    public void register(BaseSetting input) {
        settings.add(input);
    }

    public void register(BaseSetting... input) {
        settings.addAll(Arrays.asList(input));
    }

    public BaseSetting getSetting(String input, BaseModule parent) {
        return getSettingsByModule(parent).stream().filter(setting -> setting.getName().equalsIgnoreCase(input)).findFirst().get();
    }

    public List<BaseSetting> getSettingsByModule(BaseModule parent) {
        return settings.stream().filter(setting -> setting.getParent() == parent).collect(Collectors.toList());
    }
}
