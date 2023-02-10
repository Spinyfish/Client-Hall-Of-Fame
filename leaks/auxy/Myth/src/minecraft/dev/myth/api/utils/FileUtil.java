/**
 * @project Myth
 * @author CodeMan
 * @at 07.08.22, 15:48
 */
package dev.myth.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.util.ArrayList;

@UtilityClass
public class FileUtil {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public JsonObject readJsonFromFile(String path) {
        try {
            return GSON.fromJson(new FileReader(path), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeJsonToFile(JsonObject json, String path) {
        try {
            FileWriter writer = new FileWriter(path);
            GSON.toJson(json, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readLinesFromFile(String path) {
        try {
            ArrayList<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeLinesToFile(String path, ArrayList<String> lines) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
