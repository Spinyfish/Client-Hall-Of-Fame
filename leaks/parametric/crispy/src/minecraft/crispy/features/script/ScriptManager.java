/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package crispy.features.script;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crispy.Crispy;
import crispy.features.hacks.Category;
import crispy.features.script.runtime.ScriptRuntime;
import crispy.util.player.KillAuraUtils;
import crispy.util.player.PlayerUtil;
import crispy.util.player.SpeedUtils;
import crispy.util.rotation.LookUtils;
import net.minecraft.client.Minecraft;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ScriptManager {
    private static final String moduleScriptHeader = "var rt = Java.type('" + ScriptRuntime.class.getCanonicalName() + "');\n";
    private static final String mc = "var minecraft = Java.type('" + Minecraft.class.getCanonicalName() + "');\n";
    private static final String speed = "var SpeedUtils = Java.type('" + SpeedUtils.class.getCanonicalName() + "');\n";
    private static final String look = "var LookUtils = Java.type('" + LookUtils.class.getCanonicalName() + "');\n";
    private static final String killaurautils = "var KillAuraUtils = Java.type('" + KillAuraUtils.class.getCanonicalName() + "');\n";
    private static final String player = "var PlayerUtils = Java.type('" + PlayerUtil.class.getCanonicalName() + "');\n";
    private static final String crispy = "var Crispy = Java.type('" + Crispy.class.getCanonicalName() + "');\n";
    private static final String minecraft = "var mc = minecraft.getMinecraft();\n";
    private ScriptEngine engine;
    public ScriptManager() {
        newScript();
    }

    public void newScript() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");

        if (engine == null) return;

        try {
            engine.eval(moduleScriptHeader + mc + speed + look + player + crispy + killaurautils);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public Object eval(String script) throws ScriptException {
        if (engine == null) return "Failed to initialize engine";

        return engine.eval(script);
    }

    public Script load(File scriptFile) {
        try {
            ZipFile zipFile = new ZipFile(scriptFile);

            ZipEntry manifestEntry = zipFile.getEntry("manifest.json");

            if (manifestEntry == null) throw new RuntimeException("There's no manifest file ('manifest.json')");

            JsonElement manifestElement = new JsonParser().parse(new InputStreamReader(zipFile.getInputStream(manifestEntry)));

            if (!manifestElement.isJsonObject()) throw new RuntimeException("Manifest is not valid");

            JsonObject manifest = manifestElement.getAsJsonObject();

            String scriptName;
            String scriptVersion;

            {
                if (!manifest.has("name")) throw new RuntimeException("There's no 'name' in the manifest");
                JsonElement element = manifest.get("name");

                if (element.isJsonPrimitive()) scriptName = element.getAsString();
                else throw new RuntimeException("'name' is invalid");

                if (!manifest.has("version")) throw new RuntimeException("There's no 'version' in the manifest");
                element = manifest.get("version");

                if (element.isJsonPrimitive()) scriptVersion = element.getAsString();
                else throw new RuntimeException("'version' is invalid");
            }

            Script script = new Script(scriptName, scriptVersion);

            if (manifest.has("modules")) {
                JsonElement element = manifest.getAsJsonArray("modules");
                JsonArray list = manifest.getAsJsonArray("list");
                if (!list.isJsonArray()) throw new RuntimeException("'modules' have to be an array");
                if (!element.isJsonArray()) throw new RuntimeException("'modules' have to be an array");

                int count = 0;
                for (JsonElement jsonElement : element.getAsJsonArray()) {

                    ScriptModule scriptModule = loadModule(jsonElement, zipFile, list.get(count).getAsString());
                    script.getModules().add(scriptModule);
                    count++;
                }
            }

            script.register();

            System.out.println("Successfully loaded " + script.getName() + " " + script.getVersion());

            return script;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open Zip file", ex);
        }
    }

    private ScriptModule loadModule(JsonElement jsonElement, ZipFile file, String member) {
        if (!jsonElement.isJsonObject()) throw new RuntimeException("A module have to be a json object");

        JsonObject no = jsonElement.getAsJsonObject();
        JsonObject obj = no.get(member).getAsJsonObject();

        String name;
        String desc;
        String cat;
        String scriptFile;
        System.out.println(obj.get("name").getAsString());
        //<editor-fold desc="Metadata">
        {
            if (!obj.has("name")) throw new RuntimeException("There's no 'name' in the module");
            JsonElement element = obj.get("name");

            if (element.isJsonPrimitive()) name = element.getAsString();
            else throw new RuntimeException("'name' is invalid");
        }
        {
            if (!obj.has("description"))
                throw new RuntimeException("There's no 'description' specified in '" + name + "'");
            JsonElement element = obj.get("description");

            if (element.isJsonPrimitive()) desc = element.getAsString();
            else throw new RuntimeException("'description' is invalid");
        }
        {
            if (!obj.has("category")) throw new RuntimeException("There's no 'category' specified in '" + name + "'");
            JsonElement element = obj.get("category");

            if (element.isJsonPrimitive()) cat = element.getAsString();
            else throw new RuntimeException("'category' is invalid");
        }
        {
            if (!obj.has("script")) throw new RuntimeException("There's no 'script' specified in '" + name + "'");
            JsonElement element = obj.get("script");

            if (element.isJsonPrimitive()) scriptFile = element.getAsString();
            else throw new RuntimeException("'script' is invalid");
        }
        //</editor-fold>

        Category category;

        try {
            category = Category.valueOf(cat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("There's no category called '" + cat + "'. Allowed Categories: " + Arrays.toString(Category.values()));
        }

        ScriptModule module = new ScriptModule(name, desc, category);

        ZipEntry entry = file.getEntry(scriptFile);

        if (entry == null) {
            throw new RuntimeException("There's no '" + scriptFile + "' in the script file");
        }
        String scriptContent;

        try {
            scriptContent = new String(ByteStreams.toByteArray(file.getInputStream(entry)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scriptContent = moduleScriptHeader + mc + speed + look + player + crispy + minecraft + killaurautils + scriptContent;

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

        try {
            scriptEngine.eval(scriptContent);
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to compile script", e);
        }

        module.setScriptEngine(scriptEngine);

        return module;
    }
}
