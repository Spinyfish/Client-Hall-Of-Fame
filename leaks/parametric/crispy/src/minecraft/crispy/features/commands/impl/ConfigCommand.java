package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.features.hacks.ConfigModule;
import net.minecraft.client.Minecraft;
import net.superblaubeere27.valuesystem.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@CommandInfo(name = "Config", description = "Allows the user to load configs and saves configs", alias = "config", syntax = ".config load [config] || .config save [config]")
public class ConfigCommand extends Command {
    public String dir;
    File configfile;
    private final File clientDir = new File(Minecraft.getMinecraft().mcDataDir,   "Crispy");

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args[0].equalsIgnoreCase("load")) {

            dir = args[1];
            configfile = new File(clientDir, args[1] + ".json");
            if(configfile.exists()) {
                Crispy.INSTANCE.getHackManager().getHacks().forEach(hack -> hack.setState(false));
            }

            load();
            NotificationPublisher.queue("Config Manager", "Loaded " + args[1], NotificationType.SUCCESS);
            Crispy.addChatMessage("Loaded!");



        } else if(args[0].equalsIgnoreCase("save")) {

            configfile = new File(clientDir, args[1] + ".json");
            ConfigModule configModule = new ConfigModule(args[1], "Config", Category.CONFIG);
            if(!configfile.exists()) {
                Crispy.INSTANCE.getHackManager().addConfigModule(configModule);
            }

            save();

            NotificationPublisher.queue("Config Manager", "Saved " + args[1], NotificationType.SUCCESS);


        } else {
            Crispy.addChatMessage(getSyntax());
        }





    }

    private JsonObject toJsonObject() {
        System.out.println("Saving settings");

        JsonObject obj = new JsonObject();


        {
            JsonObject metadata = new JsonObject();

            metadata.addProperty("clientVersion", 2.0);

            obj.add("metadata", metadata);
        }

        {
            JsonObject modules = new JsonObject();

            for (Hack module : Crispy.INSTANCE.getHackManager().getHacks()) {
                JsonObject moduleObject = new JsonObject();

                moduleObject.addProperty("state", module.isEnabled());
                moduleObject.addProperty("keybind", module.getKey());

                modules.add(module.getName(), moduleObject);
            }

            obj.add("modules", modules);
        }
        {
            JsonObject values = new JsonObject();

            for (Map.Entry<String, List<Value>> stringListEntry : Crispy.INSTANCE.getValueManager().getAllValues().entrySet()) {
                JsonObject value = new JsonObject();

                for (Value value1 : stringListEntry.getValue()) value1.addToJsonObject(value);

                values.add(stringListEntry.getKey(), value);
            }

            obj.add("values", values);
        }

        return obj;
    }

    public void save() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        clientDir.mkdirs();



        Files.write(toJsonObject().toString().getBytes(StandardCharsets.UTF_8), configfile);
    }
    public void load() {


        List<String> backupReasons = new ArrayList<>();

        try {
            JsonObject object = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(configfile)));

            //<editor-fold desc="metadata">
            if (object.has("metadata")) {
                JsonElement metadataElement = object.get("metadata");

                if (metadataElement instanceof JsonObject) {
                    JsonObject metadata = (JsonObject) metadataElement;

                    JsonElement clientVersion = metadata.get("clientVersion");

                    if (clientVersion != null && clientVersion.isJsonPrimitive() && ((JsonPrimitive) clientVersion).isNumber()) {
                        double version = clientVersion.getAsDouble();

                        if (version > Double.parseDouble(Crispy.INSTANCE.getVersion())) {
                            backupReasons.add("Version number of save file (" + version + ") is higher than " + version);
                        }
                        if (version < Double.parseDouble(Crispy.INSTANCE.getVersion())) {
                            backupReasons.add("Version number of save file (" + version + ") is lower than " + Crispy.INSTANCE.getVersion());
                        }
                    } else {
                        backupReasons.add("'clientVersion' object is not valid.");
                    }
                } else {
                    backupReasons.add("'metadata' object is not valid.");
                }

            } else {
                backupReasons.add("Save file has no metadata");
            }
            //</editor-fold>

            //<editor-fold desc="modules">
            JsonElement modulesElement = object.get("modules");

            if (modulesElement instanceof JsonObject) {
                JsonObject modules = (JsonObject) modulesElement;

                for (Map.Entry<String, JsonElement> stringJsonElementEntry : modules.entrySet()) {
                    Hack module = Crispy.INSTANCE.getHackManager().getModule(stringJsonElementEntry.getKey(), true);
                    if(module == null)
                        continue;

                    if (stringJsonElementEntry.getValue() instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject) stringJsonElementEntry.getValue();

                        JsonElement state = moduleObject.get("state");

                        if (state instanceof JsonPrimitive && ((JsonPrimitive) state).isBoolean()) {
                            module.setState(state.getAsBoolean());
                        } else {

                        }

                        JsonElement keybind = moduleObject.get("keybind");

                        if (keybind instanceof JsonPrimitive && ((JsonPrimitive) keybind).isNumber()) {

                        }
                    }
                }
            } else {
                Crispy.addChatMessage("ERROR cannot load values, Please update the config to " + Crispy.INSTANCE.getVersion());
            }
            //</editor-fold>

            //<editor-fold desc="values">
            JsonElement valuesElement = object.get("values");

            if (valuesElement instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) valuesElement).entrySet()) {
                    List<Value> values = Crispy.INSTANCE.getValueManager().getAllValuesFrom(stringJsonElementEntry.getKey());

                    if (values == null) {

                        continue;
                    }

                    if (!stringJsonElementEntry.getValue().isJsonObject()) {

                        continue;
                    }

                    JsonObject valueObject = (JsonObject) stringJsonElementEntry.getValue();

                    for (Value value : values) {
                        try {
                            value.fromJsonObject(valueObject);
                        } catch (Exception e) {
                            backupReasons.add("Error while applying 'values/" + stringJsonElementEntry.getKey() + "' " + e);
                        }
                    }
                }
            } else {
                backupReasons.add("'values' is not valid");
            }
            //</editor-fold>

            if (backupReasons.size() > 0) {

            }
        } catch (FileNotFoundException e) {

            Crispy.addChatMessage("ERROR: File not found");
            e.printStackTrace();
        }
    }




}
