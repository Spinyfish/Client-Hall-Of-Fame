package Reallyu.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    public static String toJson(Object o) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(o);
    }

    public static Object parseJson(String s, Class cls) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, cls);
    }
}
