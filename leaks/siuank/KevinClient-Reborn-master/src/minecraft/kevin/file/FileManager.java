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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kevin.main.KevinClient;
import kevin.utils.ChatUtils;
import kevin.utils.MinecraftInstance;

import java.io.File;
import java.lang.reflect.Field;

public class FileManager extends MinecraftInstance {
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public final File dir = new File(mc.mcDataDir, KevinClient.INSTANCE.getName());
    public final File fontsDir = new File(dir, "Fonts");
    public final File spammerDir = new File(dir,"SpammerMessages");
    public final File capesDir = new File(dir,"Capes");
    public final File skinsDir = new File(dir,"Skins");
    public final File serverIconsDir = new File(dir,"ServerIcons");
    public final File configsDir = new File(dir,"Configs");
    public final File killMessages = new File(dir,"KillMessages");
    public final File playerModels = new File(dir,"PlayerModels");
    public final File scripts = new File(dir,"Scripts");
    public final File via = new File(dir,"Via");
    public final FileConfig modulesConfig = new ModulesConfig(new File(dir, "modules.json"));
    public final FileConfig hudConfig = new HudConfig(new File(dir, "hud.json"));
    public final FileConfig bindCommandConfig = new BindCommandConfig(new File(dir, "bindCommand.json"));
    public final File altsFile = new File(dir,"accounts.json");
    public final File adminNamesFile = new File(dir,"AdminNames.txt");

    public void load(){
        if (!dir.exists()) dir.mkdir();
        if (!fontsDir.exists()) fontsDir.mkdir();
        if (!spammerDir.exists()) spammerDir.mkdir();
        if (!capesDir.exists()) capesDir.mkdir();
        if (!skinsDir.exists()) skinsDir.mkdir();
        if (!serverIconsDir.exists()) serverIconsDir.mkdir();
        if (!configsDir.exists()) configsDir.mkdir();
        if (!killMessages.exists()) killMessages.mkdir();
        if (!playerModels.exists()) playerModels.mkdir();
        if (!scripts.exists()) scripts.mkdir();
        if (!via.exists()) via.mkdir();
    }

    public void saveConfig(final FileConfig config) {
        saveConfig(config, false);
    }
    private void saveConfig(final FileConfig config, final boolean ignoreStarting) {
        if (!ignoreStarting && KevinClient.INSTANCE.isStarting())
            return;

        try {
            if(!config.hasConfig())
                config.createConfig();
            config.saveConfig();
        }catch(final Throwable t) {
            ChatUtils.INSTANCE.messageWithStart("§cSaveConfig Error: " + t);
        }
    }
    public void loadConfigs(final FileConfig... configs) {
        for(final FileConfig fileConfig : configs)
            loadConfig(fileConfig);
    }
    public void loadConfig(final FileConfig config) {
        if(!config.hasConfig()) {
            saveConfig(config, true);
            return;
        }

        try {
            config.loadConfig();
        }catch(final Throwable t) {
            t.printStackTrace();
        }
    }
    public void saveAllConfigs() {
        for(final Field field : getClass().getDeclaredFields()) {
            if(field.getType() == FileConfig.class) {
                try {
                    if(!field.isAccessible())
                        field.setAccessible(true);

                    final FileConfig fileConfig = (FileConfig) field.get(this);
                    saveConfig(fileConfig);
                }catch(final IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
