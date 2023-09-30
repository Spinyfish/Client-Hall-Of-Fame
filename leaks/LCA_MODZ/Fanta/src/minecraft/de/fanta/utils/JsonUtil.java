package de.fanta.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonUtil {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {

		@Override
		public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
			if (src % 1 != 0) {
				return new JsonPrimitive(src);
			} else {
				Integer value = (int) Math.round(src);
				return new JsonPrimitive(value);
			}
		}
	}).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().enableComplexMapKeySerialization().serializeNulls()
			.create();

	public static <T extends Object> T getObject(Class<T> c, File file) {
		try {
			if (!file.exists()) {
				return null;
			}
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(reader);
			StringBuilder b = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				b.append(line);
			}
			bufferedReader.close();
			reader.close();
			fileInputStream.close();
			return gson.fromJson(b.toString(), c);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T extends Object> T getObject(Class<T> c, String json) {
		try {
			return gson.fromJson(json, c);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean writeObjectToFile(Object object, File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(gson.toJson(object).getBytes());
			outputStream.flush();
			outputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getObjectAsString(Object o) {
		return gson.toJson(o);
	}

	public static Gson getGson() {
		return gson;
	}

}
