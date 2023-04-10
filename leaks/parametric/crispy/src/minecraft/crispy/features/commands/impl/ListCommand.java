package crispy.features.commands.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@CommandInfo(name = "list", alias = "list", syntax = ".list (servername)", description = "Get's the servers players")
public class ListCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if (args.length > 0) {
            new Thread(() -> {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet httpGet = new HttpGet("https://api.minehut.com/server/" + args[0] + "?byName=true");
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    JsonElement parser = new JsonParser().parse(reader.readLine());

                    JsonObject jsonObject = parser.getAsJsonObject();
                    jsonObject = jsonObject.getAsJsonObject("server");

                    List<String> players = new ArrayList<>();
                    for (JsonElement string : jsonObject.getAsJsonArray("players")) {
                        String playersName = getName(string.getAsString());

                        players.add(playersName);
                    }

                    message("Players:", true);
                    for (String yes : players) {
                        message(yes, false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            message(getSyntax(), true);
        }


    }


    public String getName(String uuid) {
        String url = "https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names";
        try {
            @SuppressWarnings("deprecation")
            String nameJson = IOUtils.toString(new URL(url));
            JSONArray nameValue = new JSONArray(nameJson);
            String playerSlot = nameValue.get(nameValue.length() - 1).toString();
            JSONObject nameObject = new JSONObject(playerSlot);
            return nameObject.get("name").toString();
        } catch (IOException | ParseException | JSONException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String readInputStreamAsString(InputStream in)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }
}

