package dev.tenacity.commands.impl;

import com.google.gson.JsonObject;
import dev.tenacity.Tenacity;
import dev.tenacity.commands.Command;
import dev.tenacity.intent.cloud.CloudUtils;
import dev.tenacity.utils.misc.Multithreading;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class LoadCommand extends Command {

    public LoadCommand() {
        super("load", "Loads a script or config from the cloud.", ".load <share code>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            usage();
        } else {
            String shareCode = args[0];
            Multithreading.runAsync(() -> {
                sendChatWithPrefix("Loading script or config from the cloud...");
                JsonObject object = CloudUtils.getData(shareCode);
                if (object == null) {
                    sendChatError("The share code was invalid!");
                    return;
                }

                String[] meta = object.get("meta").getAsString().split(":");

                String name = object.get("name").getAsString();
                boolean isScript = meta[1].equals("true");
                String objectData = object.get("body").getAsString();


                if (isScript) {
                    File scriptFile = new File(Minecraft.getMinecraft().mcDataDir + "/Tenacity/Scripts/" + name + ".js");

                    downloadScriptToFile(scriptFile, objectData);
                } else {
                    if (Tenacity.INSTANCE.getConfigManager().loadConfig(objectData, false)) {
                        sendChatWithPrefix("Config loaded successfully!");
                    } else {
                        sendChatError("The online config did not load successfully!");
                    }
                }


            });
        }
    }

    public void downloadScriptToFile(File file, String content) {
        try {
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
            sendChatWithPrefix("Script downloaded to " + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            sendChatError("Could not download script to " + file.getAbsolutePath());
        }
    }


}
