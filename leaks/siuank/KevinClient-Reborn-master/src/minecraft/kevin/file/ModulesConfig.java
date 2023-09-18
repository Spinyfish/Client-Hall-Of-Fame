/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kevin.main.KevinClient;
import kevin.module.Module;
import kevin.module.Value;
import kotlin.Pair;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ModulesConfig extends FileConfig {

    public ModulesConfig(final File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

        if(jsonElement instanceof JsonNull)
            return;

        for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
            final Module module = KevinClient.moduleManager.getModuleByName(entry.getKey());

            if (module != null) {
                final JsonObject jsonModule = (JsonObject) entry.getValue();

                module.setState(jsonModule.get("State").getAsBoolean());
                module.setKeyBind(jsonModule.get("KeyBind").getAsInt());
                if (jsonModule.get("Hide")!=null) module.setArray(!jsonModule.get("Hide").getAsBoolean());
                if (jsonModule.get("AutoDisable")!=null) module.setAutoDisable(new Pair<>(!Objects.equals(jsonModule.get("AutoDisable").getAsString(), "Disable"), Objects.equals(jsonModule.get("AutoDisable").getAsString(), "Disable") ? "" : jsonModule.get("AutoDisable").getAsString()));
                for (final Value moduleValue : module.getValues()) {
                    final JsonElement element = jsonModule.get(moduleValue.getName());

                    if (element != null) moduleValue.fromJson(element);
                }
            }
        }
    }

    @Override
    protected void saveConfig() throws IOException {
        final JsonObject jsonObject = new JsonObject();

        for (final Module module : KevinClient.moduleManager.getModules()) {
            final JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("State", module.getState());
            jsonMod.addProperty("KeyBind", module.getKeyBind());
            jsonMod.addProperty("Hide", !module.getArray());
            jsonMod.addProperty("AutoDisable", module.getAutoDisable().getFirst() ? module.getAutoDisable().getSecond() : "Disable");
            module.getValues().forEach(value -> jsonMod.add(value.getName(), value.toJson()));
            jsonObject.add(module.getName(), jsonMod);
        }

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}
