package de.verschwiegener.atero.util.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;

public class FileSafer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void writeJSonData(File directory, String name, JsonObject jsonObject) {
        createConfigEnvironment(directory);
        try (PrintWriter printWriter = new PrintWriter(
                new BufferedWriter(new FileWriter(new File(directory, name + ".json"))), true)) {
            printWriter.println(GSON.toJson(jsonObject));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createConfigEnvironment(File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static JsonObject readConfigFile(File directory, String name) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(directory, name + ".json")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert bufferedReader != null;
        JsonElement jsonElement = GSON.fromJson(bufferedReader, JsonElement.class);
        return (JsonObject) jsonElement;
    }

}
