/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package lodomir.dev.file;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Map;
import lodomir.dev.November;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.ClickGUI;
import lodomir.dev.settings.BooleanSetting;
import lodomir.dev.settings.ModeSetting;
import lodomir.dev.settings.NumberSetting;
import lodomir.dev.settings.Setting;
import lodomir.dev.utils.file.JsonUtils;

public class FileManager {
    public static File ROOT_DIR = new File("november");
    public static File CONFIG_DIR = new File("november/configs");
    public Module mod;
    public static File settings = new File(ROOT_DIR, "settings.json");
    public static File keybinds = new File(ROOT_DIR, "keybinds.json");
    private HashSet<String> modBlacklist = Sets.newHashSet((Object[])new String[]{ClickGUI.class.getName()});

    public void init() {
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdirs();
        }
        if (!CONFIG_DIR.exists() && ROOT_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
        if (!settings.exists()) {
            this.saveMods();
        } else {
            this.loadMods();
        }
        if (!keybinds.exists()) {
            this.saveBinds();
        } else {
            this.setBinds();
        }
    }

    public void saveBinds() {
        try {
            JsonObject json = new JsonObject();
            for (Module mod : November.INSTANCE.moduleManager.getModules()) {
                JsonObject jsonMod = new JsonObject();
                jsonMod.addProperty("keybind", (Number)mod.getKey());
                json.add(mod.getName(), (JsonElement)jsonMod);
            }
            PrintWriter save = new PrintWriter(new FileWriter(keybinds));
            save.println(JsonUtils.prettyGson.toJson((JsonElement)json));
            save.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBinds() {
        try {
            BufferedReader load = new BufferedReader(new FileReader(keybinds));
            JsonObject json = (JsonObject)JsonUtils.jsonParser.parse((Reader)load);
            load.close();
            for (Map.Entry entry : json.entrySet()) {
                Module mod = November.INSTANCE.moduleManager.getModuleByName((String)entry.getKey());
                if (mod == null) continue;
                JsonObject jsonModule = (JsonObject)entry.getValue();
                int key = jsonModule.get("keybind").getAsInt();
                mod.setKey(key);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMods() {
        try {
            JsonObject json = new JsonObject();
            for (Module mod : November.INSTANCE.moduleManager.getModules()) {
                JsonObject jsonMod = new JsonObject();
                jsonMod.addProperty("enabled", Boolean.valueOf(mod.isEnabled()));
                json.add(mod.getName(), (JsonElement)jsonMod);
                for (Setting s : mod.getSettings()) {
                    Setting set;
                    if (s instanceof NumberSetting) {
                        set = (NumberSetting)s;
                        jsonMod.addProperty(set.getName(), (Number)((NumberSetting)set).getValue());
                        json.add(mod.getName(), (JsonElement)jsonMod);
                        continue;
                    }
                    if (s instanceof BooleanSetting) {
                        set = (BooleanSetting)s;
                        jsonMod.addProperty(set.getName(), Boolean.valueOf(((BooleanSetting)set).isEnabled()));
                        json.add(mod.getName(), (JsonElement)jsonMod);
                        continue;
                    }
                    if (!(s instanceof ModeSetting)) continue;
                    set = (ModeSetting)s;
                    jsonMod.addProperty(set.getName(), ((ModeSetting)set).getMode());
                    json.add(mod.getName(), (JsonElement)jsonMod);
                }
            }
            PrintWriter save = new PrintWriter(new FileWriter(settings));
            save.println(JsonUtils.prettyGson.toJson((JsonElement)json));
            save.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isBlacklisted(Module m) {
        return this.modBlacklist.contains(m.getClass().getName());
    }

    public void loadMods() {
        try {
            BufferedReader load = new BufferedReader(new FileReader(settings));
            JsonObject json = (JsonObject)JsonUtils.jsonParser.parse((Reader)load);
            load.close();
            for (Map.Entry entry : json.entrySet()) {
                JsonObject jsonModule;
                boolean enabled;
                Module mod = November.INSTANCE.moduleManager.getModuleByName((String)entry.getKey());
                if (mod != null && !this.modBlacklist.contains(mod.getClass().getName()) && (enabled = (jsonModule = (JsonObject)entry.getValue()).get("enabled").getAsBoolean())) {
                    mod.toggle();
                }
                for (Module m : November.INSTANCE.moduleManager.getModules()) {
                    for (Setting s : mod.getSettings()) {
                        JsonObject jsonObject;
                        if (s instanceof BooleanSetting) {
                            jsonObject = (JsonObject)entry.getValue();
                            boolean bool = jsonObject.get(s.getName()).getAsBoolean();
                            ((BooleanSetting)s).setEnabled(bool);
                            continue;
                        }
                        if (s instanceof NumberSetting) {
                            jsonObject = (JsonObject)entry.getValue();
                            double value = jsonObject.get(s.getName()).getAsInt();
                            ((NumberSetting)s).setValue(value);
                            continue;
                        }
                        if (!(s instanceof ModeSetting)) continue;
                        jsonObject = (JsonObject)entry.getValue();
                        String mode = jsonObject.get(s.getName()).getAsString();
                        ((ModeSetting)s).setMode(mode);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

