package me.kansio.client.commands.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.kansio.client.Client;
import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.network.HttpUtil;

import java.io.File;
import java.text.MessageFormat;

@CommandData(
        name = "config",
        description = "Handles configs"
)
public class ConfigCommand extends Command {

    @Override
    public void run(String[] args) {

        try {

            switch (args[0].toLowerCase()) {
                case "save": {
                    Client.getInstance().getConfigManager().saveConfig(args[1]);
                    ChatUtil.log("Saved Config " + args[1]);
                    break;
                }
                case "load": {
                    if (args.length == 3) {
                        Client.getInstance().getConfigManager().loadConfig(args[1], args[2].equals("keys"));
                    } else {
                        Client.getInstance().getConfigManager().loadConfig(args[1], false);
                    }
                    // .CONFIG BLOCKS ADESGOJHAOG
                    break;
                }
                case "reload": {
                    Client.getInstance().getConfigManager().loadConfigs();
                    ChatUtil.log("Reloaded Configs");
                    break;
                }
                case "delete":
                case "remove": {
                    Client.getInstance().getConfigManager().removeConfig(args[1]);
                    ChatUtil.log("Removed Config " + args[1]);
                    break;
                }
                case "list": {
                    for (File file : Client.getInstance().getConfigManager().getDir().listFiles()) {
                        ChatUtil.log("- " + file.getName().replaceAll(".sleek", ""));
                    }
                    break;
                }
                case "verified": {
                    switch (args[1].toLowerCase()) {
                        case "list": {
                            JsonElement element = new JsonParser().parse(HttpUtil.get("http://zerotwoclient.xyz:13337/api/v1/verifiedConfigs"));

                            if (element.isJsonArray()) {
                                JsonArray rr = element.getAsJsonArray();
                                rr.forEach(ele -> {
                                    JsonObject obj = ele.getAsJsonObject();

                                    ChatUtil.log(MessageFormat.format("Config \"{0}\" made by {1} was last updated on {2}", obj.get("name").getAsString(), obj.get("author").getAsString(), obj.get("lastUpdate").getAsString().split(" ")[1]));
                                });
                            }
                            break;
                        }
                        case "load": {

                            JsonElement ar2 = new JsonParser().parse(HttpUtil.get("http://zerotwoclient.xyz:13337/api/v1/verifiedConfigs"));

                            if (!ar2.isJsonArray()) {
                                return;
                            }

                            ar2.getAsJsonArray().forEach(fig -> {
                                if (fig.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(args[2])) {
                                    JsonArray arr = new JsonParser().parse(fig.getAsJsonObject().get("data").getAsString()).getAsJsonArray();
                                    arr.forEach(element -> {
                                        JsonObject obj = element.getAsJsonObject();
                                        String modName = obj.get("name").getAsString();
                                        Module m = Client.getInstance().getModuleManager().getModuleByName(modName);
                                        if (m != null) {
                                            m.load(obj, false);
                                        }
                                    });
                                }
                            });


                            break;
                        }
                    }
                    break;
                }
            }
        } catch (Throwable gnored) {
            gnored.printStackTrace();
            ChatUtil.log(".config save <configName>");
            ChatUtil.log(".config load <configName>");
            ChatUtil.log(".config remove <configName>");
            ChatUtil.log(".config verified <list | load> [name]");
            ChatUtil.log(".config reload");
            ChatUtil.log(".config list");
        }
    }
}
