package club.pulsive.api.config;

import club.pulsive.api.main.Pulsive;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.BooleanComponent;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.MultiSelectPanel;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.client.Logger;
import com.google.gson.*;
import com.sun.org.apache.xpath.internal.operations.Mult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public final class Config {

    private String name;
    public boolean save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();
        Pulsive.INSTANCE.getDraggablesManager().saveDraggableData();
        Pulsive.INSTANCE.getModuleManager().getModules().forEach(module -> {
            JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("Keybind", module.getKeyBind());
            moduleObject.addProperty("Enabled", module.isToggled());
            moduleObject.addProperty("Hidden", module.isHidden());
            JsonObject settingsObject = new JsonObject();
            Module.propertyRepository().propertiesBy(module.getClass()).forEach(property -> {
                if (property instanceof DoubleProperty)
                    settingsObject.addProperty(property.getLabel(), ((DoubleProperty) property).getValue());
                if (property instanceof ColorProperty) {
                    Color color = (Color) property.getValue();
                    JsonObject colorObject = new JsonObject();
                    colorObject.addProperty("Red", color.getRed());
                    colorObject.addProperty("Green", color.getGreen());
                    colorObject.addProperty("Blue", color.getBlue());
                    colorObject.addProperty("Alpha", color.getAlpha());
                    settingsObject.add(property.getLabel(), colorObject);
                }
                if (property instanceof EnumProperty<?>) {
                    EnumProperty<?> enumProperty = (EnumProperty<?>) property;
                    settingsObject.addProperty(property.getLabel(), enumProperty.getValue().ordinal());
                }
                if (property.getValue() instanceof Boolean)
                    settingsObject.addProperty(property.getLabel(), ((Property<Boolean>) property).getValue());
                if (property instanceof MultiSelectEnumProperty) {
                    MultiSelectEnumProperty castedProperty = (MultiSelectEnumProperty) property;
                    settingsObject.addProperty(property.getLabel(), Arrays.toString(castedProperty.getValueIndices()));
                    settingsObject.add(property.getLabel(), gson.toJsonTree(Arrays.stream(castedProperty.getValues()).filter(castedProperty::isSelected).collect(Collectors.toList()), property.type()));
                }
            });
            moduleObject.add("Settings", settingsObject);
            jsonObject.add(module.getName(), moduleObject);
        });
        try {
            FileWriter fileWriter = new FileWriter(Pulsive.INSTANCE.getConfigManager().getConfigDirectory() + "/" + name + ".json");
            gson.toJson(jsonObject, fileWriter);
            fileWriter.close();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean load() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileReader reader = new FileReader(Pulsive.INSTANCE.getConfigManager().getConfigDirectory() + "/" + name + ".json");
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                Module module = Pulsive.INSTANCE.getModuleManager().getModuleByName(entry.getKey());
                if (module != null) {
                    final JsonObject jsonModule = (JsonObject) entry.getValue();
                    boolean toggled = jsonModule.get("Enabled").getAsBoolean();
                    module.setToggled(toggled);
                    module.setKeyBind(jsonModule.get("Keybind").getAsInt());
                    module.setHidden(jsonModule.get("Hidden").getAsBoolean());
                    for (Map.Entry<String, JsonElement> setting : jsonModule.get("Settings").getAsJsonObject().entrySet()) {
                        Property property = Module.propertyRepository().propertyBy(module.getClass(), setting.getKey());
                        if (property != null) {
                            if (property instanceof DoubleProperty)
                                property.setValue(setting.getValue().getAsDouble());
                            if (property instanceof EnumProperty) {
                                EnumProperty<?> enumProperty = (EnumProperty<?>) property;
                                enumProperty.setValue(setting.getValue().getAsInt());
                            }
                            if (property.getValue() instanceof Boolean)
                                property.setValue(setting.getValue().getAsBoolean());
                            if (property instanceof ColorProperty) {
                                JsonObject colorObject = setting.getValue().getAsJsonObject();
                                property.setValue(new Color(colorObject.get("Red").getAsInt(), colorObject.get("Green").getAsInt(), colorObject.get("Blue").getAsInt(), colorObject.get("Alpha").getAsInt()));
                            }

                            if(property instanceof MultiSelectEnumProperty){
                                MultiSelectEnumProperty multiSelectEnumProperty = (MultiSelectEnumProperty) property;
                                //JsonArray multiObject = setting.getValue().getAsJsonArray();
//                                for(JsonElement jsonObject : multiObject.getAsJsonArray()){
//                                    multiSelectEnumProperty.setValue(jsonObject.get);
//                                }
                                //JsonElement rape = multiObject.getAsJsonArray().getAsJsonObject().get("Elements");
                                for(int i = 0; i < setting.getValue().getAsJsonArray().size(); i++){
                                    for(Enum e : multiSelectEnumProperty.getValues()) {
                                        if(e.name().equals(setting.getValue().getAsJsonArray().get(i).getAsString())) {
                                            multiSelectEnumProperty.select(i);
                                        }
                                    }
                                }
                            }
                        
//                                JsonObject colorObject = setting.getValue().getAsJsonObject();
//                                if(colorObject.has("Red") && colorObject.has("Green") && colorObject.has("Blue") && colorObject.has("Alpha")) {
//                                    property.setValue(new Color(colorObject.get("Red").getAsInt(), colorObject.get("Green").getAsInt(),
//                                            colorObject.get("Blue").getAsInt(), colorObject.get("Alpha").getAsInt()));
//
//                                    PlayerUtil.sendMessageWithPrefix("RED: " + colorObject.get("Red").getAsString());
//                                }
//                            }



                        }
                    }
                }
            }
            Pulsive.INSTANCE.getDraggablesManager().loadDraggableData();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete() {
        try {
            Files.delete(Paths.get(Pulsive.INSTANCE.getConfigManager().getConfigDirectory() + "/" + name + ".json"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
