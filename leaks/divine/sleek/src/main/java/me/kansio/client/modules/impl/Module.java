package me.kansio.client.modules.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import me.kansio.client.Client;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.visuals.ClickGUI;
import me.kansio.client.modules.impl.visuals.HUD;
import me.kansio.client.value.Value;
import me.kansio.client.value.value.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public abstract class Module {

    protected static final Minecraft mc = Minecraft.getMinecraft();

    private String name;
    private boolean toggled;
    private int keyBind;
    private String suffix = "";
    private ModuleCategory category;
    private List<SubSettings> subSettings = new ArrayList<>();

    public Module() {
        name = getClass().getAnnotation(ModuleData.class).name();
        keyBind = getClass().getAnnotation(ModuleData.class).bind();
        category = getClass().getAnnotation(ModuleData.class).category();
    }

    public Module(String name, int keyBind, ModuleCategory category) {
        this.category = category;
        this.keyBind = keyBind;
        this.name = name;
    }

    public Module(String name, ModuleCategory category) {
        this(name, Keyboard.KEY_NONE, category);
    }

    public String getFormattedSuffix() {
        if (getSuffix().equalsIgnoreCase("")) return "";

        HUD hud = (HUD) Client.getInstance().getModuleManager().getModuleByName("HUD");


        String suffix;

        if (getSuffix().startsWith(" "))
            suffix = getSuffix().replaceFirst(" ", "");
        else
            suffix = getSuffix();

        String formatted = hud.getListSuffix().getValue().replaceAll("%s", suffix);


        return formatted;
    }

    public void toggle() {
        toggled = !toggled;

        if (toggled) {
            Client.getInstance().getEventBus().register(this);
            onEnable();
        } else {
            Client.getInstance().getEventBus().unregister(this);
            onDisable();
        }
        if (!(this instanceof ClickGUI))
            onToggled();
    }

    public String getSuffix() {
        return suffix;
    }

    public void onToggled() {

    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public void registerSubSettings(SubSettings... subSettings) {
        Collections.addAll(this.subSettings, subSettings);
    }

    public void register(Value... properties) {
        //Collections.addAll(Client.getInstance().getValueManager().getObjects(), properties);
    }

    public void unRegister(Value... properties) {
        Collections.addAll(Client.getInstance().getValueManager().getObjects(), properties);
        Client.getInstance().getValueManager().getObjects().removeAll(Arrays.asList(properties));
    }

    public List<Value> getValues() {
        return Client.getInstance().getValueManager().getValuesFromOwner(this);
    }

    public void load(JsonObject obj, boolean loadKey) {
        obj.entrySet().forEach(data -> {
            switch (data.getKey()) {
                case "name": {
                    break;
                }
                case "keybind": {
                    if (loadKey) {
                        this.keyBind = data.getValue().getAsInt();
                    }
                    break;
                }
                case "enabled": {
                    if (!(isToggled() && data.getValue().getAsBoolean()) && !(!isToggled() && !data.getValue().getAsBoolean()))
                        setToggled(data.getValue().getAsBoolean());
                    break;
                }
            }

            Value val = Client.getInstance().getValueManager().getValueFromOwner(this, data.getKey());

            if (val != null) {
                if (val instanceof BooleanValue) {
                    val.setValue(data.getValue().getAsBoolean());
                } else if (val instanceof NumberValue) {
                    if (((NumberValue<?>) val).getIncrement() instanceof Double) {
                        val.setValue(data.getValue().getAsDouble());
                    }
                    if (((NumberValue<?>) val).getIncrement() instanceof Float) {
                        val.setValue((float) data.getValue().getAsDouble());
                    }
                    if (((NumberValue<?>) val).getIncrement() instanceof Long) {
                        val.setValue((long) data.getValue().getAsDouble());
                    }
                    if (((NumberValue<?>) val).getIncrement() instanceof Integer) {
                        val.setValue((int) data.getValue().getAsDouble());
                    }
                } else if (val instanceof ModeValue) {
                    val.setValue(data.getValue().getAsString());
                } else if (val instanceof StringValue) {
                    val.setValue(data.getValue().getAsString());
                }
            }

        });
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;

        if (toggled) {
            Client.getInstance().getEventBus().register(this);
            onEnable();
        } else {
            Client.getInstance().getEventBus().unregister(this);
            onDisable();
        }
        if (!(this instanceof ClickGUI))
            onToggled();
    }

    public JsonObject save() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("keybind", this.keyBind);
        json.addProperty("enabled", this.toggled);
        getValues().forEach(value -> json.addProperty(value.getName(), value.getValue().toString()));
        return json;
    }

    public JsonObject saveKeybind() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("keybind", this.keyBind);
        json.addProperty("keybindName", Keyboard.getKeyName(this.keyBind));

        return json;
    }

    public void setKeyBind(int keyBind, boolean save) {
        System.out.println("Saved KeyBinds " + this.name);
        this.keyBind = keyBind;
        Client.getInstance().getKeybindManager().save();
    }
}
