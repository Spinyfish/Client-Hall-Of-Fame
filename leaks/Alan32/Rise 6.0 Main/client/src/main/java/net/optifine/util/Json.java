package net.optifine.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Json {
    public static float getFloat(final JsonObject obj, final String field, final float def) {
        final JsonElement jsonelement = obj.get(field);
        return jsonelement == null ? def : jsonelement.getAsFloat();
    }

    public static boolean getBoolean(final JsonObject obj, final String field, final boolean def) {
        final JsonElement jsonelement = obj.get(field);
        return jsonelement == null ? def : jsonelement.getAsBoolean();
    }

    public static String getString(final JsonObject jsonObj, final String field) {
        return getString(jsonObj, field, null);
    }

    public static String getString(final JsonObject jsonObj, final String field, final String def) {
        final JsonElement jsonelement = jsonObj.get(field);
        return jsonelement == null ? def : jsonelement.getAsString();
    }

    public static float[] parseFloatArray(final JsonElement jsonElement, final int len) {
        return parseFloatArray(jsonElement, len, null);
    }

    public static float[] parseFloatArray(final JsonElement jsonElement, final int len, final float[] def) {
        if (jsonElement == null) {
            return def;
        } else {
            final JsonArray jsonarray = jsonElement.getAsJsonArray();

            if (jsonarray.size() != len) {
                throw new JsonParseException("Wrong array length: " + jsonarray.size() + ", should be: " + len + ", array: " + jsonarray);
            } else {
                final float[] afloat = new float[jsonarray.size()];

                for (int i = 0; i < afloat.length; ++i) {
                    afloat[i] = jsonarray.get(i).getAsFloat();
                }

                return afloat;
            }
        }
    }

    public static int[] parseIntArray(final JsonElement jsonElement, final int len) {
        return parseIntArray(jsonElement, len, null);
    }

    public static int[] parseIntArray(final JsonElement jsonElement, final int len, final int[] def) {
        if (jsonElement == null) {
            return def;
        } else {
            final JsonArray jsonarray = jsonElement.getAsJsonArray();

            if (jsonarray.size() != len) {
                throw new JsonParseException("Wrong array length: " + jsonarray.size() + ", should be: " + len + ", array: " + jsonarray);
            } else {
                final int[] aint = new int[jsonarray.size()];

                for (int i = 0; i < aint.length; ++i) {
                    aint[i] = jsonarray.get(i).getAsInt();
                }

                return aint;
            }
        }
    }
}
