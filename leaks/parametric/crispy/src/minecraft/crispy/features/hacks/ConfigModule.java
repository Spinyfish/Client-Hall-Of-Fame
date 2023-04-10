package crispy.features.hacks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import net.minecraft.client.Minecraft;
import net.superblaubeere27.valuesystem.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigModule extends Hack {
    private final File clientDir = new File(Minecraft.getMinecraft().mcDataDir,   "Crispy");

    File configfile;

    public ConfigModule(String name, String desc, Category category) {
        super(name, desc, category);
    }


    @Override
    public void onEnable() {
        onDisable();
        configfile = new File(clientDir, getName() + ".json");
        if(configfile.exists()) {
            Crispy.INSTANCE.getHackManager().getHacks().forEach(hack -> hack.setState(false));
        }
        load();
        NotificationPublisher.queue("Config Manager", "Loaded config " + getName(), NotificationType.SUCCESS, 3000);
        super.onEnable();
    }

    @Override
    public void onDisable() {

        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {

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

                        } else {

                        }
                    } else {

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
