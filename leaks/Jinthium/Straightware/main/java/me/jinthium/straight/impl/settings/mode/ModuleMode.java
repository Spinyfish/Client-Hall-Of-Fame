package me.jinthium.straight.impl.settings.mode;

import io.mxngo.echo.core.EventListener;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.Client;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModuleMode<T extends Module> implements MinecraftInstance, Toggleable, EventListener {
    protected String name;
    protected T parent;
    private final ModeInfo modeInfo;
    private final Map<Setting, String> settings = new LinkedHashMap<>();

    public ModuleMode(){
        if(this.getClass().isAnnotationPresent(ModeInfo.class)){
            this.modeInfo = this.getClass().getAnnotation(ModeInfo.class);

            this.name = modeInfo.name();
            this.parent = Client.INSTANCE.getModuleManager().getModule(modeInfo.parent());
        }else{
            throw new RuntimeException("No ModeInfo annotation found in class: " + this.getClass().getSimpleName());
        }
    }

    protected void registerSettings(Setting... settings) {
        for (Setting setting : settings) {
            this.settings.put(setting, setting.getName());
        }
    }

    public Map<Setting, String> getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public ModeInfo getModeInfo() {
        return modeInfo;
    }

    public Module getParent() {
        return Client.INSTANCE.getModuleManager().getModule(modeInfo.parent());
    }

    public String getInformationSuffix() {
        return name;
    }
}