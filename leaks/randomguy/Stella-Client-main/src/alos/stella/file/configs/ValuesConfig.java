package alos.stella.file.configs;

import com.google.gson.*;
import alos.stella.Stella;
import alos.stella.file.FileConfig;
import alos.stella.file.FileManager;
import alos.stella.module.Module;
import alos.stella.module.special.MacroManager;
import alos.stella.value.Value;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class ValuesConfig extends FileConfig {

    /**
     * Constructor of config
     *
     * @param file of config
     */
    public ValuesConfig(final File file) {
        super(file);
    }

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Override
    protected void loadConfig() throws IOException {
        final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

        if(jsonElement instanceof JsonNull)
            return;

        final JsonObject jsonObject = (JsonObject) jsonElement;

        final Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
        while(iterator.hasNext()) {
            final Map.Entry<String, JsonElement> entry = iterator.next();

            if (entry.getKey().equalsIgnoreCase("CommandPrefix")) {
                Stella.commandManager.setPrefix(entry.getValue().getAsCharacter());
            }  else if (entry.getKey().equalsIgnoreCase("macros")) {
                JsonArray jsonValue = entry.getValue().getAsJsonArray();
                for (final JsonElement macroElement : jsonValue) {
                    JsonObject macroObject = macroElement.getAsJsonObject();
                    JsonElement keyValue = macroObject.get("key");
                    JsonElement commandValue = macroObject.get("command");

                    MacroManager.INSTANCE.addMacro(keyValue.getAsInt(), commandValue.getAsString());
                }
            }  else {
                final Module module = Stella.moduleManager.getModule(entry.getKey());

                if(module != null) {
                    final JsonObject jsonModule = (JsonObject) entry.getValue();

                    for(final Value moduleValue : module.getValues()) {
                        final JsonElement element = jsonModule.get(moduleValue.getName());

                        if(element != null) moduleValue.fromJson(element);
                    }
                }
            }
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Override
    protected void saveConfig() throws IOException {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("CommandPrefix", Stella.commandManager.getPrefix());
//        jsonObject.addProperty("ShowRichPresence", Stella.clientRichPresence.getShowRichPresenceValue());
//
//        final JsonObject jsonTargets = new JsonObject();
//        jsonTargets.addProperty("TargetPlayer", EntityUtils.targetPlayer);
//        jsonTargets.addProperty("TargetMobs", EntityUtils.targetMobs);
//        jsonTargets.addProperty("TargetAnimals", EntityUtils.targetAnimals);
//        jsonTargets.addProperty("TargetInvisible", EntityUtils.targetInvisible);
//        jsonTargets.addProperty("TargetDead", EntityUtils.targetDead);
//        jsonObject.add("targets", jsonTargets);

        final JsonArray jsonMacros = new JsonArray();
        MacroManager.INSTANCE.getMacroMapping().forEach((k, v) -> {
            final JsonObject jsonMacro = new JsonObject();
            jsonMacro.addProperty("key", k);
            jsonMacro.addProperty("command", v);
            jsonMacros.add(jsonMacro);
        });
        jsonObject.add("macros", jsonMacros);

//        final JsonObject jsonFeatures = new JsonObject();
//        jsonFeatures.addProperty("DarkMode", Stella.INSTANCE.getDarkMode());
//        jsonFeatures.addProperty("AntiForge", AntiForge.enabled);
//        jsonFeatures.addProperty("AntiForgeFML", AntiForge.blockFML);
//        jsonFeatures.addProperty("AntiForgeProxy", AntiForge.blockProxyPacket);
//        jsonFeatures.addProperty("AntiForgePayloads", AntiForge.blockPayloadPackets);
//        jsonFeatures.addProperty("BungeeSpoof", BungeeCordSpoof.enabled);
//        jsonFeatures.addProperty("AutoReconnectDelay", AutoReconnect.INSTANCE.getDelay());
//        jsonObject.add("features", jsonFeatures);

//        final JsonObject theAlteningObject = new JsonObject();
//        theAlteningObject.addProperty("API-Key", GuiTheAltening.Companion.getApiKey());
//        jsonObject.add("thealtening", theAlteningObject);

//        final JsonObject backgroundObject = new JsonObject();
//        backgroundObject.addProperty("Enabled", GuiBackground.Companion.getEnabled());
//        backgroundObject.addProperty("Particles", GuiBackground.Companion.getParticles());
//        jsonObject.add("Background", backgroundObject);

        Stella.moduleManager.getModules().stream().filter(module -> !module.getValues().isEmpty()).forEach(module -> {
            final JsonObject jsonModule = new JsonObject();
            module.getValues().forEach(value -> jsonModule.add(value.getName(), value.toJson()));
            jsonObject.add(module.getName(), jsonModule);
        });

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}
