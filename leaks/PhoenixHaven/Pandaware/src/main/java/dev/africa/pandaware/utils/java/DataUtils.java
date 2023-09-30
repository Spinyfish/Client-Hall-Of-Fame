package dev.africa.pandaware.utils.java;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.africa.pandaware.Client;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class DataUtils {
    public void writeJson(JsonElement jsonElement, File file) {
        String json = Client.getInstance().getFileManager().getGson().toJson(jsonElement);

        FileUtils.writeToFile(json, file);
    }

    public JsonElement parseJson(File file) {
        String json = FileUtils.readFromFile(file);

        return parseJson(json);
    }

    public JsonElement parseJson(String text) {
        return new JsonParser().parse(text);
    }
}
