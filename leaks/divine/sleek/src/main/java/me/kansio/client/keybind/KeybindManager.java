package me.kansio.client.keybind;

import com.google.gson.*;
import me.kansio.client.Client;
import me.kansio.client.modules.impl.Module;

import java.io.*;

public class KeybindManager {

    private File keybindFile;

    public KeybindManager(File dir) {
        keybindFile = new File(dir, "keybinds.json");
        this.load();
    }

    public void load() {
        try {
            if (!keybindFile.exists()) {
                keybindFile.createNewFile();
                return;
            }

            Reader reader = new FileReader(keybindFile);
            JsonElement node = new JsonParser().parse(reader);

            if (!node.isJsonArray()) {
                return;
            }

            JsonArray arr = node.getAsJsonArray();
            arr.forEach(element -> {
                JsonObject obj = element.getAsJsonObject();
                String modName = obj.get("name").getAsString();
                Module m = Client.getInstance().getModuleManager().getModuleByName(modName);
                if (m != null) {
                    if (obj.get("keybind") != null) {
                        m.setKeyBind(obj.get("keybind").getAsInt());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            JsonArray arr = new JsonArray();
            Client.getInstance().getModuleManager().getModules().forEach(module -> arr.add(module.saveKeybind()));

            Writer writer = new FileWriter(keybindFile);
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(arr);
            System.out.println(json);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
