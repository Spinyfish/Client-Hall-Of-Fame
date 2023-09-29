package de.verschwiegener.atero.github;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.audio.Stream;
import de.verschwiegener.atero.audio.Stream.Provider;
import de.verschwiegener.atero.util.files.config.ConfigType;
import de.verschwiegener.atero.util.files.config.handler.XMLHelper;

public class OnlineConfigHandler {
    
    public static void loadOnlineConfigs() throws MalformedURLException, IOException {
	InputStream inputStream = new URL("https://api.github.com/repos/Verschwiegener/AteroConfigs/contents/")
		.openStream();
	JsonParser parser = new JsonParser();
	JsonArray JSONArray = parser.parse(IOUtils.toString(inputStream)).getAsJsonArray();
	for (JsonElement JSONElement : JSONArray) {
	    JsonObject streamObject = JSONElement.getAsJsonObject();
	    XMLHelper.parse(new URL(streamObject.get("download_url").getAsString()).openStream(), ConfigType.online);
	}
    }

}
