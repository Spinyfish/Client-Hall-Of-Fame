package dev.africa.pandaware.impl.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.file.FileObject;
import dev.africa.pandaware.impl.module.render.ClickGUIModule;
import dev.africa.pandaware.utils.java.FileUtils;

import java.io.File;
import java.io.FileReader;

public class SettingsFile extends FileObject {
    public SettingsFile(File rootFolder, String fileName, Gson gson) {
        super(rootFolder, fileName, gson);
    }

    @Override
    public void save() throws Exception {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("firstLaunch", false);

        jsonObject.addProperty("femboyPositionXXX", Client.getInstance().getClickGUI().getFemboyPosition().getX());
        jsonObject.addProperty("femboyPositionYYY", Client.getInstance().getClickGUI().getFemboyPosition().getY());

        jsonObject.addProperty("Is 18+", Client.getInstance().getModuleManager().getByClass(ClickGUIModule.class).getAllowNSFW().getValue());

        if (Client.getInstance().getSocketManager().getUsername() != null) {
            jsonObject.addProperty("ircName", Client.getInstance().getSocketManager().getUsername());
        }

        FileUtils.writeToFile(this.getGson().toJson(jsonObject), this.getFile());
    }

    @Override
    public void load() throws Exception {
        JsonObject jsonParser = new JsonParser().parse(new FileReader(this.getFile())).getAsJsonObject();

        Client.getInstance().setFirstLaunch(jsonParser.get("firstLaunch").getAsBoolean());

        Client.getInstance().getClickGUI().getFemboyPosition().setX(jsonParser.get("femboyPositionXXX").getAsInt());
        Client.getInstance().getClickGUI().getFemboyPosition().setY(jsonParser.get("femboyPositionYYY").getAsInt());

        if (jsonParser.has("ircName")) {
            Client.getInstance().getSocketManager().shutdown();

            Client.getInstance().getSocketManager().setUsername(jsonParser.get("ircName").getAsString());
            Client.getInstance().getSocketManager().init();
        }
    }
}
