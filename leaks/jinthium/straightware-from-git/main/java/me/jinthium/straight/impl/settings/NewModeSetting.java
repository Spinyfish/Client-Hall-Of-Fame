package me.jinthium.straight.impl.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.impl.settings.mode.ModuleMode;

import java.util.List;

public class NewModeSetting extends Setting {

    private final List<ModuleMode<?>> values;

    private ModuleMode<?> value;

    @Expose
    @SerializedName("value")
    private String currentMode;

    public NewModeSetting(String name, List<ModuleMode<?>> values, ModuleMode<?> value) {
        this.name = name;
        this.values = values;
        this.value = value;
        this.currentMode = value.getName();
    }

    public List<ModuleMode<?>> getValues() {
        return values;
    }

    public ModuleMode<?> getCurrentMode() {
        this.currentMode = value.getName();
        return this.value;
    }

    public void setValue(ModuleMode<?> value) {
        this.value = value;
        this.currentMode = value.getName();
    }

    public ModuleMode<?> findByString(String name){
        return values.stream().filter(moduleMode -> moduleMode.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public String getConfigValue() {
        return currentMode;
    }
}
