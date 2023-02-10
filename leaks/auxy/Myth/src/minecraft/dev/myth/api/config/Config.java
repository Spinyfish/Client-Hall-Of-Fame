/**
 * @project Myth
 * @author CodeMan
 * @at 07.08.22, 15:46
 */
package dev.myth.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.myth.api.feature.Feature;
import dev.myth.api.interfaces.IMethods;
import dev.myth.api.setting.Setting;
import dev.myth.api.utils.FileUtil;
import dev.myth.main.ClientMain;
import dev.myth.managers.ConfigManager;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.*;
import lombok.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Config implements IMethods {

    private final File configFolder = new File(MC.mcDataDir, "myth/config");

    @Getter
    private final String name;
    private boolean saveKeybinds;

    public Config(String name, boolean saveKeybinds) {
        this.name = name;
        this.saveKeybinds = saveKeybinds;
    }

    public void write() {
        JsonObject jsonObject = new JsonObject();
        ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeatures().values().forEach(feature -> {
            JsonObject featureObject = new JsonObject();
            featureObject.addProperty("state", feature.isEnabled());
            if (saveKeybinds) featureObject.addProperty("keybind", feature.getKeyBind());
            JsonObject settingsObject = new JsonObject();
            feature.getSettings().forEach(setting -> {
                if (setting instanceof BooleanSetting) {
                    settingsObject.addProperty(setting.getName(), ((BooleanSetting) setting).getValue());
                }
                if (setting instanceof EnumSetting<?>) {
                    settingsObject.addProperty(setting.getName(), ((EnumSetting<?>) setting).getValue().ordinal());
                }
                if (setting instanceof NumberSetting) {
                    settingsObject.addProperty(setting.getName(), ((NumberSetting) setting).getValue());
                }
                if (setting instanceof ColorSetting) {
                    settingsObject.addProperty(setting.getName(), ((ColorSetting) setting).getColor());
                }
                if (setting instanceof ListSetting) {
                    JsonObject listObject = new JsonObject();
                    Arrays.asList(((ListSetting) setting).addons).forEach(toggled -> {
                        listObject.addProperty(toggled.name(), ((ListSetting) setting).is(toggled));
                    });
                    settingsObject.add(setting.getName(), listObject);
                }
            });
            featureObject.add("settings", settingsObject);
            jsonObject.add(feature.getName(), featureObject);
        });
        FileUtil.writeJsonToFile(jsonObject, new File(configFolder, name + ".myth").getAbsolutePath());
    }

    public void read() {
        boolean loadVisuals = ClientMain.INSTANCE.manager.getManager(ConfigManager.class).isLoadVisuals();
        read(loadVisuals);
    }

    public void read(boolean loadVisuals) {
        JsonObject jsonObject = FileUtil.readJsonFromFile(new File(configFolder, name + ".myth").getAbsolutePath());
        List<Feature> antiStackOverflow = new ArrayList<>(ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeatures().values());
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            for (Feature feature : antiStackOverflow) {
                if(feature.getCategory() == Feature.Category.VISUAL || feature.getCategory() == Feature.Category.DISPLAY) {
                    if(!loadVisuals) continue;
                }
                if (entry.getKey().equalsIgnoreCase(feature.getName())) {
                    JsonObject jsonFeature = (JsonObject) entry.getValue();
                    if (jsonFeature.get("state").getAsBoolean() != feature.isEnabled()) {
                        try {
                            feature.toggle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (jsonFeature.has("keybind")) {
                        feature.setKeyBind(jsonFeature.get("keybind").getAsInt());
                    }
                    JsonObject settings = jsonFeature.get("settings").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> setting : settings.entrySet()) {
                        if (feature.getSettingByName(setting.getKey()) != null) {
                            Setting<?> set = feature.getSettingByName(setting.getKey());

                            try {
                                if (set instanceof BooleanSetting) {
                                    ((BooleanSetting) set).setValue(setting.getValue().getAsBoolean());
                                }
                                if (set instanceof EnumSetting<?>) {
                                    ((EnumSetting<?>) set).setValueByIndex(setting.getValue().getAsInt());
                                }
                                if (set instanceof NumberSetting) {
                                    ((NumberSetting) set).setValue(setting.getValue().getAsDouble());
                                }
                                if (set instanceof ColorSetting) {
                                    ((ColorSetting) set).setValue(setting.getValue().getAsInt());
                                }
                                if (set instanceof ListSetting) {
                                    JsonObject listObject = setting.getValue().getAsJsonObject();
                                    ListSetting<? extends Enum<?>> listSetting = (ListSetting<? extends Enum<?>>) set;
                                    List<Enum<?>> list = Arrays.asList(listSetting.addons);
                                    list.forEach(toggled -> {
                                        if (listObject.has(toggled.name())) {
                                            if (listSetting.isEnabled(toggled.toString()) != listObject.get(toggled.name()).getAsBoolean()) {
                                                listSetting.toggle(toggled.toString());
                                            }
                                        } else {
                                            if (listSetting.isEnabled(toggled.toString())) {
                                                listSetting.toggle(toggled.toString());
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
