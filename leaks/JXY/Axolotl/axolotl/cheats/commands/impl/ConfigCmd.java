package axolotl.cheats.commands.impl;

import axolotl.Axolotl;
import axolotl.cheats.commands.Command;
import axolotl.cheats.config.Config;
import axolotl.cheats.config.JSONModule;
import axolotl.cheats.config.JSONSetting;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.Setting;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ConfigCmd extends Command {

    public ConfigCmd() {
        super("config", "Change cheat configuration");
    }

    @Override
    public String onCommand(String[] args, String message) {
        try {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("load")) {

                    if(args.length > 1) {

                        String config = args[1];

                        return Axolotl.INSTANCE.configManager.loadConfig(config);

                    }

                } else if (args[0].equalsIgnoreCase("save")) {

                    if (args.length > 1) {

                        String configName = message.replace(Axolotl.INSTANCE.cmdManager.settings.prefix + name + " " + args[0] + " ", "");

                        Config c = Axolotl.INSTANCE.configManager.createConfig(Axolotl.INSTANCE.moduleManager, args[1]);
                        try {
                            Axolotl.INSTANCE.configManager.saveConfig(c);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "Could not save config.";
                        }

                        return "Saved config \"" + configName + "\" to " + c.location;

                    } else {
                        return "Enter config name";
                    }
                } else {
                    return args[0] + " is not an option (Options are \"save\" and \"load\").";
                }
            } else {
                return "Enter a config to load into!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occured.";
        }
        return "An error occured.";
    }

}
