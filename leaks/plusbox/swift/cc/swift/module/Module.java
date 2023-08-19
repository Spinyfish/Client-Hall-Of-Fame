/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:17
 */

package cc.swift.module;

import cc.swift.Swift;
import cc.swift.util.IMinecraft;
import cc.swift.value.Value;
import cc.swift.value.impl.*;
import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.*;

@Getter
public abstract class Module implements IMinecraft {

    private final String name;
    private final Category category;
    private int key;
    @Setter private String suffix;
    @Setter private boolean enabled;
    private final ArrayList<Value<?>> valueList = new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void registerValues(Value<?>... values) {
        valueList.addAll(Arrays.asList(values));
    }

    public void onEnable() {}

    public void onDisable() {}

    public void setKey(int key) {
        this.key = key;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
            Swift.INSTANCE.getEventBus().subscribe(this);
        } else {
            Swift.INSTANCE.getEventBus().unsubscribe(this);
            onDisable();
        }
    }

    public JsonObject getKeybindJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("keybind", getKey());
        return object;
    }
    public JsonObject getModuleJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("toggled", this.enabled);
        JsonArray array = new JsonArray();
        for (Value<?> value : valueList) {
            JsonObject json = new JsonObject();
            json.addProperty("name", value.getName());
            if (value instanceof BooleanValue) {
                json.addProperty("value", ((BooleanValue) value).getValue());
            } else if (value instanceof DoubleValue) {
                json.addProperty("value", ((DoubleValue) value).getValue());
            } else if (value instanceof ModeValue) {
                json.addProperty("value", ((ModeValue<?>) value).getValue().toString());
            } else if (value instanceof StringValue) {
                json.addProperty("value", ((StringValue) value).getValue());
            } else if (value instanceof ColorValue) {
                json.addProperty("value", ((ColorValue) value).getValue().getRGB());
            }
            array.add(json);
            // TODO: finish later
        }
        object.add("settings", array);
        return object;
    }

    public void loadModuleJson(JsonObject object) {
        if (object.get("toggled").getAsBoolean() != enabled)
            toggle();

        JsonArray settings = object.get("settings").getAsJsonArray();

        for (Value<?> value : valueList) {
            for (JsonElement jsonElement : settings) {
                JsonObject json = jsonElement.getAsJsonObject();
                if (json.get("name").getAsString().equals(value.getName())) {
                    if (value instanceof BooleanValue) {
                        ((BooleanValue) value).setValue(json.get("value").getAsBoolean());
                    } else if (value instanceof DoubleValue) {
                        ((DoubleValue) value).setValue(json.get("value").getAsDouble());
                    } else if (value instanceof StringValue) {
                        ((StringValue) value).setValue(json.get("value").getAsString());
                    } else if (value instanceof ModeValue) {
                        ((ModeValue<?>) value).setValueFromString(json.get("value").getAsString());
                    } else if (value instanceof ColorValue) {
                        ((ColorValue) value).setValue(new Color(json.get("value").getAsInt()));
                    }
                    break;
                }
            }
        }
    }

    public void loadKeybindJson(JsonObject object) {
        this.key = (object.get("keybind").getAsInt());
    }

    @Getter
    @AllArgsConstructor
    public enum Category {
        COMBAT("Combat", 'i'),
        MOVEMENT("Movement", 'd'),
        PLAYER("Player", 'e'),
        RENDER("Render", 'f'),
        MISC("Misc", 'c');

        private final String name;
        private final char icon;
    }

}
