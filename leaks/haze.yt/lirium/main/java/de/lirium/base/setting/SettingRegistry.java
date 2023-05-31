package de.lirium.base.setting;

import de.lirium.Client;
import de.lirium.impl.module.ModuleFeature;
import god.buddy.aot.BCompiler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SettingRegistry {
    private static final HashMap<ModuleFeature, ArrayList<ISetting<?>>> values = new HashMap<>();

    public static void init() {
        values.clear();
        for (final ModuleFeature module : Client.INSTANCE.getModuleManager().getFeatures()) {
            if (!values.containsKey(module))
                values.put(module, new ArrayList<>());
            for (Field field : module.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Value.class)) {
                    try {
                        field.setAccessible(true);
                        final Object setting = field.get(module);
                        final Value value = field.getAnnotation(Value.class);
                        if (setting instanceof ISetting<?>) {
                            final ISetting<?> castedSetting = (ISetting<?>) setting;
                            castedSetting.name = value.name();
                            castedSetting.setDisplayName(value.displayName());
                            castedSetting.setModule(module);
                            castedSetting.setVisual(value.visual());
                            values.get(module).add(castedSetting);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static ISetting<?> getSetting(String name, ModuleFeature module) {
        final AtomicReference<ISetting<?>> finalSetting = new AtomicReference<>(null);
        values.get(module).forEach(setting -> {
            if (setting.getDisplay().equalsIgnoreCase(name))
                finalSetting.set(setting);
        });
        return finalSetting.get();
    }

    public static ISetting<?> getSetting(String name, Class<? extends ModuleFeature> module) {
        final AtomicReference<ISetting<?>> finalSetting = new AtomicReference<>(null);
        values.get(Client.INSTANCE.getModuleManager().get(module)).forEach(
                setting -> {
                    if (setting.name.equalsIgnoreCase(name))
                        finalSetting.set(setting);
                });
        return finalSetting.get();
    }

    public static ArrayList<ISetting<?>> getSettings(ModuleFeature module) {
        return values.get(module);
    }

    private static ModuleFeature getModule(String name) {
        for (ModuleFeature module : Client.INSTANCE.getModuleManager().getFeatures()) {
            if (module.getClass().getCanonicalName().equalsIgnoreCase(name))
                return module;
        }
        return null;
    }

    public static HashMap<ModuleFeature, ArrayList<ISetting<?>>> getValues() {
        return values;
    }
}
