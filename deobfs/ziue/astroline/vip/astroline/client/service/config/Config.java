/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.altMgr.Alt
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.layout.altMgr.kingAlts.KingAlts
 *  vip.astroline.client.service.config.preset.PresetManager
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.service.module.value.ModeValue
 *  vip.astroline.client.service.module.value.Value
 *  vip.astroline.client.service.module.value.ValueManager
 */
package vip.astroline.client.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.altMgr.Alt;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.layout.altMgr.kingAlts.KingAlts;
import vip.astroline.client.service.config.preset.PresetManager;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.service.module.value.ModeValue;
import vip.astroline.client.service.module.value.Value;
import vip.astroline.client.service.module.value.ValueManager;

public class Config {
    public static final String DIR = "Astroline";

    public static void loadPresets() {
        File presetsFolder = new File("Astroline/presets");
        PresetManager.presets.clear();
        File[] fileArray = Objects.requireNonNull(presetsFolder.listFiles());
        int n = fileArray.length;
        int n2 = 0;
        while (n2 < n) {
            File file = fileArray[n2];
            if (!file.isDirectory() && file.getName().endsWith(".prs")) {
                PresetManager.presets.add(file.getName().substring(0, file.getName().length() - 4));
            }
            ++n2;
        }
    }

    public static void saveConfig() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Astroline/config.json"));
            JSONObject total = new JSONObject(true);
            total.put("KingAltsAPI", (Object)KingAlts.API_KEY);
            JSONArray alts = new JSONArray();
            for (Alt alt : GuiAltMgr.alts) {
                JSONObject altsObj = new JSONObject(true);
                altsObj.put("Email", (Object)alt.getEmail());
                if (!alt.isCracked()) {
                    altsObj.put("Password", (Object)alt.getPassword());
                    altsObj.put("Name", (Object)alt.getName());
                }
                altsObj.put("Star", (Object)alt.isStarred());
                alts.add((Object)altsObj);
            }
            total.put("Alts", (Object)alts);
            total.put("Modules", (Object)Config.saveModules(true));
            writer.write(JSONObject.toJSONString((Object)total, (boolean)true));
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject saveModules(boolean keyBinds) {
        JSONObject modules = new JSONObject();
        Iterator iterator = Astroline.INSTANCE.moduleManager.getModules().iterator();
        while (iterator.hasNext()) {
            Module module = (Module)iterator.next();
            JSONObject moduleConfig = new JSONObject(true);
            moduleConfig.put("isToggled", (Object)module.isToggled());
            if (keyBinds) {
                moduleConfig.put("Keybind", (Object)module.getKey());
            }
            moduleConfig.put("isHidden", (Object)module.isHidden());
            JSONObject values = new JSONObject(true);
            for (Value value : ValueManager.getValueByModName((String)module.getName())) {
                JSONObject valueConfig = new JSONObject(true);
                values.put(value.getKey(), value.getValue());
            }
            moduleConfig.put("Value", (Object)values);
            modules.put(module.getName(), (Object)moduleConfig);
        }
        return modules;
    }

    public static void loadAlts(JSONArray alts) {
        Iterator iterator = GuiAltMgr.alts.iterator();
        while (iterator.hasNext()) {
            Alt alt = (Alt)iterator.next();
            if (!alts.contains((Object)alt.getName())) continue;
            JSONArray altConfig = alts.getJSONArray(4);
            try {
                alt.email = altConfig.getString(1);
                alt.password = altConfig.getString(2);
                alt.starred = altConfig.getBoolean(3);
                alt.name = altConfig.getString(4);
                Astroline.currentAlt = new String[]{alt.email, alt.password};
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    public static void loadModules(JSONObject modules) {
        var1_1 = Astroline.INSTANCE.moduleManager.getModules().iterator();
        while (true) {
            if (var1_1.hasNext() == false) return;
            module = (Module)var1_1.next();
            if (module == null) {
                return;
            }
            if (modules.containsKey((Object)module.getName())) {
                moduleConfig = modules.getJSONObject(module.getName());
                try {
                    module.toggled = moduleConfig.getBoolean("isToggled");
                    module.refreshEvents();
                }
                catch (Throwable var4_5) {
                    // empty catch block
                }
                if (moduleConfig.containsKey((Object)"Keybind")) {
                    module.setKey(moduleConfig.getInteger("Keybind").intValue());
                }
                module.setHidden(moduleConfig.getBoolean("isHidden").booleanValue());
                if (!moduleConfig.containsKey((Object)"Value")) continue;
            } else {
                System.out.println("Skipping loading module: " + module.getName());
                continue;
            }
            moduleValues = moduleConfig.getJSONObject("Value");
            var5_6 = ValueManager.getValueByModName((String)module.getName()).iterator();
            while (true) {
                if (!var5_6.hasNext()) ** break;
                value = (Value)var5_6.next();
                if (!moduleValues.containsKey((Object)value.getKey())) {
                    System.out.println("Skipping load Value: " + value.getKey());
                    continue;
                }
                if (value instanceof BooleanValue) {
                    value.setValue((Object)moduleValues.getBooleanValue(value.getKey()));
                }
                if (value instanceof FloatValue) {
                    value.setValue((Object)Float.valueOf(moduleValues.getFloatValue(value.getKey())));
                }
                if (!(value instanceof ModeValue)) continue;
                value.setValue((Object)moduleValues.getString(value.getKey()));
            }
            break;
        }
    }
}
