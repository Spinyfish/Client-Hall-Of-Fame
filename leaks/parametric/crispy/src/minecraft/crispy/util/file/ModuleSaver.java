package crispy.util.file;


import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import crispy.Crispy;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.ConfigModule;
import net.minecraft.client.Minecraft;
import net.superblaubeere27.valuesystem.Value;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ModuleSaver {
    private final File clientDir = new File(Minecraft.getMinecraft().mcDataDir, "Crispy");
    private final File backupDir = new File(clientDir, "backups");
    private final File scriptsDir = new File(clientDir, "scripts");
    private final File saveFile = new File(clientDir, "client.json");

    @SuppressWarnings("UnstableApiUsage")
    public void save() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        clientDir.mkdirs();

        if (!saveFile.exists() && !saveFile.createNewFile())
            throw new IOException("Failed to create " + saveFile.getAbsolutePath());

        Files.write(toJsonObject().toString().getBytes(StandardCharsets.UTF_8), saveFile);
    }

    public void loadConfigs() {
        String[] pathNames;

        pathNames = clientDir.list();
        if(pathNames != null) {
            for (String iterate : pathNames) {
                if(iterate.endsWith(".json")) {
                    ConfigModule config = new ConfigModule(iterate.replace(".json", ""), "Config", Category.CONFIG);
                    Crispy.INSTANCE.getHackManager().addConfigModule(config);
                }
            }
        }
    }

    private JsonObject toJsonObject() {
        System.out.println("Saving settings");

        JsonObject obj = new JsonObject();


        {
            JsonObject metadata = new JsonObject();

            metadata.addProperty("clientVersion", Crispy.INSTANCE.getVersion());

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

    public void load() {
        if (!saveFile.exists()) return;

        List<String> backupReasons = new ArrayList<>();

        try {
            JsonObject object = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(saveFile)));

            //<editor-fold desc="metadata">
            if (object.has("metadata")) {
                JsonElement metadataElement = object.get("metadata");

                if (metadataElement instanceof JsonObject) {
                    JsonObject metadata = (JsonObject) metadataElement;

                    JsonElement clientVersion = metadata.get("clientVersion");

                    if (clientVersion != null && clientVersion.isJsonPrimitive() && ((JsonPrimitive) clientVersion).isNumber()) {
                        double version = clientVersion.getAsDouble();

                        if (version > Integer.parseInt(Crispy.INSTANCE.getVersion())) {
                            backupReasons.add("Version number of save file (" + version + ") is higher than " + version);
                        }
                        if (version < Integer.parseInt(Crispy.INSTANCE.getVersion())) {
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

                    if (module == null) {
                        backupReasons.add("Module '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (stringJsonElementEntry.getValue() instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject) stringJsonElementEntry.getValue();

                        JsonElement state = moduleObject.get("state");

                        if (state instanceof JsonPrimitive && ((JsonPrimitive) state).isBoolean()) {
                            module.setState(state.getAsBoolean());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/state' isn't valid");
                        }

                        JsonElement keybind = moduleObject.get("keybind");

                        if (keybind instanceof JsonPrimitive && ((JsonPrimitive) keybind).isNumber()) {
                            module.setKey(keybind.getAsInt());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/keybind' isn't valid");
                        }
                    } else {
                        backupReasons.add("Module object '" + stringJsonElementEntry.getKey() + "' isn't valid");
                    }
                }
            } else {
                backupReasons.add("'modules' object is not valid");
            }
            //</editor-fold>

            //<editor-fold desc="values">
            JsonElement valuesElement = object.get("values");

            if (valuesElement instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) valuesElement).entrySet()) {
                    List<Value> values = Crispy.INSTANCE.getValueManager().getAllValuesFrom(stringJsonElementEntry.getKey());

                    if (values == null) {
                        backupReasons.add("Value owner '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (!stringJsonElementEntry.getValue().isJsonObject()) {
                        backupReasons.add("'values/" + stringJsonElementEntry.getKey() + "' is not valid");
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
                backup(backupReasons);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void backup(List<String> backupReasons) {
        System.out.println("Creating backup " + backupReasons);

        try {
            backupDir.mkdirs();

            File out = new File(backupDir, "backup_" + System.currentTimeMillis() + ".zip");
            out.createNewFile();

            StringBuilder reason = new StringBuilder();

            for (String backupReason : backupReasons) {
                reason.append("- ").append(backupReason).append("\n");
            }

            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(out));

            outputStream.putNextEntry(new ZipEntry("client.json"));
            Files.copy(saveFile, outputStream);
            outputStream.closeEntry();

            outputStream.putNextEntry(new ZipEntry("reason.txt"));
            outputStream.write(reason.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.closeEntry();

            outputStream.close();
        } catch (Exception e) {
            System.out.println("Failed to backup");
            e.printStackTrace();
        }

    }

    public void loadScripts() {
        if (!scriptsDir.exists()) scriptsDir.mkdirs();

        File[] files = scriptsDir.listFiles(pathname -> pathname.getName().endsWith("zip") || pathname.getName().endsWith("cbs"));
        System.out.println("Loading scripts!");
        if (files != null) {
            for (File file : files) {
                try {
                    Crispy.INSTANCE.getScriptManager().load(file);
                } catch (Exception e) {
                    if (Minecraft.theWorld != null) {
                        Crispy.addChatMessage("Failed to load script " + file.getName());
                        Crispy.addChatMessage("Check logs for errors");
                    }
                    System.err.println("Failed to load script " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

}
