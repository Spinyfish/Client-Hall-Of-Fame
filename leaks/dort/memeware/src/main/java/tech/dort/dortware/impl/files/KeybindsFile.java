package tech.dort.dortware.impl.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.file.FileManager;
import tech.dort.dortware.api.file.MFile;
import tech.dort.dortware.api.module.Module;

import java.io.BufferedReader;

public class KeybindsFile extends MFile {

    public KeybindsFile() {
        super("keybinds.json");
    }

    @Override
    public void load(FileManager fileManager) {
        BufferedReader fileContents;
        try {
            fileContents = fileManager.getBufferedReaderForFile(this);
        } catch (Exception e) {
            fileManager.initializeFile(this);
            return;
        }
        JsonElement jsonObject = new JsonParser().parse(fileContents);
        for (Module module : Client.INSTANCE.getModuleManager().getObjects()) {
            try {
                module.setKeyBindNoCall(jsonObject.getAsJsonObject().getAsJsonPrimitive(module.getModuleData().getName()).getAsInt());
            } catch (Exception ignored) {

            }
        }

    }

    @Override
    public void save() {
        final FileManager fileManager = Client.INSTANCE.getFileManager();
        final JsonObject json = new JsonObject();

        for (Module module : Client.INSTANCE.getModuleManager().getObjects()) {
            json.addProperty(module.getModuleData().getName(), module.getKeyBind());
        }

        try {
            fileManager.writeFile(this, json.toString());
        } catch (Exception ignored) {

        }


    }
}
