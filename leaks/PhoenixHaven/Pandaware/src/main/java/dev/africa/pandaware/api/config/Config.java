package dev.africa.pandaware.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.api.setting.Setting;
import dev.africa.pandaware.impl.setting.*;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.java.DataUtils;
import dev.africa.pandaware.utils.math.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.var;

import java.awt.*;
import java.io.File;

@Getter
@AllArgsConstructor
public class Config {
    private final String name;
    private final File file;

    public void save(boolean clientConfig) {
        try {
            if (!this.getFile().exists()) {
                this.getFile().createNewFile();
            }

            JsonObject configObject = new JsonObject();

            Client.getInstance().getModuleManager()
                    .getMap()
                    .values()
                    .stream()
                    .filter(module -> module.getData().getCategory() != Category.VISUAL || clientConfig).forEach(module -> {
                        JsonObject moduleObject = new JsonObject();

                        moduleObject.addProperty("enabled", module.getData().isEnabled());

                        if (clientConfig) {
                            moduleObject.addProperty("hidden", module.getData().isHidden());
                            moduleObject.addProperty("key", module.getData().getKey());
                        }

                        JsonObject settingsObject = new JsonObject();
                        if (!module.getSettings().isEmpty()) {
                            module.getSettings()
                                    .keySet()
                                    .forEach(setting -> {
                                        if (setting.isSaveConfig() || clientConfig) {
                                            this.addSettings(settingsObject, setting, null);
                                        }
                                    });

                            if (module.getModeSetting() != null && !module.getModeSetting().getValues().isEmpty()) {
                                if (!module.getModeSetting().getValues().isEmpty()) {
                                    module.getModeSetting().getValues().forEach(moduleMode ->
                                            moduleMode.getSettings()
                                                    .keySet()
                                                    .forEach(setting -> {
                                                        if (setting.isSaveConfig() || clientConfig) {
                                                            this.addSettings(settingsObject, setting, moduleMode);
                                                        }
                                                    }));
                                }
                            }

                            moduleObject.add("settings", settingsObject);
                        }

                        configObject.add(module.getData().getName(), moduleObject);
                    });

            DataUtils.writeJson(configObject, this.getFile());
        } catch (Exception e) {
            e.printStackTrace();

            Printer.consoleError("There was an error while saving the config: " + this.getName());
        }
    }

    public void load(boolean clientConfig) {
        try {
            if (!this.getFile().exists()) {
                return;
            }

            JsonObject jsonObject = DataUtils.parseJson(this.getFile()).getAsJsonObject();

            Client.getInstance().getModuleManager()
                    .getMap()
                    .values()
                    .stream()
                    .filter(module -> module.getData().getCategory() != Category.VISUAL || clientConfig).forEach(module -> {
                        JsonObject moduleObject = jsonObject.getAsJsonObject(module.getData().getName());

                        if (moduleObject != null) {
                            boolean toggled = moduleObject.get("enabled").getAsBoolean();
                            if (toggled && !module.getData().isEnabled() || !toggled && module.getData().isEnabled()) {
                                module.toggle();
                            }

                            if (clientConfig) {
                                module.getData().setHidden(moduleObject.get("hidden").getAsBoolean());
                                module.getData().setKey(moduleObject.get("key").getAsInt());
                            }

                            JsonObject settingsObject = moduleObject.getAsJsonObject("settings");

                            if (settingsObject != null) {
                                module.getSettings().keySet().forEach(setting -> {
                                    if (setting.isSaveConfig() || clientConfig) {
                                        JsonElement value = settingsObject.get(setting.getName());

                                        if (value != null) {
                                            this.fetchSettings(setting, value);
                                        }
                                    }
                                });

                                if (module.getModeSetting() != null && !module.getModeSetting().getValues().isEmpty()) {
                                    module.getModeSetting().getValues().forEach(moduleMode -> {
                                        if (!moduleMode.getSettings().isEmpty()) {
                                            moduleMode.getSettings().keySet().forEach(setting -> {
                                                if (setting.isSaveConfig() || clientConfig) {
                                                    JsonElement value = settingsObject
                                                            .get(moduleMode.getName() + "_" + setting.getName());

                                                    if (value != null) {
                                                        this.fetchSettings(setting, value);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

            Printer.consoleError("There was an error while loading the config: " + this.getName());
        }
    }

    public void delete() {
        this.getFile().delete();

        Client.getInstance().getConfigManager().getItems().remove(this);
    }

    void addSettings(JsonObject settingsObject, Setting<?> setting, ModuleMode<?> moduleMode) {
        String settingName = moduleMode != null ? moduleMode.getName() + "_" + setting.getName() : setting.getName();

        if (setting instanceof ColorSetting) {
            var colorSetting = (ColorSetting) setting;

            settingsObject.addProperty(settingName, colorSetting.getValue().getRGB());
        } else if (setting instanceof EnumSetting) {
            var enumSetting = (EnumSetting) setting;

            settingsObject.addProperty(settingName, enumSetting.getValue().name());
        } else if (setting instanceof NumberRangeSetting) {
            var rangeSetting = (NumberRangeSetting) setting;

            String value = rangeSetting.getFirstValue().doubleValue() + "_"
                    + rangeSetting.getSecondValue().doubleValue();

            settingsObject.addProperty(settingName, value);
        } else if (setting instanceof BooleanSetting) {
            var booleanSetting = (BooleanSetting) setting;

            settingsObject.addProperty(settingName, booleanSetting.getValue());
        } else if (setting instanceof ModeSetting) {
            var modeSetting = (ModeSetting) setting;

            settingsObject.addProperty(settingName, modeSetting.getValue().getName());
        } else {
            settingsObject.addProperty(settingName, String.valueOf(setting.getValue()));
        }
    }

    void fetchSettings(Setting<?> setting, JsonElement value) {
        if (setting instanceof ColorSetting) {
            ((ColorSetting) setting).setValue(new Color(value.getAsInt()));
        } else if (setting instanceof EnumSetting) {
            var enumSetting = (EnumSetting) setting;

            for (Enum enumSettingValue : enumSetting.getValues()) {
                if (value.getAsString().equalsIgnoreCase(enumSettingValue.name())) {
                    enumSetting.setValue(enumSettingValue);
                    break;
                }
            }
        } else if (setting instanceof NumberRangeSetting) {
            var rangeSetting = (NumberRangeSetting) setting;

            String[] split = value.getAsString().split("_");

            double firstValue = Double.parseDouble(split[0]);
            double secondValue = Double.parseDouble(split[1]);

            if (rangeSetting.getIncrement() != null) {
                firstValue = MathUtils.roundToIncrement(firstValue, rangeSetting.getIncrement().doubleValue());
                secondValue = MathUtils.roundToIncrement(secondValue, rangeSetting.getIncrement().doubleValue());
            }

            rangeSetting.setValue(new Number[]{firstValue, secondValue});
        } else if (setting instanceof BooleanSetting) {
            ((BooleanSetting) setting).setValue(value.getAsBoolean());
        } else if (setting instanceof NumberSetting) {
            ((NumberSetting) setting).setValue(value.getAsDouble());
        } else if (setting instanceof ModeSetting) {
            var modeSetting = (ModeSetting) setting;

            for (ModuleMode<?> moduleModeValue : modeSetting.getValues()) {
                if (value.getAsString().equalsIgnoreCase(moduleModeValue.getName())) {
                    modeSetting.setValue(moduleModeValue);
                    break;
                }
            }
        } else if (setting instanceof TextBoxSetting) {
            ((TextBoxSetting) setting).setValue(value.getAsString());
        }
    }
}
