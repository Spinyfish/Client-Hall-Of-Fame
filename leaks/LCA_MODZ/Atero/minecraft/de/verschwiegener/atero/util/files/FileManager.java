package de.verschwiegener.atero.util.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class FileManager {


    public void saveValues(String[] keys, ArrayList<Object[]> values, File directory, String fileName) throws Exception {
        if (values.isEmpty() || (keys.length != values.get(0).length)) {
            throw new Exception("Values is Empty or there are not enouth keys");
        }

        JsonObject mainobject = new JsonObject();
        for (Object[] value : values) {
            if (!(value[0] instanceof String)) {
                throw new Exception("First Key must be the name");
            }
            JsonObject jsonObject = new JsonObject();
            for (int i = 0; i < value.length; i++) {
                Object object = value[i];
                if (object instanceof Integer) {
                    jsonObject.addProperty(keys[i], (int) object);
                } else if (object instanceof String) {
                    jsonObject.addProperty(keys[i], (String) object);
                } else if (object instanceof Boolean) {
                    jsonObject.addProperty(keys[i], (Boolean) object);
                }
            }
            mainobject.add((String) value[0], jsonObject);
        }
        FileSafer.writeJSonData(directory, fileName, mainobject);
    }

    public ArrayList<Object[]> loadValues(String[] keys, File directory, String fileName) {
        ArrayList<Object[]> values = new ArrayList<>();
        try {
            JsonObject mainObject = FileSafer.readConfigFile(directory, fileName);
            if (mainObject != null) {
                for (Map.Entry<String, JsonElement> entry : mainObject.entrySet()) {
                    JsonObject jsonObject = entry.getValue().getAsJsonObject();
                    Object[] object = new Object[keys.length];
                    object[0] = entry.getKey();

                    for (int i = 0; i < keys.length; i++) {
                        if (jsonObject.has(keys[i])) {
                            object[i] = getObjects(jsonObject.get(keys[i]).getAsString());
                        }
                    }
                    values.add(object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private Object getObjects(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.valueOf(value);
        } else if (isInt(value)) {
            return Integer.parseInt(value);
        }
        return value;
    }

    private boolean isInt(String str) {
        try {
            int x = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

}
