package crispy.features.commands.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.util.color.ChatColor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@CommandInfo(name = "Info", alias = "info", description = "Get's the info of the mh server", syntax = ".info [mhserver]")
public class InfoCommand extends Command {
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

                    final String serverPlan = jsonObject.get("activeServerPlan").getAsString();
                    final String online = jsonObject.get("online").getAsString();
                    final String type = jsonObject.get("server_version_type").getAsString();
                    final String motd = jsonObject.get("motd").getAsString();

                    message("Online: " + online, false);
                    message("Type: " + type, false);
                    message("Server plan: " + serverPlan, false);
                    message(ChatColor.translateAlternateColorCodes("MOTD: " + motd), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();

        } else {
            message(getSyntax(), true);
        }
    }
}
