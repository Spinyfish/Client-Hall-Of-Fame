package de.verschwiegener.atero.audio.ilovemusik;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.audio.Stream;
import de.verschwiegener.atero.audio.Stream.Provider;
import de.verschwiegener.atero.audio.StreamLoader;
//Juli is gott
//gg

public class IloveMusikStreamLoader extends StreamLoader {

    public IloveMusikStreamLoader() {
	super("https://api.ilovemusic.team/traffic/");
    }

    @Override
    public void loadStreams() {
	try {
	    InputStream inputStream = new URL(getDEFAULT_BASE_URL()).openStream();
	    JsonParser parser = new JsonParser();
	    JsonObject JSONObject = parser.parse(IOUtils.toString(inputStream)).getAsJsonObject();
	    for (Map.Entry<String, JsonElement> entry : JSONObject.entrySet()) {
		JsonArray JSONEntries = entry.getValue().getAsJsonArray();
		for (JsonElement JSONElement : JSONEntries) {
		    JsonObject streamObject = JSONElement.getAsJsonObject();
		    Stream stream = Management.instance.streamManager.getStreamByFullName(streamObject.get("name").getAsString());
		    if (stream != null) {
			stream.updateStream(streamObject.get("title").getAsString(), streamObject.get("artist").getAsString(), streamObject.get("cover").getAsString());
		    } else {
			Management.instance.streamManager.getStreams().add(new Stream(Provider.ILoveMusik,
				streamObject.get("name").getAsString(), streamObject.get("stream_url").getAsString(),
				streamObject.get("title").getAsString(), streamObject.get("artist").getAsString(),
				streamObject.get("cover").getAsString()));
		    }
		}
	    }
	    inputStream.close();
	} catch (JsonSyntaxException | IOException e) {
	    e.printStackTrace();
	}
    }
}
